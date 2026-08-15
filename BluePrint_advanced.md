# TaskFlow — Advanced Blueprint (Phase 2.5)
### Caching, Async/Events, File Storage, Search, Observability & CI/CD
*(Built on top of: Spring Boot + Angular + schema-per-tenant multitenancy + Liquibase + JWT-embedded tenantId)*

---

## 0. Assumptions Going In

This document assumes you've already done the harder-than-average version of Phase 2:

- **Schema-per-tenant**, not discriminator column — each tenant's tables live in their own Postgres schema.
- **`tenantId` is embedded in the JWT claim**, resolved once per request — no `X-Tenant-Id` header floating around.
- **Liquibase** is already running your migrations (this matters a lot below — schema-per-tenant means Liquibase has to run *per schema*, not once).

Everything below is written against that reality, not the discriminator-column version in the original blueprint.

---

## 1. High-Level Architecture (Updated)

```
┌─────────────┐      REST/JSON     ┌────────────────────────────┐
│ Angular SPA │ ─────────────────► │        Spring Boot API       │
└─────────────┘ ◄───────────────── │  TenantResolvingFilter       │
                                    │  (reads tenantId from JWT,   │
                                    │   sets DataSource schema)    │
                                    └───────────┬──────────────────┘
                                                │
                    ┌───────────────────────────┼───────────────────────────┐
                    ▼                           ▼                           ▼
            ┌───────────────┐          ┌────────────────┐          ┌───────────────┐
            │  Postgres      │          │     Redis       │          │  RabbitMQ/     │
            │ schema_tenantA │          │ (tenant-scoped   │          │  Kafka         │
            │ schema_tenantB │          │  cache keys)     │          │ (events)       │
            └───────────────┘          └────────────────┘          └───────┬────────┘
                                                                            ▼
                                                                   ┌─────────────────┐
                                                                   │  Async workers    │
                                                                   │  (email, indexing,│
                                                                   │   S3 uploads)     │
                                                                   └─────────────────┘
```

---

## 2. Caching (Redis)

Schema-per-tenant changes the caching problem: you're not filtering rows by `organization_id`, you're pointing at a different schema per request. The cache has to mirror that separation or you'll leak Tenant A's data into Tenant B's cache hit.

**Key convention — always prefix with the schema name, not a generic tenant id:**
```
{schemaName}:project:{projectId}
{schemaName}:user:{userId}
{schemaName}:task-list:{projectId}:page-{n}
```

**What to cache first (highest value, lowest risk):**
- `GET /api/projects/{id}` and `GET /api/tasks/{id}` — read-heavy, low mutation frequency.
- `listMembers` / `listTags` — small, stable-ish lists, high read count.
- Do **not** cache paginated task lists at first — invalidation gets messy fast (status changes, reassignment, comments). Add it only once single-entity caching is solid.

**Config:**
- `spring-boot-starter-data-redis` + `@EnableCaching`.
- Custom `CacheKeyGenerator` that pulls the current schema from your existing `TenantContext` (the same ThreadLocal your Hibernate/DataSource routing already uses) and prepends it automatically — this way nobody has to remember the prefix by hand.
- TTL, not just eviction-on-write, as a safety net (e.g. 10 min) — belt and suspenders against a missed `@CacheEvict`.
- `@CacheEvict` on every `update*`/`delete*` service method touching a cached entity. Put this in the service layer, same place your authorization checks live — one more reason "logic lives in services" pays off.

---

## 3. Async / Events (RabbitMQ or Kafka)

Use this for anything that shouldn't block the request/response cycle: "task assigned" emails, activity-feed entries, search index updates.

**Recommendation: start with RabbitMQ, not Kafka.** Kafka's value (replay, partition-ordered streams, huge throughput) doesn't pay off at your current scale, and RabbitMQ's routing model (exchanges/queues) maps more directly onto "an event happened, a few things need to react to it." Revisit Kafka only if you outgrow single-consumer-group semantics.

**Event shape — since you're schema-per-tenant, the schema name has to travel with the event, not just the entity id:**
```json
{
  "eventType": "TASK_ASSIGNED",
  "tenantSchema": "schema_acme",
  "taskId": 42,
  "assigneeId": 7,
  "occurredAt": "2026-08-14T10:15:00Z"
}
```

**Consumers must re-establish tenant context from the event payload before touching the DB** — there's no JWT in a background worker, so your existing `TenantContext.set(schema)` needs a second entry point that isn't "read it off the request."

**First three events worth wiring:**
1. `TASK_ASSIGNED` → email notification.
2. `TASK_STATUS_CHANGED` → activity feed row.
3. `COMMENT_ADDED` → email/notification to task watchers.

Package placement: `com.taskflow.event.publisher` / `com.taskflow.event.listener`, kept separate from `service/` — publishing an event is a side effect of a service method, not the method's core job.

---

## 4. File Storage (S3-Compatible)

For task attachments. Use MinIO locally (S3-compatible, runs in Docker) so your dev environment doesn't need real AWS credentials; swap to real S3 via config profile in prod — same pattern you already used for H2 → Postgres.

**Design:**
- New entity: `Attachment { id, task, uploader, fileName, s3Key, contentType, sizeBytes, uploadedAt }`.
- `s3Key` convention: `{schemaName}/{taskId}/{uuid}-{fileName}` — again, tenant isolation baked into the key, not just the DB row.
- Upload flow: client requests a **pre-signed upload URL** from your API (`POST /api/tasks/{id}/attachments/presign`), uploads directly to S3/MinIO, then confirms with your API to create the `Attachment` row. Don't proxy file bytes through your Spring app — it doesn't scale and ties up request threads.
- Download the same way: pre-signed GET URL, short expiry (e.g. 5 min).

