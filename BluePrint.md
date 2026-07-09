# TaskFlow — Architecture Blueprint
### A Spring Boot + Angular + H2 Task/Project Management System
*(Designed to scale later into a multi-tenant SaaS platform)*

---

## 1. What You're Building

**TaskFlow** is a Jira-lite task and project management app.

- Users sign up and log in
- Users create **Projects**
- Projects contain **Tasks**
- Tasks have status, priority, assignee, due date
- Tasks have **Comments** and **Tags**
- Project owners can add/remove **Members**

This domain is intentionally chosen because it already has the shape of a multi-tenant system (Organizations → Users → Projects → Tasks), so extending it later is a natural next step rather than a rewrite.

---

## 2. High-Level Architecture

```
┌─────────────────────────┐         REST/JSON        ┌──────────────────────────────┐
│        Angular SPA       │ ───────────────────────► │        Spring Boot API        │
│  (Components, Services,  │ ◄─────────────────────── │ Controller → Service → Repo   │
│   Guards, Interceptors)  │                           │        → Entity (JPA)         │
└─────────────────────────┘                           └──────────────┬────────────────┘
                                                                        │
                                                                        ▼
                                                                 ┌─────────────┐
                                                                 │   H2 (dev)  │
                                                                 │ Postgres    │
                                                                 │ (prod, later)│
                                                                 └─────────────┘
```

**Backend layering (strict one-direction dependency):**

```
Controller  →  Service (interface + impl)  →  Repository  →  Entity
     ↑               ↑
   DTO           Mapper (Entity <-> DTO)
```

Controllers never touch repositories directly. Services never return entities to controllers — always DTOs. This single rule is what makes multi-tenancy, caching, and testing painless later.

---

## 3. Backend Package Structure

```
com.taskflow
 ├── config/            → SecurityConfig, CorsConfig, OpenApiConfig, DataSeeder
 ├── security/           → JwtUtil, JwtAuthFilter, UserDetailsServiceImpl
 ├── entity/             → User, Role, Project, ProjectMember, Task, Comment, Tag
 ├── repository/         → Spring Data JPA interfaces
 ├── dto/
 │    ├── request/       → CreateTaskRequest, UpdateTaskRequest, LoginRequest...
 │    └── response/      → TaskResponse, ProjectResponse, UserResponse...
 ├── mapper/             → EntityMapper classes (or MapStruct)
 ├── service/
 │    ├── *.java         → Interfaces (UserService, ProjectService...)
 │    └── impl/          → Implementations
 ├── controller/         → AuthController, ProjectController, TaskController...
 ├── exception/          → GlobalExceptionHandler, custom exceptions
 └── TaskFlowApplication.java
```

---

## 4. Entities & Relationships

| Entity | Key Fields | Relationships |
|---|---|---|
| **User** | id, username, email, passwordHash, role (enum: ADMIN/USER), createdAt | 1–N owned Projects, M–N Project membership |
| **Project** | id, name, description, status, createdAt | N–1 owner (User), 1–N Task, M–N members (via ProjectMember) |
| **ProjectMember** | id, project, user, roleInProject (OWNER/EDITOR/VIEWER) | joins User ↔ Project |
| **Task** | id, title, description, status (enum: TODO/IN_PROGRESS/DONE), priority (enum: LOW/MED/HIGH), dueDate, createdAt, updatedAt | N–1 Project, N–1 assignee (User), 1–N Comment, M–N Tag |
| **Comment** | id, content, createdAt | N–1 Task, N–1 author (User) |
| **Tag** | id, name, colorHex | M–N Task |

**Why `ProjectMember` as its own entity instead of a plain `@ManyToMany`:** it lets you store metadata (role within the project) on the relationship — and structurally, it's identical to the `TenantMember` table you'd need for multi-tenancy later. Building this now means you're practicing the exact pattern you'll need to scale.

Entity design notes:
- Use `enum` types stored as `STRING` (not `ORDINAL`) to stay safe across schema changes.
- Add `createdAt`/`updatedAt` via `@PrePersist`/`@PreUpdate` or a shared `Auditable` base class — set this up once, reuse everywhere.
- Every entity gets a `Long id` with `@GeneratedValue(strategy = GenerationType.IDENTITY)`.

---

## 5. Services & Their Methods

### AuthService
- `register(RegisterRequest) → UserResponse`
- `login(LoginRequest) → AuthTokenResponse` (JWT)
- `getCurrentUser(Authentication) → UserResponse`

