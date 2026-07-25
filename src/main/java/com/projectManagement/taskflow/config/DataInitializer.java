package com.projectManagement.taskflow.config;

import com.projectManagement.taskflow.entity.*;
import com.projectManagement.taskflow.enums.*;
import com.projectManagement.taskflow.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Configuration
public class DataInitializer {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Bean
    @Transactional
    CommandLineRunner initData(
            UserRepo userRepo,
            ProjectRepo projectRepo,
            TaskRepo taskRepo,
            TagRepo tagRepo,
            CommentRepo commentRepo,
            ProjectMemberRepo projectMemberRepo,
            ProfileRepo profileRepo,
            BCryptPasswordEncoder passwordEncoder
    ) {
        return args -> {
            if (userRepo.count() > 0) {
                log.info("Database already contains data — skipping seed.");
                return;
            }

            log.info("Seeding database with initial data...");

            // ══════════════════════════════════════════════
            // 1. USERS  — 10 total
            // ══════════════════════════════════════════════
            UserEntity admin   = createUser("admin",        "Admin@123",    "admin@taskflow.io",     RoleEnum.ADMIN, passwordEncoder);
            UserEntity john    = createUser("johndoe",      "John@123",     "john@taskflow.io",      RoleEnum.USER,  passwordEncoder);
            UserEntity jane    = createUser("janesmith",    "Jane@123",     "jane@taskflow.io",      RoleEnum.USER,  passwordEncoder);
            UserEntity bob     = createUser("bobwilson",    "Bob@123",      "bob@taskflow.io",       RoleEnum.USER,  passwordEncoder);
            UserEntity alice   = createUser("alicemartin",  "Alice@123",    "alice@taskflow.io",     RoleEnum.USER,  passwordEncoder);
            UserEntity chris   = createUser("chrischen",    "Chris@123",    "chris@taskflow.io",     RoleEnum.USER,  passwordEncoder);
            UserEntity diana   = createUser("dianapark",    "Diana@123",    "diana@taskflow.io",     RoleEnum.USER,  passwordEncoder);
            UserEntity ethan   = createUser("ethanlee",     "Ethan@123",    "ethan@taskflow.io",     RoleEnum.USER,  passwordEncoder);
            UserEntity fiona   = createUser("fionawright",  "Fiona@123",    "fiona@taskflow.io",     RoleEnum.USER,  passwordEncoder);
            UserEntity george  = createUser("georgemoore",  "George@123",   "george@taskflow.io",    RoleEnum.USER,  passwordEncoder);

            userRepo.saveAll(List.of(admin, john, jane, bob, alice, chris, diana, ethan, fiona, george));
            log.info("✅ Created {} users", userRepo.count());

            // ══════════════════════════════════════════════
            // 2. PROFILES  — 10 total (one per user)
            // ══════════════════════════════════════════════
            profileRepo.saveAll(List.of(
                    createProfile(admin, "Admin", "User", "https://api.dicebear.com/7.x/avataaars/svg?seed=admin", "Platform administrator and team lead."),
                    createProfile(john, "John", "Doe", "https://api.dicebear.com/7.x/avataaars/svg?seed=john", "Full-stack developer with 8 years of experience."),
                    createProfile(jane, "Jane", "Smith", "https://api.dicebear.com/7.x/avataaars/svg?seed=jane", "Backend specialist passionate about microservices."),
                    createProfile(bob, "Bob", "Wilson", "https://api.dicebear.com/7.x/avataaars/svg?seed=bob", "DevOps engineer and cloud infrastructure enthusiast."),
                    createProfile(alice, "Alice", "Martin", "https://api.dicebear.com/7.x/avataaars/svg?seed=alice", "UI/UX designer who also codes. Loves design systems."),
                    createProfile(chris, "Chris", "Chen", "https://api.dicebear.com/7.x/avataaars/svg?seed=chris", "Senior platform engineer. Kubernetes ninja."),
                    createProfile(diana, "Diana", "Park", "https://api.dicebear.com/7.x/avataaars/svg?seed=diana", "Data engineer skilled in stream processing and analytics."),
                    createProfile(ethan, "Ethan", "Lee", "https://api.dicebear.com/7.x/avataaars/svg?seed=ethan", "Software engineer focused on API design and security."),
                    createProfile(fiona, "Fiona", "Wright", "https://api.dicebear.com/7.x/avataaars/svg?seed=fiona", "Mobile developer specialising in React Native."),
                    createProfile(george, "George", "Moore", "https://api.dicebear.com/7.x/avataaars/svg?seed=george", "Site reliability engineer keeping the lights on.")
            ));
            log.info("✅ Created {} profiles", profileRepo.count());

            // ══════════════════════════════════════════════
            // 3. TAGS  — 12 total
            // ══════════════════════════════════════════════
            TagEntity bug         = createTag("bug",           "#dc3545");
            TagEntity feature     = createTag("feature",       "#28a745");
            TagEntity enhancement = createTag("enhancement",   "#007bff");
            TagEntity docs        = createTag("documentation", "#6c757d");
            TagEntity design      = createTag("design",        "#e83e8c");
            TagEntity urgent      = createTag("urgent",        "#ffc107");
            TagEntity frontend    = createTag("frontend",      "#17a2b8");
            TagEntity backend     = createTag("backend",       "#6610f2");
            TagEntity testing     = createTag("testing",       "#fd7e14");
            TagEntity security    = createTag("security",      "#343a40");
            TagEntity performance = createTag("performance",   "#20c997");
            TagEntity api         = createTag("api",           "#6f42c1");

            tagRepo.saveAll(List.of(bug, feature, enhancement, docs, design, urgent,
                                    frontend, backend, testing, security, performance, api));
            log.info("✅ Created {} tags", tagRepo.count());

            // ══════════════════════════════════════════════
            // 4. PROJECTS  — 5 total
            // ══════════════════════════════════════════════
            ProjectEntity taskflowApp = createProject(
                    "TaskFlow Platform",
                    "The ultimate task management application. Built with Spring Boot + Angular to help teams " +
                            "organize, track, and collaborate on projects in real-time. Features include Kanban boards, " +
                            "sprint planning, time tracking, and real-time notifications.",
                    Status.IN_PROGRESS, admin
            );

            ProjectEntity mobileRedesign = createProject(
                    "Mobile App Redesign",
                    "A complete visual overhaul of the mobile application to align with the new design system. " +
                            "Includes new navigation patterns, updated colour palette, accessibility improvements, " +
                            "and a全新的 component library built with React Native.",
                    Status.TODO, john
            );

            ProjectEntity ecomBackend = createProject(
                    "E-Commerce Backend",
                    "Microservices-based backend for the upcoming e-commerce platform. Covers product catalogue, " +
                            "inventory management, order processing, payment integration with Stripe, " +
                            "and a recommendation engine powered by ML.",
                    Status.IN_PROGRESS, jane
            );

            ProjectEntity devopsInfra = createProject(
                    "DevOps Infrastructure",
                    "Centralised CI/CD pipelines, container orchestration with Kubernetes, monitoring with " +
                            "Prometheus & Grafana, and automated rollback strategies for all microservices.",
                    Status.IN_PROGRESS, chris
            );

            ProjectEntity analyticsDashboard = createProject(
                    "Data Analytics Dashboard",
                    "Real-time analytics dashboard for executive leadership. Displays KPIs across revenue, " +
                            "user engagement, churn rate, and operational efficiency. Powered by Apache Kafka + ClickHouse.",
                    Status.TODO, diana
            );

            projectRepo.saveAll(List.of(taskflowApp, mobileRedesign, ecomBackend, devopsInfra, analyticsDashboard));
            log.info("✅ Created {} projects", projectRepo.count());

            // ══════════════════════════════════════════════
            // 5. PROJECT MEMBERS  — 18 total
            // ══════════════════════════════════════════════
            projectMemberRepo.saveAll(List.of(
                    // TaskFlow Platform (4 members)
                    createMember(taskflowApp, admin,   RoleInProject.OWNER),
                    createMember(taskflowApp, john,    RoleInProject.EDITOR),
                    createMember(taskflowApp, jane,    RoleInProject.EDITOR),
                    createMember(taskflowApp, bob,     RoleInProject.EDITOR),
                    // Mobile App Redesign (4 members)
                    createMember(mobileRedesign, john,    RoleInProject.OWNER),
                    createMember(mobileRedesign, alice,   RoleInProject.EDITOR),
                    createMember(mobileRedesign, jane,    RoleInProject.VIEWER),
                    createMember(mobileRedesign, fiona,   RoleInProject.EDITOR),
                    // E-Commerce Backend (4 members)
                    createMember(ecomBackend, jane,    RoleInProject.OWNER),
                    createMember(ecomBackend, bob,     RoleInProject.EDITOR),
                    createMember(ecomBackend, admin,   RoleInProject.VIEWER),
                    createMember(ecomBackend, ethan,   RoleInProject.EDITOR),
                    // DevOps Infrastructure (3 members)
                    createMember(devopsInfra, chris,   RoleInProject.OWNER),
                    createMember(devopsInfra, bob,     RoleInProject.EDITOR),
                    createMember(devopsInfra, george,  RoleInProject.EDITOR),
                    // Data Analytics Dashboard (3 members)
                    createMember(analyticsDashboard, diana,  RoleInProject.OWNER),
                    createMember(analyticsDashboard, alice,  RoleInProject.EDITOR),
                    createMember(analyticsDashboard, ethan,  RoleInProject.VIEWER)
            ));
            log.info("✅ Created {} project members", projectMemberRepo.count());

            // ══════════════════════════════════════════════
            // 6. TASKS  — 23 total
            // ══════════════════════════════════════════════
            Calendar cal = Calendar.getInstance();

            // ── TaskFlow Platform (7 tasks) ──
            TaskEntity tf1 = createTask(
                    "Set up CI/CD pipeline",
                    "Configure GitHub Actions to run tests, lint, build, and deploy to staging on every push to main. " +
                            "Include branch protection rules and required status checks.",
                    taskflowApp, admin, Status.DONE, Priority.HIGH,
                    offsetDate(cal, -5), offsetDate(cal, -2), offsetDate(cal, -1));

            TaskEntity tf2 = createTask(
                    "Implement user authentication",
                    "Add JWT-based login/signup with refresh tokens. Include role-based access control for ADMIN vs USER, " +
                            "password reset flow, and email verification.",
                    taskflowApp, jane, Status.DONE, Priority.HIGH,
                    offsetDate(cal, -3), offsetDate(cal, 0), offsetDate(cal, 0));

            TaskEntity tf3 = createTask(
                    "Build dashboard analytics",
                    "Create widgets for the dashboard: task completion rate, project health score, " +
                            "burndown chart for the current sprint, and team velocity metrics.",
                    taskflowApp, bob, Status.IN_PROGRESS, Priority.MEDIUM,
                    offsetDate(cal, 0), null, null);

            TaskEntity tf4 = createTask(
                    "Write API documentation",
                    "Document all REST endpoints using Swagger/OpenAPI annotations. " +
                            "Include request/response examples, error codes, and authentication requirements.",
                    taskflowApp, john, Status.TODO, Priority.LOW,
                    offsetDate(cal, 1), null, null);

            TaskEntity tf5 = createTask(
                    "Performance audit & optimisation",
                    "Profile the application with JProfiler. Identify slow queries, add caching with Redis, " +
                            "and reduce API response times below 200ms for the 95th percentile.",
                    taskflowApp, jane, Status.TODO, Priority.MEDIUM,
                    offsetDate(cal, 3), null, null);

            TaskEntity tf6 = createTask(
                    "Add real-time notifications",
                    "Integrate WebSocket support for real-time notifications. Users should be notified when they are " +
                            "assigned a task, mentioned in a comment, or when a task status changes.",
                    taskflowApp, bob, Status.TODO, Priority.MEDIUM,
                    offsetDate(cal, 5), null, null);

            TaskEntity tf7 = createTask(
                    "Fix search pagination bug",
                    "When searching tasks with special characters, the pagination metadata returns incorrect total counts. " +
                            "Root cause seems to be in the Specification query builder.",
                    taskflowApp, admin, Status.IN_PROGRESS, Priority.HIGH,
                    offsetDate(cal, -1), null, null);

            // ── Mobile App Redesign (5 tasks) ──
            TaskEntity mr1 = createTask(
                    "Create component library in Figma",
                    "Design reusable UI components: buttons, inputs, cards, modals, navigation bars, " +
                            "and data-display components following the new design tokens.",
                    mobileRedesign, alice, Status.IN_PROGRESS, Priority.HIGH,
                    offsetDate(cal, -2), null, null);

            TaskEntity mr2 = createTask(
                    "Implement dark mode support",
                    "Add CSS custom properties for theme switching. Ensure all screens respect the " +
                            "prefers-color-scheme media query and provide a manual toggle in settings.",
                    mobileRedesign, alice, Status.TODO, Priority.MEDIUM,
                    offsetDate(cal, 7), null, null);

            TaskEntity mr3 = createTask(
                    "User onboarding flow",
                    "Design and implement a 3-step onboarding experience for first-time users, " +
                            "including feature highlights, permission requests, and personalisation preferences.",
                    mobileRedesign, fiona, Status.TODO, Priority.LOW,
                    offsetDate(cal, 14), null, null);

            TaskEntity mr4 = createTask(
                    "Accessibility audit",
                    "Run axe-core and Lighthouse accessibility audits on all screens. Fix contrast issues, " +
                            "add ARIA labels, ensure keyboard navigation, and test with screen readers.",
                    mobileRedesign, alice, Status.TODO, Priority.HIGH,
                    offsetDate(cal, 3), null, null);

            TaskEntity mr5 = createTask(
                    "Animate page transitions",
                    "Add smooth page transition animations using React Native Reanimated. Transitions should " +
                            "feel native and respect the user's reduced-motion accessibility setting.",
                    mobileRedesign, fiona, Status.TODO, Priority.LOW,
                    offsetDate(cal, 10), null, null);

            // ── E-Commerce Backend (5 tasks) ──
            TaskEntity ec1 = createTask(
                    "Product catalogue service",
                    "Build RESTful CRUD for products with categories, filtering, pagination, full-text search, " +
                            "and variant support (size, colour, etc.).",
                    ecomBackend, bob, Status.IN_PROGRESS, Priority.HIGH,
                    offsetDate(cal, -1), null, null);

            TaskEntity ec2 = createTask(
                    "Integrate Stripe payments",
                    "Implement checkout session creation, webhook handling for payment events, " +
                            "idempotency key support, and refund processing.",
                    ecomBackend, bob, Status.TODO, Priority.HIGH,
                    offsetDate(cal, 10), null, null);

            TaskEntity ec3 = createTask(
                    "Order processing & inventory sync",
                    "Consume order-placed events, update inventory counts in real-time, and trigger " +
                            "fulfilment notifications via email/SMS. Handle race conditions on stock.",
                    ecomBackend, jane, Status.TODO, Priority.MEDIUM,
                    offsetDate(cal, 5), null, null);

            TaskEntity ec4 = createTask(
                    "Build recommendation engine",
                    "Implement collaborative filtering and content-based recommendations using Apache Spark MLlib. " +
                            "Serve top-10 personalised product suggestions on the home page and product detail page.",
                    ecomBackend, ethan, Status.TODO, Priority.MEDIUM,
                    offsetDate(cal, 14), null, null);

            TaskEntity ec5 = createTask(
                    "API rate limiting & security",
                    "Add rate limiting per API key and per IP using Redis. Implement request validation, " +
                            "SQL injection prevention, and proper CORS configuration for production.",
                    ecomBackend, jane, Status.TODO, Priority.HIGH,
                    offsetDate(cal, 2), null, null);

            // ── DevOps Infrastructure (4 tasks) ──
            TaskEntity di1 = createTask(
                    "Kubernetes cluster setup",
                    "Provision an EKS cluster with node groups, IAM roles, and auto-scaling. " +
                            "Install NGINX ingress controller, cert-manager, and ExternalDNS.",
                    devopsInfra, chris, Status.DONE, Priority.HIGH,
                    offsetDate(cal, -7), offsetDate(cal, -3), offsetDate(cal, -4));

            TaskEntity di2 = createTask(
                    "Migrate services to Docker",
                    "Containerise all backend microservices with multi-stage Dockerfiles. " +
                            "Optimise image sizes and set up Docker Compose for local development.",
                    devopsInfra, bob, Status.IN_PROGRESS, Priority.HIGH,
                    offsetDate(cal, -2), null, null);

            TaskEntity di3 = createTask(
                    "Prometheus & Grafana monitoring",
                    "Set up Prometheus to scrape metrics from all services and Kubernetes nodes. " +
                            "Create Grafana dashboards for CPU, memory, request latency, and error rates.",
                    devopsInfra, george, Status.TODO, Priority.MEDIUM,
                    offsetDate(cal, 4), null, null);

            TaskEntity di4 = createTask(
                    "Incident response runbook",
                    "Document runbooks for common incidents: pod crashes, node failures, database replication lag, " +
                            "and DDoS mitigation. Integrate with PagerDuty for on-call alerts.",
                    devopsInfra, chris, Status.TODO, Priority.LOW,
                    offsetDate(cal, 8), null, null);

            // ── Data Analytics Dashboard (2 tasks) ──
            TaskEntity ad1 = createTask(
                    "Set up Kafka data pipeline",
                    "Deploy Apache Kafka cluster and configure producers for user events, order events, " +
                            "and system metrics. Ensure exactly-once semantics and proper partitioning strategy.",
                    analyticsDashboard, diana, Status.IN_PROGRESS, Priority.HIGH,
                    offsetDate(cal, -1), null, null);

            TaskEntity ad2 = createTask(
                    "Build revenue KPI widget",
                    "Design and implement a real-time revenue tracking widget showing MRR, ARR, " +
                            "average order value, and churn rate with period-over-period comparisons.",
                    analyticsDashboard, alice, Status.TODO, Priority.MEDIUM,
                    offsetDate(cal, 6), null, null);

            List<TaskEntity> allTasks = List.of(
                    tf1, tf2, tf3, tf4, tf5, tf6, tf7,
                    mr1, mr2, mr3, mr4, mr5,
                    ec1, ec2, ec3, ec4, ec5,
                    di1, di2, di3, di4,
                    ad1, ad2
            );
            taskRepo.saveAll(allTasks);
            log.info("✅ Created {} tasks", taskRepo.count());

            // ══════════════════════════════════════════════
            // 7. TASK–TAG RELATIONSHIPS
            // ══════════════════════════════════════════════
            tf1.setTags(List.of(enhancement, docs, backend));
            tf2.setTags(List.of(feature, security, backend));
            tf3.setTags(List.of(feature, enhancement, frontend));
            tf4.setTags(List.of(docs, api));
            tf5.setTags(List.of(enhancement, performance, backend));
            tf6.setTags(List.of(feature, frontend, backend));
            tf7.setTags(List.of(bug, urgent, frontend));

            mr1.setTags(List.of(design, feature, frontend));
            mr2.setTags(List.of(design, enhancement, frontend));
            mr3.setTags(List.of(design, feature));
            mr4.setTags(List.of(enhancement, testing));
            mr5.setTags(List.of(design, frontend));

            ec1.setTags(List.of(feature, backend, api));
            ec2.setTags(List.of(feature, urgent, api));
            ec3.setTags(List.of(feature, backend));
            ec4.setTags(List.of(feature, enhancement, performance));
            ec5.setTags(List.of(security, urgent, backend));

            di1.setTags(List.of(enhancement, backend));
            di2.setTags(List.of(enhancement, backend));
            di3.setTags(List.of(enhancement, performance));
            di4.setTags(List.of(docs, security));

            ad1.setTags(List.of(feature, backend));
            ad2.setTags(List.of(feature, frontend, design));

            taskRepo.saveAll(allTasks);

            // ══════════════════════════════════════════════
            // 8. COMMENTS  — 22 total
            // ══════════════════════════════════════════════
            commentRepo.saveAll(List.of(
                    // TaskFlow Platform comments
                    createComment(tf1, admin, "CI/CD check",
                            "Pipeline is green and deploying to staging every 30 minutes. Ready for production."),
                    createComment(tf2, jane, "Auth flow",
                            "JWT access token expires in 1 hour, refresh token in 7 days. Added token rotation on refresh."),
                    createComment(tf2, admin, "Review complete",
                            "Code looks solid. One suggestion: add rate-limiting to the login endpoint."),
                    createComment(tf3, bob, "Progress update",
                            "Burndown chart widget is done. Working on the project health score now."),
                    createComment(tf3, jane, "Blocking issue",
                            "The health score calculation depends on the analytics data pipeline which isn't ready yet."),
                    createComment(tf7, admin, "Root cause found",
                            "The bug is in the escape logic for special characters in the JPA Specification query. Fix incoming."),
                    createComment(tf7, bob, "PR submitted",
                            "Sent a PR with the fix. Added proper escaping for all special characters and a unit test."),

                    // Mobile App Redesign comments
                    createComment(mr1, alice, "First draft",
                            "Component library v1 is ready for review. Shared the Figma link in the team channel."),
                    createComment(mr1, john, "Great work!",
                            "Love the consistency. Can we add loading skeletons for all components?"),
                    createComment(mr1, alice, "Added skeletons",
                            "Loading skeleton variants added for Card, Table, and List components."),
                    createComment(mr4, alice, "Audit results",
                            "Initial audit shows 34 contrast issues and 12 missing ARIA labels. Logged all in a spreadsheet."),
                    createComment(mr4, john, "Prioritize",
                            "Let's fix the critical contrast issues first. The ARIA labels can be done in a follow-up sprint."),

                    // E-Commerce Backend comments
                    createComment(ec1, bob, "Catalogue API",
                            "CRUD endpoints are done. Moved on to building the search index with Elasticsearch."),
                    createComment(ec1, ethan, "Question",
                            "Should we add GraphQL for the product catalogue? The mobile team needs flexible queries."),
                    createComment(ec1, bob, "Good idea",
                            "Let's add GraphQL in v2. For now REST is fine for the MVP timeline."),
                    createComment(ec2, bob, "Stripe keys",
                            "Need production Stripe keys from the finance team before we can complete the integration."),
                    createComment(ec5, ethan, "Rate limiting approach",
                            "I'm thinking a token bucket algorithm per API key with 1000 req/min as default. Thoughts?"),
                    createComment(ec5, jane, "Sounds good",
                            "Make the limit configurable per plan tier. Free tier = 100/min, Pro = 1000/min, Enterprise = unlimited."),

                    // DevOps Infrastructure comments
                    createComment(di1, chris, "Cluster ready",
                            "EKS cluster provisioned. All add-ons installed and TLS certificates are being issued."),
                    createComment(di2, bob, "Dockerfiles done",
                            "Containerised auth-service, project-service, and task-service. Image sizes reduced by 60% using Alpine base."),
                    createComment(di3, george, "Dashboards progress",
                            "CPU and memory dashboards are live. Working on request latency dashboard with percentile breakdowns.")
            ));
            log.info("✅ Created {} comments", commentRepo.count());

            log.info("✨ Database seeding complete! ✨");
        };
    }

