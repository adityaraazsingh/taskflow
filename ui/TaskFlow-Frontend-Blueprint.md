# TaskFlow — Frontend Blueprint
### Angular Architecture for the TaskFlow SPA
*(Companion to `BluePrint.md` — read that first for entities/API surface)*

---

## 1. Project Setup

```bash
ng new taskflow-frontend --standalone --routing --style=scss
cd taskflow-frontend
ng add @angular/material   # or Tailwind — pick one, don't mix both frameworks
```

- Use **standalone components** (no `NgModules`) — current Angular default, simpler for an intermediate project.
- `environment.ts` / `environment.prod.ts` hold the API base URL:
```ts
// environment.ts
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api'
};
```
Every service reads from `environment.apiUrl` — never hardcode the backend URL in a component or service body.

---

## 2. Folder Structure

```
src/app
 ├── core/
 │    ├── guards/
 │    │    └── auth.guard.ts
 │    ├── interceptors/
 │    │    ├── jwt.interceptor.ts
 │    │    └── error.interceptor.ts
 │    ├── models/
 │    │    ├── user.model.ts
 │    │    ├── project.model.ts
 │    │    ├── task.model.ts
 │    │    ├── comment.model.ts
 │    │    ├── tag.model.ts
 │    │    └── auth.model.ts
 │    └── services/
 │         ├── auth.service.ts
 │         ├── user.service.ts
 │         ├── project.service.ts
 │         ├── task.service.ts
 │         ├── comment.service.ts
 │         └── tag.service.ts
 ├── shared/
 │    ├── components/
 │    │    ├── status-badge/
 │    │    ├── priority-tag/
 │    │    ├── pagination/
 │    │    ├── confirm-dialog/
 │    │    └── loading-spinner/
 │    └── pipes/
 │         └── relative-time.pipe.ts
 ├── features/
 │    ├── auth/
 │    │    ├── login/
 │    │    └── register/
 │    ├── dashboard/
 │    │    └── dashboard.component.ts        (project list/overview)
 │    ├── projects/
 │    │    ├── project-list/
 │    │    ├── project-detail/
 │    │    ├── project-form/                  (create/edit)
 │    │    └── project-members/
 │    ├── tasks/
 │    │    ├── task-board/                    (kanban-style TODO/IN_PROGRESS/DONE columns)
 │    │    ├── task-detail/
 │    │    ├── task-form/                      (create/edit)
 │    │    └── comment-list/                   (nested under task-detail)
 │    └── profile/
 │         └── profile.component.ts
 ├── app.routes.ts
 ├── app.config.ts        → providers: HttpClient, interceptors, router
 └── app.component.ts     → root shell (nav bar + <router-outlet>)
```

---

## 3. TypeScript Models (mirror the backend DTOs — not the entities)

This matters: your Angular models should match the backend's **Response DTOs**, not the raw JPA entities. Based on the API reference doc, make sure your backend actually returns these shapes (fix the entity-leak issues first).

```ts
// user.model.ts
export interface User {
  id: number;
  username: string;
  email: string;
  role: 'ADMIN' | 'USER';
  createdAt: string | null;
}

// project.model.ts
export interface Project {
  id: number;
  name: string;
  description: string;
  status: 'TODO' | 'ACTIVE' | 'ARCHIVED' | 'COMPLETED';
  createdAt: string;
  owner: User;
}

export interface ProjectMember {
  id: number;
  user: User;
  roleInProject: 'OWNER' | 'EDITOR' | 'VIEWER';
}

// task.model.ts
export interface Task {
  id: number;
  title: string;
  description: string;
  status: 'TODO' | 'IN_PROGRESS' | 'DONE';
  priority: 'LOW' | 'MED' | 'HIGH' | null;
  dueDate: string | null;
  createdAt: string;
  updatedAt: string;
  assignee: User | null;
  projectId: number;      // just the id — not a nested project object
  tags: Tag[];
}

// comment.model.ts
export interface Comment {
  id: number;
  content: string;
  createdAt: string;
  author: User;
  taskId: number;          // just the id — not a nested task object
}

// tag.model.ts
export interface Tag {
  id: number;
  name: string;
  colorHex: string;
}

// auth.model.ts
export interface AuthResponse {
  token: string;
  user: User;
}
export interface LoginRequest { username: string; password: string; }
export interface RegisterRequest { username: string; email: string; password: string; }
```

Keep **separate `*Request` interfaces** for what you send (e.g. `CreateTaskRequest` with just `title`, `description`, `assigneeId`) versus what you receive (`Task`, with the full nested `assignee` object). This avoids the exact request/response mismatch problems seen in the API reference doc.

---

## 4. Core Services (HTTP layer)

Each service is a thin wrapper around `HttpClient` — no business logic, just API calls returning `Observable`s.