### UserService
- `findById(Long id)`
- `findByUsername(String username)`
- `updateProfile(Long id, UpdateProfileRequest)`
- `changePassword(Long id, ChangePasswordRequest)`
- `listUsers(Pageable)` — admin only

### ProjectService
- `createProject(CreateProjectRequest, User owner)`
- `getProjectById(Long id, User requester)` — throws `AccessDeniedException` if requester isn't a member
- `listProjectsForUser(User user, Pageable)`
- `updateProject(Long id, UpdateProjectRequest, User requester)`
- `deleteProject(Long id, User requester)`
- `addMember(Long projectId, Long userId, ProjectRole role, User requester)`
- `removeMember(Long projectId, Long userId, User requester)`
- `listMembers(Long projectId)`

### TaskService
- `createTask(Long projectId, CreateTaskRequest, User requester)`
- `getTaskById(Long id, User requester)`
- `listTasksByProject(Long projectId, TaskFilter filter, Pageable)`
- `updateTask(Long id, UpdateTaskRequest, User requester)`
- `updateStatus(Long id, TaskStatus newStatus, User requester)`
- `assignTask(Long id, Long userId, User requester)`
- `deleteTask(Long id, User requester)`

### CommentService
- `addComment(Long taskId, CreateCommentRequest, User author)`
- `listCommentsForTask(Long taskId, Pageable)`
- `deleteComment(Long id, User requester)`

### TagService
- `createTag(CreateTagRequest)`
- `listTags()`
- `attachTagToTask(Long taskId, Long tagId)`
- `removeTagFromTask(Long taskId, Long tagId)`

**Convention:** every service method that mutates data takes the `User requester` (from the authenticated principal) as its last argument, and checks authorization *inside the service*, not the controller. This keeps authorization logic centralized and testable, and is the same pattern you'll extend into tenant-scoping later.

---

## 6. REST API Surface (summary)

```
POST   /api/auth/register
POST   /api/auth/login
GET    /api/users/me

GET    /api/projects
POST   /api/projects
GET    /api/projects/{id}
PUT    /api/projects/{id}
DELETE /api/projects/{id}
POST   /api/projects/{id}/members
DELETE /api/projects/{id}/members/{userId}

GET    /api/projects/{id}/tasks
POST   /api/projects/{id}/tasks
GET    /api/tasks/{id}
PUT    /api/tasks/{id}
PATCH  /api/tasks/{id}/status
PATCH  /api/tasks/{id}/assignee
DELETE /api/tasks/{id}

GET    /api/tasks/{id}/comments
POST   /api/tasks/{id}/comments
DELETE /api/comments/{id}

GET    /api/tags
POST   /api/tags
POST   /api/tasks/{id}/tags/{tagId}
DELETE /api/tasks/{id}/tags/{tagId}
```

Use `@Valid` on all request DTOs, return `201 Created` with `Location` header on creation, and paginate every list endpoint (`Pageable` from Spring Data, `?page=&size=&sort=`).

---

## 7. Cross-Cutting Backend Concerns

| Concern | Approach |
|---|---|
| **Security** | Spring Security + JWT (stateless). `JwtAuthFilter` reads the token, sets `SecurityContext`. Roles enforced via `@PreAuthorize("hasRole('ADMIN')")`. |
| **Validation** | `jakarta.validation` annotations on DTOs (`@NotBlank`, `@Size`, `@Future` for due dates). |
| **Error handling** | One `@ControllerAdvice` `GlobalExceptionHandler` mapping exceptions → consistent JSON error shape `{timestamp, status, message, path}`. |
| **DTO mapping** | Manual mapper classes to start (clearer to learn); swap to MapStruct once comfortable. |
| **DB** | H2 in-memory for dev (`application-dev.yml`), profile-based config so swapping to Postgres later is a one-file change. |
| **API docs** | springdoc-openapi → Swagger UI at `/swagger-ui.html`. |
| **Testing** | `@DataJpaTest` for repositories, `@WebMvcTest` for controllers with mocked services, `@SpringBootTest` for a couple of end-to-end flows. |

---

## 8. Angular Frontend Structure

