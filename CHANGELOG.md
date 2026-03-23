# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

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
