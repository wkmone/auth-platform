# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
# Build parent POM only (first time or after POM changes)
mvn install -N

# Build common-lib (required before any service build)
mvn install -pl common/common-lib -DskipTests

# Build a single service (e.g. auth-service)
mvn clean compile -pl services/auth-service

# Build all modules
mvn clean compile

# Build frontend apps
cd frontend/login-app && npm install && npm run build
cd frontend/admin-app && npm install && npm run build

# Start dev infrastructure (PostgreSQL, Redis, RabbitMQ, Nacos)
cd docker && docker compose up -d postgres redis rabbitmq nacos
```

## Architecture

OAuth2/OIDC unified authentication platform (SSO) for internal enterprise systems. Full microservices on JDK 21 / Spring Boot 3.3 / Spring Cloud 2023.0.2 / Spring Authorization Server 1.3.0.

### Module Map

```
auth-platform/
├── pom.xml                          # Parent POM, dependency management for all modules
├── common/common-lib/               # Shared library (Result, PageResult, ErrorCode,
│                                     #   BusinessException, JwtUtils, AuthConstants,
│                                     #   AuditEventPublisher)
├── services/
│   ├── gateway/          :9000       # Spring Cloud Gateway — single entry point
│   │                                 #   GlobalFilters: TraceFilter → RateLimitFilter →
│   │                                 #   JwtAuthFilter → RoleAuthFilter
│   ├── auth-service/     :9001       # OAuth2 Authorization Server (Spring Auth Server)
│   │                                 #   JdbcRegisteredClientRepository, JWKSet endpoint,
│   │                                 #   /userinfo, consent page, login page
│   ├── user-service/     :9002       # User/Role/Permission CRUD (MyBatis-Plus)
│   │                                 #   Password policy, login log, /by-username for auth
│   ├── app-service/      :9003       # OAuth2 app registration + approval workflow
│   │                                 #   Publishes app.approved / app.revoked to RabbitMQ
│   ├── audit-service/    :9004       # Audit event storage (monthly partitioned)
│   │                                 #   Consumes audit.log from RabbitMQ
│   └── notification-service/ :9005   # Email/SMS/in-app notifications
│                                     #   Thymeleaf templates, consumes notify.* queues
├── frontend/
│   ├── login-app/        :3001       # SSO login pages (React 18 + Ant Design 5)
│   └── admin-app/        :3000       # Admin management SPA (React 18 + Ant Design 5)
└── docker/
    ├── docker-compose.yml            # All 14 services (6 Java + 2 frontend + 4 infra + mailhog)
    ├── Dockerfile                    # Java service image (eclipse-temurin:21-jre-alpine)
    ├── Dockerfile.frontend           # Frontend image (nginx:alpine serving dist/)
    └── init-db.sql                   # Creates 5 databases on first postgres start
```

### Service Communication

- **Sync:** Gateway routes to services by path prefix (e.g. `/api/users/**` → `lb://user-service`)
- **Async:** RabbitMQ — 6 queues defined in `AuthConstants`: `app.approved`, `app.revoked`, `audit.log`, `notify.email`, `notify.sms`, `notify.inapp`
- **Service discovery:** Nacos (all services register at startup)
- **Auth Service → User Service:** RestTemplate call to `/api/users/by-username/{username}` for user lookup during login

### Gateway Filter Chain (order)

| Order | Filter | Purpose |
|-------|--------|---------|
| -3 | TraceFilter | Inject `X-Trace-Id` (UUID) on request + response |
| -2 | RateLimitFilter | Redis Lua sliding window: 60/min per IP, 200/min per user, 30/min `/oauth2/token` |
| -1 | JwtAuthFilter | Nimbus JWT validation via JWK Set from auth-service, sets `X-User-Id`/`X-Username`/`X-User-Roles` headers |
| 0 | RoleAuthFilter | Enforces `admin`/`super_admin` role for `/api/roles/**` and `/api/audit/**` |

Public paths skipped by JwtAuthFilter: `/oauth2/`, `/.well-known/`, `/login`, `/oauth2/consent`, `/actuator/`

### Database Per Service

| Service | Database | Key Tables |
|---------|----------|------------|
| auth-service | auth_db | oauth2_registered_client, oauth2_authorization, oauth2_authorization_consent |
| user-service | user_db | sys_user, sys_role, sys_permission, sys_user_role, sys_role_permission, sys_password_history, sys_login_log |
| app-service | app_db | oauth2_application |
| audit-service | audit_db | audit_event (monthly partitioned) |
| notification-service | notification_db | notification_template, notification_message |

### Auth Flow (Authorization Code + PKCE)

1. User accesses external app (CRM/OA/ERP) → 302 to `auth.company.com/login?client_id=...&redirect_uri=...`
2. User submits credentials → Auth Service calls User Service `/api/users/by-username/{username}` to load user
3. Auth Service validates password → shows consent page (first time) or issues authorization code
4. 302 back to app with `?code=xxx&state=xyz`
5. App backend calls `POST /oauth2/token` to exchange code for tokens
6. Returns `{ access_token (JWT, 15min), refresh_token (opaque, 7d), id_token }`

### Key Conventions

- All REST endpoints return `Result<T>` (code, message, data) from common-lib
- Paginated endpoints return `PageResult<T>` (total, page, size, records)
- Business errors throw `BusinessException(ErrorCode)` — caught by `GlobalExceptionHandler`
- MyBatis-Plus entities use `@TableId(type = IdType.ASSIGN_UUID)`, auto-fill timestamps via `MetaObjectHandler`
- Flyway migrations in `src/main/resources/db/migration/` (naming: `V{N}__description.sql`)
- API Gateway uses Spring Cloud Gateway (reactive/WebFlux), NOT spring-boot-starter-web
- Token format: JWT signed RS256, opaque refresh tokens stored in Redis