    // ──────────────────────────────────────────────────────
    // Helper methods
    // ──────────────────────────────────────────────────────

    private UserEntity createUser(String username, String rawPassword, String email,
                                  RoleEnum role, BCryptPasswordEncoder encoder) {
        UserEntity u = new UserEntity();
        u.setUsername(username);
        u.setPasswordHash(encoder.encode(rawPassword));
        u.setEmail(email);
        u.setRole(role);
        u.setCreatedAt(new Date());
        return u;
    }

    private ProfileEntity createProfile(UserEntity user, String firstName, String lastName, String avatarUrl, String bio) {
        ProfileEntity p = new ProfileEntity();
        p.setUserId(user.getId());
        p.setFirstName(firstName);
        p.setLastName(lastName);
        p.setAvatarUrl(avatarUrl);
        p.setBio(bio);
        return p;
    }

    private TagEntity createTag(String name, String colorHex) {
        TagEntity t = new TagEntity();
        t.setName(name);
        t.setColorHex(colorHex);
        return t;
    }

    private ProjectEntity createProject(String name, String description,
                                        Status status, UserEntity owner) {
        ProjectEntity p = new ProjectEntity();
        p.setName(name);
        p.setDescription(description);
        p.setStatus(status);
        p.setUser(owner);
        p.setCreatedAt(new Date());
        return p;
    }

