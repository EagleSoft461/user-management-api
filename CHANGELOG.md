# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

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

### Planned Features
- Refresh token mechanism
- Email verification
- Password reset functionality
- Rate limiting
- Redis caching
- User profile management
- Audit logging
- Two-factor authentication (2FA)
