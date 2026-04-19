# User Management System - Backend API

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.7-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Build](https://img.shields.io/badge/Build-Passing-success.svg)](https://github.com)

A modern and secure user management system REST API built with Spring Boot 3, Spring Security, JWT authentication, and PostgreSQL.

## Screenshots

### Swagger UI
![Swagger UI](docs/images/swagger-ui.png)

### API Response Examples
![Register Response](docs/images/register-response.png)
![Login Response](docs/images/login-response.png)
![Change Password](docs/images/change-password-response.png)
![Deactivate Account](docs/images/deactivate-account-response.png)

## Technologies

- **Java 17** - LTS version
- **Spring Boot 3.3.7** - Latest stable version
- **Spring Security** - Authentication & Authorization
- **JWT (JSON Web Token)** - Token-based authentication
- **Spring Data JPA** - Database operations
- **PostgreSQL 16** - Relational database
- **Redis 7** - In-memory caching
- **Docker & Docker Compose** - Containerization
- **Swagger/OpenAPI** - API Documentation
- **JUnit 5 & Mockito** - Testing
- **BCrypt** - Password encryption
- **Bucket4j** - Rate limiting
- **JavaMailSender** - Email (Gmail SMTP)
- **Spring AOP** - Audit logging
- **TOTP (dev.samstevens.totp)** - Two-Factor Authentication

## Features

### v1.0.0 (Released)
- JWT-based authentication
- BCrypt password encryption
- Role-based access control (USER, ADMIN)
- RESTful API endpoints
- Global exception handling
- Input validation
- Swagger UI documentation
- Unit & Integration tests
- Docker support

### v1.1.0 (Released)
- Change password functionality
- Account deactivation (soft delete)
- Password reset with token
- Refresh token mechanism with rotation

### v1.2.0 (Released)
- Email verification (Gmail SMTP)
- Rate limiting (Bucket4j - IP based)
- Redis caching (5-minute TTL)

### v1.3.0 (Released)
- Audit logging with Spring AOP
- Login success/failure tracking
- Admin audit log endpoints

### v1.4.0 (Released)
- Two-Factor Authentication (TOTP/Google Authenticator)
- QR code generation for authenticator apps
- 2FA enable/disable/validate endpoints

### v1.5.0 (Released)
- Pagination for user listing (GET /api/users/paged)
- Filtering by active status, email, role
- Sorting by any field (asc/desc)
- Page metadata (totalPages, totalElements, currentPage)

## Architecture

```
src/
├── main/
│   ├── java/com/backend/usermanagement/
│   │   ├── config/          # Configuration classes
│   │   ├── controller/      # REST Controllers
│   │   ├── domain/entity/   # JPA Entities
│   │   ├── dto/             # Data Transfer Objects
│   │   ├── exception/       # Exception handling
│   │   ├── repository/      # JPA Repositories
│   │   ├── security/        # Security, JWT, AOP
│   │   └── service/         # Business logic
│   └── resources/
│       └── application.properties
└── test/                    # Test classes
```

## Installation

### Prerequisites

- Java 17+
- Maven 3.6+
- Docker & Docker Compose

### 1. Clone the Repository

```bash
git clone https://github.com/EagleSoft461/user-management-api.git
cd user-management-api
```

### 2. Set Environment Variables

```bash
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password
```

### 3. Start PostgreSQL and Redis

```bash
docker-compose up -d
```

### 4. Run the Application

```bash
./mvnw spring-boot:run
```

The application will start at `http://localhost:8081`

## API Endpoints

### Authentication

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/auth/register` | Register new user | No |
| POST | `/auth/login` | User login | No |
| POST | `/auth/refresh` | Refresh access token | No |
| POST | `/auth/forgot-password` | Request password reset | No |
| POST | `/auth/reset-password` | Reset password with token | No |
| GET | `/auth/verify-email` | Verify email with token | No |
| POST | `/auth/resend-verification` | Resend verification email | No |
| POST | `/auth/2fa/setup` | Setup 2FA (get QR code) | Yes |
| POST | `/auth/2fa/verify` | Enable 2FA | Yes |
| POST | `/auth/2fa/validate` | Login with 2FA code | No |
| POST | `/auth/2fa/disable` | Disable 2FA | Yes |

### User Management

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/api/users` | List all users | ADMIN |
| GET | `/api/users/paged` | List users with pagination & filtering | ADMIN |
| GET | `/api/users/{id}` | Get user details | ADMIN |
| DELETE | `/api/users/{id}` | Deactivate user | ADMIN |
| PUT | `/api/users/{id}/roles/{roleName}` | Add role to user | ADMIN |
| POST | `/api/users/change-password` | Change password | USER |
| POST | `/api/users/deactivate` | Deactivate own account | USER |

### Audit Logs

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/api/admin/audit-logs` | All audit logs | ADMIN |
| GET | `/api/admin/audit-logs/failed` | Failed operations only | ADMIN |
| GET | `/api/admin/audit-logs/user/{email}` | Logs by user | ADMIN |

## API Documentation

Swagger UI: `http://localhost:8081/swagger-ui.html`

OpenAPI Docs: `http://localhost:8081/v3/api-docs`

## Security

- Passwords hashed with BCrypt
- JWT tokens valid for 24 hours
- Role-based authorization
- Rate limiting: 5 req/min (login), 3 req/min (register), 30 req/min (general)
- Email verification required after registration
- All sensitive operations audit logged
- Optional Two-Factor Authentication (TOTP)

## Pagination

```
GET /api/users/paged?page=0&size=10&sortBy=createdAt&sortDir=desc&active=true&email=test&role=ADMIN
```

Response includes: content, currentPage, totalPages, totalElements, pageSize, first, last

## Testing

```bash
./mvnw test
```

## Docker

```bash
docker-compose up -d
docker-compose down
docker-compose down -v
```

## Deployment

### Environment Variables

```properties
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/userdb
SPRING_DATASOURCE_USERNAME=admin
SPRING_DATASOURCE_PASSWORD=admin
JWT_SECRET=your-secret-key
JWT_EXPIRATION=86400000
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password
SPRING_DATA_REDIS_HOST=localhost
SPRING_DATA_REDIS_PORT=6379
```

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes
4. Push to the branch
5. Open a Pull Request

## Roadmap

- [x] JWT authentication
- [x] Role-based access control
- [x] Change password & account deactivation
- [x] Password reset & refresh token
- [x] Email verification
- [x] Rate limiting
- [x] Redis caching
- [x] Audit logging
- [x] Two-Factor Authentication (2FA)
- [x] Pagination & Filtering
- [ ] User profile management
- [ ] API versioning

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Author

**EagleSoft461**
- GitHub: [@EagleSoft461](https://github.com/EagleSoft461)
- LinkedIn: [Ali Orhan Ok](https://www.linkedin.com/in/ali-orhan-ok-309a2a38a/)

---

If you find this project useful, please consider giving it a star!
