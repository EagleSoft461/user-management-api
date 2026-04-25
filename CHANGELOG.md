# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.7.0] - 2026-04-25

### Added
- API Versioning with URL-based strategy
- `/api/v1/users` — V1 endpoints (simple list, backward compatible)
- `/api/v2/users` — V2 endpoints (pagination + filtering as default)
- `UserControllerV1` — maintains existing behavior for v1 clients
- `UserControllerV2` — enhanced endpoints with pagination support
- Both versions support full CRUD, profile management, and role operations

### Technical
- URL-based versioning (`/api/v1/`, `/api/v2/`)
- Separate controller packages (`controller/v1`, `controller/v2`)
- Shared service layer — no code duplication
- Swagger UI shows both versions separately

## [1.6.0] - 2026-04-23

### Added
- User Profile Management
- `GET /api/users/me` — view own profile
- `PUT /api/users/me` — update own profile
- `firstName`, `lastName`, `bio`, `phoneNumber` fields added to User entity
- `ProfileResponse` DTO with full user details including 2FA status

## [1.5.0] - 2026-04-19

### Added
- Pagination and filtering for user listing
- `GET /api/users/paged` endpoint with query parameters:
  - `page`, `size` — pagination control
  - `sortBy`, `sortDir` — sorting (any field, asc/desc)
  - `active` — filter by active status
  - `email` — search by email (LIKE)
  - `role` — filter by role name
- `PagedResponse<T>` generic wrapper with metadata (totalPages, totalElements, currentPage, first, last)
- `emailVerified` field added to `UserResponse`
- Custom JPQL query with JOIN FETCH to avoid N+1 lazy loading

### Technical
- `UserRepository` extended with pagination query methods
- `@Transactional(readOnly = true)` for read-only pagination queries
- Spring Data JPA `Pageable` and `Page<T>` usage

## [1.4.0] - 2026-04-04

### Added
- Two-Factor Authentication (TOTP) using Google Authenticator
- `POST /auth/2fa/setup` — generates secret key and QR code
- `POST /auth/2fa/verify` — verifies TOTP code and enables 2FA
- `POST /auth/2fa/validate` — validates code during login and returns JWT
- `POST /auth/2fa/disable` — disables 2FA after code verification
- Login flow updated: returns `twoFactorRequired: true` when 2FA is enabled
- `twoFactorEnabled` and `twoFactorSecret` fields added to User entity
- `TwoFactorService` with secret generation, QR code creation, code verification
- `dev.samstevens.totp` library for TOTP implementation

### Security
- Time-based one-time passwords (30-second window)
- SHA1 hashing algorithm, 6-digit codes
- 2FA codes verified before issuing JWT tokens

## [1.3.0] - 2026-03-28

### Added
- Audit logging for all critical operations using Spring AOP
- `AuditLog` entity with action, success, IP address, timestamp fields
- `AuditAction` enum: LOGIN, REGISTER, PASSWORD_CHANGE, PASSWORD_RESET_REQUEST, ACCOUNT_DEACTIVATE, ROLE_UPDATE
- `AuditAspect` — automatically intercepts service methods via `@Around` advice
- `GET /api/admin/audit-logs` — all logs (ADMIN only)
- `GET /api/admin/audit-logs/failed` — failed operations for security analysis
- `GET /api/admin/audit-logs/user/{email}` — per-user audit trail
- Failed login attempts logged with IP address
- `spring-boot-starter-aop` dependency added

### Technical
- `AuditLogService` uses `@Transactional(propagation = REQUIRES_NEW)` — logs persist even when parent transaction rolls back
- Direct logging in `AuthService` for failed authentication (Spring Security exception handling)

## [1.2.0] - 2026-03-23

### Added
- Email verification on user registration (Gmail SMTP)
- `GET /auth/verify-email` endpoint with token-based verification
- `POST /auth/resend-verification` endpoint to resend verification email
- IP-based rate limiting with Bucket4j token bucket algorithm
  - Login: 5 requests/minute
  - Register: 3 requests/minute
  - Forgot password: 3 requests/minute
  - General API: 30 requests/minute
- Redis caching for user data with 5-minute TTL
  - `GET /api/users` — full list cached
  - `GET /api/users/{id}` — per-user cache with key `users::{id}`
  - Automatic cache eviction on write operations
- `EmailVerificationToken` entity with 24-hour expiry
- `CacheConfig` with JSON serialization for Redis
- Docker Compose updated with Redis 7 service

### Security
- Email verification required before login
- Rate limiting prevents brute-force attacks (429 Too Many Requests)
- Email credentials stored as environment variables (never hardcoded)

### Technical
- `spring-boot-starter-data-redis` dependency added
- `bucket4j-core` dependency added
- `spring-boot-starter-mail` dependency added
- `UserResponse` implements `Serializable` for Redis serialization
- `RateLimitingFilter` as Spring Security filter chain component
- `.env.example` added for environment variable reference

## [1.1.0] - 2026-03-13

### Added
- Change password functionality for authenticated users
- Account deactivation (soft delete) by user with password verification
- Password reset flow with time-limited tokens (15 minutes)
- Refresh token mechanism with 7-day validity
- Token rotation strategy (old refresh token revoked when new one issued)
- `/api/users/change-password` endpoint with JWT authentication
- `/api/users/deactivate` endpoint for self-service account deactivation
- `/auth/forgot-password` endpoint to generate password reset token
- `/auth/reset-password` endpoint to reset password with token
- `/auth/refresh` endpoint to obtain new access token
- `PasswordResetToken` entity with expiration tracking
- `RefreshToken` entity with revocation support
- Comprehensive unit tests for all new features

### Security
- Current password verification before allowing change
- Password confirmation matching validation
- Password reset tokens expire after 15 minutes
- Refresh tokens expire after 7 days
- All refresh tokens revoked on password reset (force re-login)
- Single-use refresh tokens (token rotation)
- BCrypt encryption for all password operations

### Technical
- `PasswordResetTokenRepository` with cleanup methods
- `RefreshTokenRepository` with token management
- `RefreshTokenService` for token lifecycle management
- Enhanced `AuthService` with password reset and token refresh
- Transaction management for all critical operations

## [1.0.0] - 2026-02-28

### Added
- JWT-based authentication system
- User registration with email and password
- User login with JWT token generation
- Spring Security integration with BCrypt password encryption
- Role-based access control (USER, ADMIN)
- Global exception handling with custom error responses
- Input validation using Bean Validation
- Swagger/OpenAPI documentation
- RESTful API endpoints for user management
- PostgreSQL database integration
- Docker Compose setup for PostgreSQL
- Unit tests for service layer
- Integration tests for application context
- Comprehensive README with setup instructions

### Security
- BCrypt password hashing
- JWT token-based authentication
- Role-based authorization
- CSRF protection disabled for stateless API
- Secure password validation (minimum 6 characters)

### Technical Stack
- Java 17
- Spring Boot 3.3.7
- Spring Security 6.3.6
- Spring Data JPA
- PostgreSQL 16
- JWT (jjwt 0.12.3)
- Swagger/OpenAPI 2.3.0
- Maven
- Docker

## [Unreleased]

### Planned for v1.3.0
- User profile management
- Audit logging
- Two-factor authentication (2FA)