### `AuthService`
- `register(req: RegisterRequest): Observable<AuthResponse>`
- `login(req: LoginRequest): Observable<AuthResponse>`
- `logout(): void` — clears stored token, redirects to `/login`
- `getToken(): string | null`
- `isLoggedIn(): boolean`
- `currentUser$: BehaviorSubject<User | null>` — exposes the logged-in user reactively to the whole app (e.g., for the nav bar)

### `UserService`
- `getMe(): Observable<User>`
- `updateProfile(req: UpdateProfileRequest): Observable<User>`
- `changePassword(req: ChangePasswordRequest): Observable<void>`

### `ProjectService`
- `list(page: number, size: number): Observable<Page<Project>>`
- `getById(id: number): Observable<Project>`
- `create(req: CreateProjectRequest): Observable<Project>`
- `update(id: number, req: UpdateProjectRequest): Observable<Project>`
- `delete(id: number): Observable<void>`
- `listMembers(id: number): Observable<ProjectMember[]>`
- `addMember(id: number, userId: number, role: string): Observable<ProjectMember>`
- `removeMember(id: number, userId: number): Observable<void>`

### `TaskService`
- `listByProject(projectId: number, filter?: TaskFilter, page?: number): Observable<Page<Task>>`
- `getById(id: number): Observable<Task>`
- `create(projectId: number, req: CreateTaskRequest): Observable<Task>`
- `update(id: number, req: UpdateTaskRequest): Observable<Task>`
- `updateStatus(id: number, status: string): Observable<Task>`
- `assign(id: number, userId: number): Observable<Task>`
- `delete(id: number): Observable<void>`

### `CommentService`
- `listForTask(taskId: number, page?: number): Observable<Page<Comment>>`
- `add(taskId: number, content: string): Observable<Comment>`
- `delete(id: number): Observable<void>`

### `TagService`
- `list(): Observable<Tag[]>`
- `create(req: CreateTagRequest): Observable<Tag>`
- `attach(taskId: number, tagId: number): Observable<void>`
- `remove(taskId: number, tagId: number): Observable<void>`

**Convention:** every service method returns an `Observable` and does nothing else — no subscribing inside the service, no state mutation. Components/state-services subscribe and decide what to do with the result. This keeps services trivially testable with `HttpClientTestingModule`.

---

## 5. State Management (keep it simple)

Don't reach for NgRx yet. Use one lightweight **state service per feature**, backed by `BehaviorSubject`, sitting between the components and the HTTP services:

```ts
// projects/project-state.service.ts
@Injectable({ providedIn: 'root' })
export class ProjectStateService {
  private projectsSubject = new BehaviorSubject<Project[]>([]);
  projects$ = this.projectsSubject.asObservable();

  constructor(private projectService: ProjectService) {}

  loadProjects() {
    this.projectService.list(0, 20).subscribe(page => this.projectsSubject.next(page.content));
  }

  addProject(project: Project) {
    this.projectsSubject.next([...this.projectsSubject.value, project]);
  }
}
```

Components subscribe to `projects$` via the `async` pipe in templates (`*ngFor="let p of projects$ | async"`) rather than manual `.subscribe()` + manual unsubscribe — this avoids memory leaks without needing `takeUntil` boilerplate everywhere.

Only introduce NgRx if: multiple unrelated components need to react to the same state changes in complex ways, or you have deeply nested async dependency chains that `BehaviorSubject` chaining makes hard to follow. For this project's scope, you likely won't need it.

---

## 6. Routing

```ts
// app.routes.ts
export const routes: Routes = [
  { path: 'login', loadComponent: () => import('./features/auth/login/login.component').then(m => m.LoginComponent) },
  { path: 'register', loadComponent: () => import('./features/auth/register/register.component').then(m => m.RegisterComponent) },

  {
    path: '',
    canActivate: [authGuard],
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      { path: 'dashboard', loadComponent: () => import('./features/dashboard/dashboard.component').then(m => m.DashboardComponent) },
      { path: 'projects/:id', loadComponent: () => import('./features/projects/project-detail/project-detail.component').then(m => m.ProjectDetailComponent) },
      { path: 'projects/:id/members', loadComponent: () => import('./features/projects/project-members/project-members.component').then(m => m.ProjectMembersComponent) },
      { path: 'tasks/:id', loadComponent: () => import('./features/tasks/task-detail/task-detail.component').then(m => m.TaskDetailComponent) },
      { path: 'profile', loadComponent: () => import('./features/profile/profile.component').then(m => m.ProfileComponent) },
    ]
  },
  { path: '**', redirectTo: 'dashboard' }
];
```

Every route is **lazy-loaded** (`loadComponent`) so the initial bundle stays small. The `authGuard` on the parent route protects everything under it in one place instead of repeating it per-route.

---

## 7. Guards & Interceptors

### `auth.guard.ts`
```ts
export const authGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);
  if (auth.isLoggedIn()) return true;
  router.navigate(['/login']);
  return false;
};
```

