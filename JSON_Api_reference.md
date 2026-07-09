# TaskFlow — API Request/Response Reference

Quick reference for actual request and response shapes seen while building/testing the API. Use this alongside the architecture blueprint.

---

## Users

### `GET /api/users/me`
Returns the currently authenticated user.

**Response**
```json
{
  "id": 2,
  "username": "User",
  "email": "user@mail.com",
  "role": "ADMIN",
  "passwordHash": "123123",
  "createdAt": null
}
```

⚠️ **Note:** `passwordHash` is being returned in the response here. This should never be exposed by the API — it needs a dedicated `UserResponse` DTO that excludes it. Also `createdAt` is coming back `null`, which means it isn't being set on creation (check the `@PrePersist` logic).

---

## Projects

### `POST /api/projects`
Create a project.

**Request**
```json
{
  "name": "TaskFlow Backend",
  "description": "Spring Boot project management system",
  "status": "TODO",
  "createdAt": "2026-07-08T15:30:00",
  "user": {
    "id": 2,
    "username": "User",
    "passwordHash": "123123",
    "email": "user@mail.com",
    "role": "ADMIN"
  }
}
```

⚠️ **Note:** The client shouldn't be sending `createdAt` or a full nested `user` object (including `passwordHash`) in the request. A `CreateProjectRequest` DTO should only accept `name`, `description`, and `status` — the owner should be derived from the authenticated principal server-side, and `createdAt` should be set automatically. Sending the password hash from the frontend is a security smell worth fixing early.

### `POST /api/projects/{id}/members`
Add or update a member's role on a project.

**Request** — raw string, not an object
```json
"EDITOR"
```

📝 This currently just accepts a bare role string in the body. Once you add a `userId` parameter, this becomes: *which user, what role* — e.g. `{ "userId": 5, "role": "EDITOR" }`. Worth revisiting once you wire up the membership endpoint fully — right now it's unclear which user this role applies to.

---

## Tasks

### `POST /api/projects/{id}/tasks`
Create a task under a project.

**Request**
```json
{
  "title": "TaskFlow Backend",
  "description": "Spring Boot project management system",
  "status": "TODO",
  "createdAt": "2026-07-08T15:30:00",
  "assignee": {
    "id": 2,
    "username": "User",
    "passwordHash": "123123",
    "email": "user@mail.com",
    "role": "ADMIN"
  },
  "project": {
    "id": 3,
    "name": "TaskFlow Backend Part 2",
    "description": "Spring Boot project management system",
    "status": "TODO",
    "createdAt": null,
    "user": {
      "id": 2,
      "username": "User",
      "email": "user@mail.com",
      "role": "ADMIN",
      "createdAt": null
    }
  }
}
```

**Response**
```json
{
  "id": 2,
  "title": "1st Task",
  "description": "Be happy your time has come",
  "status": "TODO",
  "priority": null,
  "dueDate": null,
  "createdAt": "2026-07-08T16:25:33.074Z",
  "updatedAt": "2026-07-08T16:25:33.074Z",
  "assignee": {
    "id": 5,
    "username": "Abhay",
    "email": "abhay@mail.com",
    "role": "ADMIN",
    "createdAt": null
  },
  "project": {
    "id": 3,
    "name": "TaskFlow Backend Part 2",
    "description": "Spring Boot project management system",
    "status": "TODO",
    "createdAt": null,
    "user": {
      "id": 2,
      "username": "User",
      "email": "user@mail.com",
      "role": "ADMIN",
      "createdAt": null
    }
  },
  "comments": [],
  "tags": []
}
```