    private ProjectMember createMember(ProjectEntity project, UserEntity user,
                                       RoleInProject role) {
        ProjectMember m = new ProjectMember();
        m.setProject(project);
        m.setUser(user);
        m.setRoleInProject(role);
        return m;
    }

    private TaskEntity createTask(String title, String description,
                                  ProjectEntity project, UserEntity assignee,
                                  Status status, Priority priority,
                                  Date createdAt, Date updatedAt, Date dueDate) {
        TaskEntity t = new TaskEntity();
        t.setTitle(title);
        t.setDescription(description);
        t.setProject(project);
        t.setAssignee(assignee);
        t.setStatus(status);
        t.setPriority(priority);
        t.setDueDate(dueDate);
        t.setCreatedAt(createdAt != null ? createdAt : new Date());
        t.setUpdatedAt(updatedAt);
        return t;
    }

    private CommentEntity createComment(TaskEntity task, UserEntity commentator,
                                        String name, String content) {
        CommentEntity c = new CommentEntity();
        c.setTask(task);
        c.setCommentator(commentator);
        c.setName(name);
        c.setContent(content);
        return c;
    }

    /** Returns a Date relative to today. Negative = past, positive = future. */
    private Date offsetDate(Calendar cal, int daysFromNow) {
        Calendar c = (Calendar) cal.clone();
        c.add(Calendar.DAY_OF_YEAR, daysFromNow);
        c.set(Calendar.HOUR_OF_DAY, 10);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        return c.getTime();
    }
}