### `jwt.interceptor.ts`
Attaches the bearer token to every outgoing request (except `/auth/login` and `/auth/register`):
```ts
export const jwtInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(AuthService);
  const token = auth.getToken();
  if (token && !req.url.includes('/auth/')) {
    req = req.clone({ setHeaders: { Authorization: `Bearer ${token}` } });
  }
  return next(req);
};
```

### `error.interceptor.ts`
Catches `401`/`403` globally, logs out and redirects; surfaces other errors (`400`, `500`) to a shared toast/snackbar service so components don't need repetitive `catchError` blocks everywhere:
```ts
export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(AuthService);
  const router = inject(Router);
  return next(req).pipe(
    catchError((err: HttpErrorResponse) => {
      if (err.status === 401) { auth.logout(); router.navigate(['/login']); }
      return throwError(() => err);
    })
  );
};
```

Register both in `app.config.ts`:
```ts
providers: [
  provideHttpClient(withInterceptors([jwtInterceptor, errorInterceptor])),
  provideRouter(routes),
]
```

---

## 8. Forms

Use **Reactive Forms** (`FormBuilder`/`FormGroup`) everywhere, with validators mirroring the backend's `@Valid` constraints, so bad input is caught client-side before hitting the API.

```ts
// project-form.component.ts
this.form = this.fb.group({
  name: ['', [Validators.required, Validators.maxLength(100)]],
  description: ['', Validators.maxLength(500)],
  status: ['TODO', Validators.required],
});
```

- Show inline validation errors under each field (`form.get('name')?.errors`).
- Disable the submit button while the request is in-flight (`isSubmitting` flag) to prevent double-submits.
- On `400` validation errors from the backend, map the error response's field-level messages back onto the form controls if you want polish — otherwise a single toast with the message is fine for an intermediate project.

---

## 9. Component Responsibilities (per feature)

| Component | Responsibility |
|---|---|
| `LoginComponent` / `RegisterComponent` | Auth forms, calls `AuthService`, redirects to dashboard on success |
| `DashboardComponent` | Lists projects the user is a member of, "create project" entry point |
| `ProjectListComponent` | Reusable project card/list rendering (used by dashboard) |
| `ProjectDetailComponent` | Project info + embeds `TaskBoardComponent` |
| `ProjectFormComponent` | Create/edit project (reused for both via an `@Input() project?`) |
| `ProjectMembersComponent` | List members, add/remove, change role |
| `TaskBoardComponent` | Kanban columns (TODO / IN_PROGRESS / DONE), drag-and-drop optional (Angular CDK) |
| `TaskDetailComponent` | Full task view — description, assignee, due date, tags, embeds `CommentListComponent` |
| `TaskFormComponent` | Create/edit task (reused, like `ProjectFormComponent`) |
| `CommentListComponent` | Lists + adds comments for a task |
| `ProfileComponent` | View/edit own profile, change password |

Reusable presentational components (`status-badge`, `priority-tag`) take simple `@Input()`s and emit nothing — they're pure display, no service calls, easy to reuse and test.

---

## 10. Suggested Build Order (mirrors the backend's Section 9)

1. `environment.ts`, `app.config.ts` with `HttpClient` + router set up.
2. Models (`user.model.ts`, `auth.model.ts`) + `AuthService` + login/register components.
3. `jwt.interceptor.ts`, `error.interceptor.ts`, `auth.guard.ts` — confirm protected routes redirect correctly.
4. `ProjectService` + `ProjectStateService` + dashboard/project-list/project-form.
5. `project-detail` + `project-members`.
6. `TaskService` + task-board + task-form + task-detail.
7. `CommentService` + comment-list (nested in task-detail).
8. `TagService` + tag chips on task-form/task-detail.
9. Shared components (`status-badge`, `pagination`, `loading-spinner`, `confirm-dialog`) — extract these once you notice repetition, not preemptively.
10. Polish: loading states, empty states, error toasts, responsive layout pass.

---

## 11. Scaling the Frontend to Multi-Tenancy (Phase 2)

This is intentionally a small section — the frontend needs very little change, because the backend does the real work (JWT already carries `organizationId`, Hibernate filter enforces scoping).

- `AuthResponse`/`User` model gains an `organizationId` (and possibly `organizationName`) field — already flows through existing interceptors/services with no change.
- If a user can belong to multiple organizations, add:
  - An **org switcher** in the nav bar (dropdown), backed by a small `OrganizationService`/`OrganizationStateService` following the exact same pattern as `ProjectStateService`.
  - Re-login or a `/auth/switch-organization` endpoint that issues a new JWT scoped to the newly selected org — the frontend just stores the new token, same as login.
- No changes needed to routing, guards, or interceptors — they're already organization-agnostic since scoping happens server-side.

This is the payoff of the DTO discipline from Section 3: because components never depended on backend-internal fields, adding a tenant boundary is additive, not a refactor.
