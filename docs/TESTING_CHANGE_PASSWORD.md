# Testing Change Password Feature

This guide shows how to test the new change password functionality.

## Prerequisites
- Application running on `http://localhost:8081`
- PostgreSQL database running (via Docker Compose)
- A registered user account
- Valid JWT token

## Step 1: Register a New User

```bash
curl -X POST http://localhost:8081/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123"
  }'
```

**Expected Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "email": "test@example.com"
}
```

Save the token for the next steps.

## Step 2: Change Password

```bash
curl -X POST http://localhost:8081/api/users/change-password \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE" \
  -d '{
    "currentPassword": "password123",
    "newPassword": "newPassword456",
    "confirmPassword": "newPassword456"
  }'
```

**Expected Response:**
```
Password changed successfully
```

## Step 3: Verify Password Change

Try logging in with the old password (should fail):

```bash
curl -X POST http://localhost:8081/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123"
  }'
```

**Expected Response:** 401 Unauthorized

Try logging in with the new password (should succeed):

```bash
curl -X POST http://localhost:8081/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "newPassword456"
  }'
```

**Expected Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "email": "test@example.com"
}
```

## Error Cases

### 1. Passwords Don't Match

```bash
curl -X POST http://localhost:8081/api/users/change-password \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE" \
  -d '{
    "currentPassword": "newPassword456",
    "newPassword": "anotherPassword789",
    "confirmPassword": "differentPassword"
  }'
```

**Expected Response:** 400 Bad Request
```json
{
  "message": "New password and confirm password do not match",
  "timestamp": "2026-03-08T13:00:00"
}
```

### 2. Incorrect Current Password

```bash
curl -X POST http://localhost:8081/api/users/change-password \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE" \
  -d '{
    "currentPassword": "wrongPassword",
    "newPassword": "anotherPassword789",
    "confirmPassword": "anotherPassword789"
  }'
```

**Expected Response:** 400 Bad Request
```json
{
  "message": "Current password is incorrect",
  "timestamp": "2026-03-08T13:00:00"
}
```

### 3. New Password Same as Current

```bash
curl -X POST http://localhost:8081/api/users/change-password \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE" \
  -d '{
    "currentPassword": "newPassword456",
    "newPassword": "newPassword456",
    "confirmPassword": "newPassword456"
  }'
```

**Expected Response:** 400 Bad Request
```json
{
  "message": "New password must be different from current password",
  "timestamp": "2026-03-08T13:00:00"
}
```

### 4. Missing JWT Token

```bash
curl -X POST http://localhost:8081/api/users/change-password \
  -H "Content-Type: application/json" \
  -d '{
    "currentPassword": "newPassword456",
    "newPassword": "anotherPassword789",
    "confirmPassword": "anotherPassword789"
  }'
```

**Expected Response:** 403 Forbidden

### 5. Password Too Short

```bash
curl -X POST http://localhost:8081/api/users/change-password \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE" \
  -d '{
    "currentPassword": "newPassword456",
    "newPassword": "123",
    "confirmPassword": "123"
  }'
```

**Expected Response:** 400 Bad Request
```json
{
  "message": "New password must be at least 6 characters",
  "timestamp": "2026-03-08T13:00:00"
}
```

## Testing via Swagger UI

1. Open `http://localhost:8081/swagger-ui/index.html`
2. Click on "Authorize" button (top right)
3. Enter your JWT token: `Bearer YOUR_JWT_TOKEN_HERE`
4. Navigate to "User Management" section
5. Find "POST /api/users/change-password"
6. Click "Try it out"
7. Fill in the request body
8. Click "Execute"

## Unit Tests

Run the comprehensive unit tests:

```bash
./mvnw test -Dtest=UserServiceTest
```

All 9 tests should pass, including 5 new tests for the change password feature:
- ✅ changePassword_Success
- ✅ changePassword_PasswordsDoNotMatch_ThrowsException
- ✅ changePassword_NewPasswordSameAsCurrent_ThrowsException
- ✅ changePassword_IncorrectCurrentPassword_ThrowsException
- ✅ changePassword_UserNotFound_ThrowsException
