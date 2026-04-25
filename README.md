# User Management System - Backend API

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.7-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Build](https://img.shields.io/badge/Build-Passing-success.svg)](https://github.com)

A modern and secure user management system REST API built with Spring Boot 3, Spring Security, JWT authentication, and PostgreSQL.

## 📸 Screenshots

### Swagger UI
![Swagger UI](docs/images/swagger-ui.png)
*API Documentation - All endpoints can be tested interactively*

### v1.1.0 Features

#### Register Response
![Register Response](docs/images/register-response.png)
*User registration with access token and refresh token*

#### Login Response
![Login Response](docs/images/login-response.png)
*User login with JWT tokens*

#### Refresh Token
![Refresh Token](docs/images/refresh-token-response.png)
*Token refresh with rotation - old token revoked, new tokens issued*

#### Forgot Password
![Forgot Password](docs/images/forgot-password-response.png)
*Password reset token generation*

#### Reset Password
![Reset Password](docs/images/reset-password-response.png)
*Password reset with token*

#### Change Password
![Change Password](docs/images/change-password-response.png)
*Authenticated user changing password*

#### Deactivate Account
![Deactivate Account](docs/images/deactivate-account-response.png)
*User deactivating their own account*

## 🚀 Technologies

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

## 📋 Features

### v1.0.0 (Released)
- ✅ JWT-based authentication
- ✅ BCrypt password encryption
- ✅ Role-based access control (USER, ADMIN)
- ✅ RESTful API endpoints
- ✅ Global exception handling
- ✅ Input validation
- ✅ Swagger UI documentation
- ✅ Unit & Integration tests
- ✅ Docker support

### v1.1.0 (Released)
- ✅ Change password functionality
- ✅ Account deactivation (soft delete)
- ✅ Password reset with token
- ✅ Refresh token mechanism with rotation

### v1.2.0 (Released)
- ✅ Email verification (Gmail SMTP)
- ✅ Rate limiting (Bucket4j - IP based)
- ✅ Redis caching (5-minute TTL)

### v1.3.0 (Released)
- ✅ Audit logging with Spring AOP
- ✅ Login success/failure tracking with IP address
- ✅ Admin audit log endpoints

### v1.4.0 (Released)
- ✅ Two-Factor Authentication (TOTP/Google Authenticator)
- ✅ QR code generation for authenticator apps
- ✅ 2FA enable/disable/validate endpoints

### v1.6.0 (Released)
- User Profile Management (GET/PUT /api/users/me)
- firstName, lastName, bio, phoneNumber fields
- Full profile response with 2FA and email verification status

### v1.5.0 (Released)
- ✅ Pagination for user listing (`GET /api/users/paged`)
- ✅ Filtering by active status, email, role
- ✅ Sorting by any field (asc/desc)
- ✅ Page metadata (totalPages, totalElements, currentPage)

## 🏗️ Architecture

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
│   │   ├── security/        # Security, JWT & AOP
│   │   └── service/         # Business logic
│   └── resources/
│       └── application.properties
└── test/                    # Test classes
```

## 🔧 Installation

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
# Create .env file or set system variables
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

## 📚 API Endpoints

### Authentication

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/auth/register` | Register new user | ❌ |
| POST | `/auth/login` | User login | ❌ |
| POST | `/auth/refresh` | Refresh access token | ❌ |
| POST | `/auth/forgot-password` | Request password reset | ❌ |
| POST | `/auth/reset-password` | Reset password with token | ❌ |
| GET | `/auth/verify-email` | Verify email with token | ❌ |
| POST | `/auth/resend-verification` | Resend verification email | ❌ |
| POST | `/auth/2fa/setup` | Setup 2FA - get QR code | ✅ USER |
| POST | `/auth/2fa/verify` | Enable 2FA with code | ✅ USER |
| POST | `/auth/2fa/validate` | Login with 2FA code | ❌ |
| POST | `/auth/2fa/disable` | Disable 2FA | ✅ USER |

### User Management

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/api/users` | List all users | ✅ ADMIN |
| GET | `/api/users/paged` | List users with pagination & filtering | ✅ ADMIN |
| GET | `/api/users/{id}` | Get user details | ✅ ADMIN |
| DELETE | `/api/users/{id}` | Deactivate user | ✅ ADMIN |
| PUT | `/api/users/{id}/roles/{roleName}` | Add role to user | ✅ ADMIN |
| POST | `/api/users/change-password` | Change password | ✅ USER |
| POST | `/api/users/deactivate` | Deactivate own account | ✅ USER |

### Audit Logs

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/api/admin/audit-logs` | All audit logs | ✅ ADMIN |
| GET | `/api/admin/audit-logs/failed` | Failed operations only | ✅ ADMIN |
| GET | `/api/admin/audit-logs/user/{email}` | Logs by user | ✅ ADMIN |

## 📖 API Documentation

Swagger UI: `http://localhost:8081/swagger-ui.html`

OpenAPI Docs: `http://localhost:8081/v3/api-docs`

## 🔐 Authentication Flow

1. **Register**: Send email and password to `/auth/register`
2. **Login**: Send credentials to `/auth/login`
3. **Token**: Receive JWT token in response
4. **Authorization**: Add token to header for protected endpoints:
   ```
   Authorization: Bearer <your-jwt-token>
   ```

## 📝 Example Requests

### Register

```bash
curl -X POST http://localhost:8081/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "password123"
  }'
```

### Login

```bash
curl -X POST http://localhost:8081/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "password123"
  }'
```

### Get Users with Pagination

```bash
curl -X GET "http://localhost:8081/api/users/paged?page=0&size=10&sortBy=createdAt&sortDir=desc&active=true" \
  -H "Authorization: Bearer <your-jwt-token>"
```

### Change Password

```bash
curl -X POST http://localhost:8081/api/users/change-password \
  -H "Authorization: Bearer <your-jwt-token>" \
  -H "Content-Type: application/json" \
  -d '{
    "currentPassword": "password123",
    "newPassword": "newPassword456",
    "confirmPassword": "newPassword456"
  }'
```

### Deactivate Account

```bash
curl -X POST http://localhost:8081/api/users/deactivate \
  -H "Authorization: Bearer <your-jwt-token>" \
  -H "Content-Type: application/json" \
  -d '{
    "password": "password123"
  }'
```

### Forgot Password

```bash
curl -X POST http://localhost:8081/auth/forgot-password \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com"
  }'
```

### Reset Password

```bash
curl -X POST http://localhost:8081/auth/reset-password \
  -H "Content-Type: application/json" \
  -d '{
    "token": "reset-token-from-email",
    "newPassword": "newPassword456",
    "confirmPassword": "newPassword456"
  }'
```

### Refresh Token

```bash
curl -X POST http://localhost:8081/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{
    "refreshToken": "your-refresh-token"
  }'
```

## 🧪 Testing

```bash
# Run all tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=UserServiceTest
```

## 🗄️ Database Schema

### Users Table
- id (PK)
- email (unique)
- password (encrypted)
- is_active
- email_verified
- two_factor_enabled
- two_factor_secret
- created_at

### Roles Table
- id (PK)
- name (unique)

### User_Roles Table (Many-to-Many)
- user_id (FK)
- role_id (FK)

### Audit Logs Table
- id (PK)
- user_email
- action
- success
- ip_address
- details
- timestamp

## 🔒 Security

- Passwords are hashed with BCrypt
- JWT tokens are valid for 24 hours
- Role-based authorization
- Rate limiting: 5 req/min (login), 3 req/min (register/forgot-password), 30 req/min (general)
- Email verification required after registration
- All sensitive operations are audit logged
- Optional Two-Factor Authentication (TOTP)

## 🗃️ Caching (Redis)

User data is cached in Redis with a 5-minute TTL:
- `GET /api/users` — full list cached
- `GET /api/users/{id}` — per-user cache with key `users::{id}`
- Cache is automatically evicted on any write operation

## 🐳 Docker

```bash
# Start PostgreSQL + Redis
docker-compose up -d

# Stop services
docker-compose down

# Remove data volumes
docker-compose down -v
```

## 📦 Build

```bash
# Create JAR file
./mvnw clean package

# Run JAR
java -jar target/usermanagement-0.0.1-SNAPSHOT.jar
```

## 🚀 Deployment

### Environment Variables

```properties
# Database
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/userdb
SPRING_DATASOURCE_USERNAME=admin
SPRING_DATASOURCE_PASSWORD=admin

# JWT
JWT_SECRET=your-secret-key
JWT_EXPIRATION=86400000

# Email (Gmail SMTP)
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password

# Redis
SPRING_DATA_REDIS_HOST=localhost
SPRING_DATA_REDIS_PORT=6379
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 🗺️ Roadmap

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

See our detailed [ROADMAP.md](ROADMAP.md) for planned features and timeline.

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👤 Author

**EagleSoft461**
- GitHub: [@EagleSoft461](https://github.com/EagleSoft461)
- LinkedIn: [ALİ ORHAN OK](https://www.linkedin.com/in/ali-orhan-ok-309a2a38a/)
- Email: aliorhanok78@gmail.com

## 🙏 Acknowledgments

- Spring Boot Team
- Spring Security Team
- JWT.io
- PostgreSQL Community

## 📞 Support

For support, email aliorhanok78@gmail.com or open an issue in the repository.

---

⭐ If you find this project useful, please consider giving it a star!