```
src/app
 ├── core/
 │    ├── guards/        → auth.guard.ts
 │    ├── interceptors/  → jwt.interceptor.ts, error.interceptor.ts
 │    └── models/        → task.model.ts, project.model.ts, user.model.ts
 ├── shared/              → reusable UI components (status-badge, priority-tag, pagination)
 ├── features/
 │    ├── auth/           → login, register
 │    ├── dashboard/      → project list/overview
 │    ├── projects/       → project detail, member management
 │    ├── tasks/          → task board/list, task detail, comments
 │    └── profile/
 ├── services/            → auth.service.ts, project.service.ts, task.service.ts (HttpClient wrappers)
 └── app.routes.ts        → lazy-loaded feature routes, guarded by auth.guard
```

- Use **standalone components** (current Angular default) rather than NgModules — simpler for an intermediate project.
- State: start with simple `BehaviorSubject`-backed services per feature. Only reach for NgRx if the app's state genuinely gets tangled — don't add it preemptively.
- `jwt.interceptor.ts` attaches the token to every request; `error.interceptor.ts` catches 401s and redirects to login.
- Reactive Forms (`FormGroup`/`FormBuilder`) for all create/edit forms, with validators mirroring backend constraints.

---

## 9. Suggested Build Order

1. Entities + repositories + H2 config — confirm schema with `spring.jpa.hibernate.ddl-auto=update` and H2 console.
2. Security: register/login, JWT issuing & filter, password hashing (BCrypt).
3. Project CRUD + membership.
4. Task CRUD + status/assignee updates + filtering/pagination.
5. Comments + Tags.
6. Global exception handling + validation polish.
7. Angular: auth flow (login/register/guard/interceptor).
8. Angular: project list/detail, task board, comments UI.
9. Swagger docs, basic tests, README.

---

## 10. Scaling to Multi-Tenancy (Phase 2)

Once the single-tenant app works end-to-end, here's the upgrade path — planned now so nothing you build in Phase 1 has to be thrown away.

### Step 1 — Introduce `Organization` as the tenant boundary
Add:
```
Organization { id, name, createdAt }
```
Give every `User` an `organizationId`, and every `Project` an `organizationId` (denormalized even though it's reachable via owner, for fast filtering). This single addition is 90% of the migration — everything else is enforcement.

### Step 2 — Choose a tenancy strategy
| Strategy | Description | When to use |
|---|---|---|
| **Shared DB, shared schema, discriminator column** | One `organization_id` column on every table; every query filters by it | Simplest, good for most SaaS at moderate scale — **recommended starting point** |
| **Shared DB, separate schema per tenant** | Same DB, `schema-per-tenant` | Medium isolation, more ops overhead |
| **Separate DB per tenant** | Full isolation | Enterprise/compliance-heavy customers only |

Start with the discriminator-column approach — it's the natural extension of what you already built.

### Step 3 — Enforce tenant scoping automatically
- Put the current tenant id in a `ThreadLocal` via a `TenantContext` class, populated by a `TenantInterceptor`/filter that reads it from the JWT claim (add `organizationId` to the JWT payload at login).
- Use a **Hibernate `@Filter`** (`@FilterDef`/`@Filter(name="tenantFilter", condition="organization_id = :tenantId")`) enabled per-session from that `ThreadLocal`, so you don't have to remember to add `.where(organizationId = ...)` to every repository query by hand.
- Alternative if you want it more explicit/less "magic": add `organizationId` as a required parameter to every repository/service method now (even in Phase 1, hardcode it to a single default org) — then it's already threaded through when you add real multi-tenancy.

### Step 4 — Frontend changes
- JWT already carries `organizationId` — no major Angular changes needed, just surface an org switcher if you support users belonging to multiple orgs.

### Step 5 — Further "advanced stuff" once multi-tenancy is in place
- **Caching**: Redis for session/lookup caching, tenant-aware cache keys.
- **Async/events**: RabbitMQ or Kafka for notifications (e.g., "task assigned" emails) — decouples side effects from request/response.
- **File storage**: move task attachments to S3-compatible storage instead of local disk.
- **Search**: Elasticsearch for full-text task/project search at scale.
- **Observability**: structured logging + Micrometer/Prometheus + Grafana dashboards.
- **Containerization & CI/CD**: Dockerfile + docker-compose (app + Postgres + Redis), GitHub Actions pipeline.
- **DB migration**: switch `ddl-auto=update` → Flyway/Liquibase migrations once the schema stabilizes (do this *before* multi-tenancy changes, not after).

---

## 11. The One Design Habit That Makes All of This Easy

Keep controllers thin, keep authorization logic inside services, and never leak JPA entities past the service layer. If you hold that line throughout Phase 1, adding a `WHERE organization_id = ?` everywhere in Phase 2 becomes a mechanical, low-risk change — not a redesign.