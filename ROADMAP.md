# 🗺️ Project Roadmap

This document outlines the planned features and improvements for the User Management API.

## Current Version: v1.5.0 ✅

---

## ✅ v1.0.0 - Foundation
**Status: Completed**

- [x] JWT-based authentication
- [x] User registration and login
- [x] Spring Security integration
- [x] BCrypt password encryption
- [x] Role-based access control (USER, ADMIN)
- [x] Global exception handling
- [x] Input validation
- [x] Swagger/OpenAPI documentation
- [x] PostgreSQL database integration
- [x] Docker Compose setup
- [x] Unit & Integration tests

---

## ✅ v1.1.0 - Authentication Enhancement
**Status: Completed — March 2026**

- [x] Refresh token mechanism with rotation (7-day validity)
- [x] Password reset with time-limited tokens (15 minutes)
- [x] Change password (authenticated users)
- [x] Account deactivation (soft delete)

---

## ✅ v1.2.0 - Email & Security
**Status: Completed — March 2026**

- [x] Email verification on registration (Gmail SMTP)
- [x] Resend verification email endpoint
- [x] IP-based rate limiting with Bucket4j
  - Login: 5 req/min
  - Register: 3 req/min
  - General API: 30 req/min
- [x] Redis caching with 5-minute TTL
- [x] Docker Compose updated with Redis 7

---

## ✅ v1.3.0 - Audit Logging
**Status: Completed — March 2026**

- [x] Spring AOP-based audit logging
- [x] Login success/failure tracking with IP address
- [x] Admin audit log endpoints (all, failed, by user)
- [x] REQUIRES_NEW transaction for log persistence

---

## ✅ v1.4.0 - Two-Factor Authentication
**Status: Completed — April 2026**

- [x] TOTP-based 2FA (Google Authenticator compatible)
- [x] QR code generation for authenticator apps
- [x] 2FA setup, verify, validate, disable endpoints
- [x] Login flow updated with twoFactorRequired flag

---

## ✅ v1.5.0 - Pagination & Filtering
**Status: Completed — April 2026**

- [x] Paginated user listing (GET /api/users/paged)
- [x] Filtering by active status, email (LIKE), role
- [x] Sorting by any field (asc/desc)
- [x] PagedResponse wrapper with metadata
- [x] JOIN FETCH query to prevent N+1 lazy loading

---

## 🚧 v1.6.0 - User Profile Management
**Target: May 2026**
**Priority: Medium**

- [ ] Add firstName, lastName, bio, phoneNumber fields to User
- [ ] GET /api/users/me — view own profile
- [ ] PUT /api/users/me — update own profile
- [ ] Profile completion percentage

---

## 🚧 v1.7.0 - API Versioning & Quality
**Target: June 2026**
**Priority: Medium**

- [ ] API versioning (/api/v1, /api/v2)
- [ ] Increase test coverage to 80%+
- [ ] Add tests for pagination, 2FA, audit logging
- [ ] Integration tests for all endpoints
- [ ] CI/CD pipeline (GitHub Actions)

---

## 🔮 v2.0.0 - Cloud & Microservices
**Target: Q3 2026**
**Priority: Low**

- [ ] Deploy to cloud (Railway / AWS EC2)
- [ ] OAuth2 integration (Google, GitHub)
- [ ] Multi-tenancy support
- [ ] API Gateway integration
- [ ] Distributed tracing

---

## Continuous Improvements

### Code Quality
- [ ] Increase test coverage to 80%+
- [ ] Performance testing with JMeter
- [ ] Security testing with OWASP ZAP

### DevOps
- [ ] CI/CD pipeline (GitHub Actions)
- [ ] Docker multi-stage builds
- [ ] Kubernetes deployment config

---

**Last Updated:** April 2026
**Current Version:** v1.5.0
**Next Release:** v1.6.0 - User Profile Management