---

## 5. Search (Elasticsearch)

Full-text search across task titles/descriptions/comments, per tenant.

**Index-per-tenant, mirroring your schema-per-tenant DB choice** — keeps the isolation model consistent instead of introducing a second, different multitenancy strategy at the search layer:
```
tasks_schema_acme
tasks_schema_globex
```

**Sync strategy:** don't call Elasticsearch synchronously inside `TaskService.createTask`/`updateTask`. Publish a `TASK_INDEXED` event (same RabbitMQ setup as Section 3) and let a dedicated consumer write to ES. This keeps your write path fast and keeps ES as an eventually-consistent read model, not a dependency your core CRUD can fail on.

**Start narrow:** index `title`, `description`, `tags`, `status`. Resist indexing comments on day one — it's the highest-volume, lowest-immediate-value data to search.

---

## 6. Observability

| Layer | Tool | Notes |
|---|---|---|
| Structured logging | Logback + `logstash-logback-encoder` (JSON output) | Always include `tenantSchema` and `requestId` as MDC fields — this is your #1 debugging tool once you have real tenants, since "which tenant's data caused this 500" is the first question you'll ask. |
| Metrics | Micrometer → Prometheus | Track request latency, cache hit/miss ratio, queue depth, per-tenant DB connection pool usage (schema-per-tenant means connection pooling deserves its own dashboard — you'll want to catch one noisy tenant starving the pool). |
| Dashboards | Grafana | One dashboard for "system health," one for "per-tenant activity" once you have >2 real tenants. |
| Tracing | Micrometer Tracing + Zipkin/OTel (optional, add later) | Most valuable once requests fan out across the async workers in Section 3 — trace an event from HTTP request → queue → consumer. |

---

## 7. Containerization & CI/CD

**`docker-compose.yml` services:** `app`, `postgres`, `redis`, `rabbitmq`, `minio`, and optionally `elasticsearch` + `zipkin` behind a profile flag so local dev doesn't have to boot everything every time.

**Liquibase note specific to your setup:** with schema-per-tenant, "run migrations" means "run Liquibase once per existing tenant schema, plus once for the template used when provisioning a new tenant." Write a small `TenantMigrationRunner` that loops over known schemas at startup (or on-demand when a new tenant is provisioned) rather than relying on Liquibase's default single-schema run.

**GitHub Actions pipeline (minimum viable):**
1. Build + unit tests (`@DataJpaTest`, `@WebMvcTest`) on every PR.
2. Spin up Postgres + Redis + RabbitMQ as service containers, run `@SpringBootTest` integration tests against them.
3. Build Docker image, push to a registry on merge to `main`.
4. (Later) deploy step once you have a real target environment.

---

## 8. Suggested Build Order

1. Docker Compose for Postgres/Redis/RabbitMQ/MinIO — get the whole stack runnable locally in one command.
2. Redis caching on single-entity reads (`getProjectById`, `getTaskById`) — smallest, safest win.
3. RabbitMQ + the three events in Section 3, with `TASK_ASSIGNED` → email as the first end-to-end slice.
4. `TenantMigrationRunner` for Liquibase-per-schema, and a real "provision new tenant" flow that creates the schema + runs migrations + seeds an owner user.
5. Attachments via MinIO/S3 (pre-signed URLs).
6. Elasticsearch, index-per-tenant, populated via the event pipeline you already built in step 3.
7. Structured logging with tenant/request MDC fields.
8. Micrometer + Prometheus + Grafana.
9. CI pipeline, then containerized deploy.

---

## 9. The One Design Habit That Makes This Easy

Same rule as Phase 1, extended: **every cross-cutting resource — cache key, S3 key, search index, log field — carries the tenant schema explicitly, not implicitly.** You already made the hard call (schema-per-tenant over discriminator column); the payoff is that "which tenant does this belong to" is never a query you have to guess at, it's a prefix you can `grep` for. Keep that discipline in every new subsystem you bolt on, and none of this becomes a rewrite either.

---

## 10. Phase 3 — What Comes After This

Once caching, events, storage, search, observability, and CI/CD are all in and stable, here's the natural next horizon — not because you need it yet, but so nothing above forecloses it:

- **Read replicas / connection pool tuning per tenant** — once one tenant's query load can affect another's latency, this is the next isolation lever (still short of separate DBs).
- **Tenant provisioning as a self-serve flow** — turn the `TenantMigrationRunner` from Section 7 into an actual "sign up → new org → new schema" API path, with rollback if provisioning fails partway.
- **API gateway / BFF layer** — once you have more than one frontend client (mobile app, admin panel), a thin gateway in front of the Spring API avoids duplicating auth/rate-limiting logic per client.
- **Feature flags** (e.g. Unleash or a simple DB-backed flag table) — useful once you're rolling features out to some tenants before others.
- **Load testing & SLOs** (k6 or Gatling against a staging environment) — turn "it feels fast" into a number you can regress-test in CI.
- **Cost/observability per tenant** — once tenants are paying customers, being able to answer "what does tenant X cost us in compute/storage" becomes a real business question, not just an engineering nicety.
- **Selective service extraction** — *only* if a specific piece (e.g. search indexing, or the notification worker) genuinely needs independent scaling or deploy cadence. Don't decompose into microservices preemptively — the layered monolith you built is doing its job.

Treat this section the way you treated Section 10 of the original blueprint: a map, not a to-do list. Build it when a real need shows up, not because the list exists.