⚠️ **Notes:**
- Same issue as `/api/projects` — the request shouldn't require a full nested `assignee` or `project` object. It should just take `assigneeId` (a plain number) — the `projectId` is already in the URL path, so it shouldn't be repeated in the body at all.
- **The response should be an array** when listing tasks (e.g. `GET /api/projects/{id}/tasks`) — a single object is only correct for a *create* (`POST`) or *get-by-id* response. Keep this distinction consistent: `POST`/`GET .../{id}` → single object, `GET` (list) → array (ideally wrapped in a `Page` object with pagination metadata).
- Field casing inconsistency: `"CreatedAt"` (capital C) appears in some nested project objects while everywhere else it's `"createdAt"`. This is a Java field-naming bug (likely a typo in the entity or DTO) — fix so JSON keys are consistent.

### `PATCH /api/tasks/{id}/status`
Update a task's status only.

**Request** — raw string, not an object
```json
"IN_PROGRESS"
```

📝 Simple and fine for a single-field update. Just make sure the controller reads this as a raw string body (`@RequestBody String status`) or wraps it as `{ "status": "IN_PROGRESS" }` — pick one convention and use it consistently across all your "just update one field" endpoints (assignee update should probably match this same style).

---

## Comments

### `GET /api/tasks/{id}/comments`
List comments for a task.

**Response** *(should be an array — see note below)*
```json
{
  "id": null,
  "name": "1st Comment",
  "content": "1st comments content",
  "commentator": {
    "id": 5,
    "username": "Abhay",
    "email": "abhay@mail.com",
    "role": "ADMIN",
    "createdAt": null
  },
  "task": {
    "id": 3,
    "title": "1st Task",
    "description": "Be happy your time has come",
    "status": "TODO",
    "priority": null,
    "dueDate": null,
    "createdAt": "2026-07-09T07:00:20.390Z",
    "updatedAt": "2026-07-09T07:00:20.390Z",
    "assignee": {
      "id": 2,
      "username": "User",
      "email": "user@mail.com",
      "role": "ADMIN",
      "createdAt": null
    },
    "project": {
      "id": 3,
      "name": "TaskFlow Backend Part 2",
      "description": "Spring Boot project management system",
      "status": "TODO",
      "createdAt": null,
      "user": {
        "id": 2,
        "username": "User",
        "email": "user@mail.com",
        "role": "ADMIN",
        "createdAt": null
      }
    },
    "comments": [],
    "tags": []
  }
}
```

⚠️ **Notes:**
- This is a *list* endpoint but the sample response is a single object — it needs to return an array of comments, each shaped like this.
- Field name mismatch: the blueprint's `Comment` entity uses `content` + `author`, but this response has both `name` **and** `content`, and `commentator` instead of `author`. Pick one naming convention and make it consistent across entity, DTO, and API — right now `name` looks like leftover/unused field.
- Returning the **full nested `task` object inside every comment** (which itself re-embeds `project`, `assignee`, etc.) is expensive and usually unnecessary — a comment response typically only needs `taskId` (not the whole task), especially since you're already fetching comments *from* a task endpoint. Embedding deeply nested objects everywhere will get expensive fast and is a common source of circular-reference bugs (`Task → comments → Task → comments...`). This is the clearest sign in this whole file that you're returning entities directly instead of mapping to response DTOs — worth fixing before it compounds.

---

## Recurring Issues to Fix Across All Endpoints

1. **Entities are being returned/accepted directly instead of DTOs.** This causes: password hashes leaking, deeply nested circular objects, and requests that require sending full nested objects when only an ID is needed. This is the single highest-value fix — build `*Request` and `*Response` DTOs for every entity and never let `@Entity` classes appear in a controller method signature.
2. **IDs in the URL shouldn't be repeated in the body.** `POST /api/projects/{id}/tasks` already has the project ID in the path — the body shouldn't also need a full `project` object.
3. **List endpoints must return arrays** (or a paginated wrapper), not single objects.
4. **Field naming consistency** — `CreatedAt` vs `createdAt`, `name` vs `content`, `commentator` vs `author`. Lock these down in the entity classes once, and every layer above should follow it.
5. **`createdAt` fields coming back `null`** in several responses — the `@PrePersist` auto-timestamp logic isn't wired up yet, or it's being overwritten by a client-supplied value.