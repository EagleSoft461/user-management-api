# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

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

### Planned for v1.2.0
- Email verification
- Rate limiting
- Redis caching
