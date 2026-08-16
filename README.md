# CodeCluster Auth Service⚡

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Docker](https://img.shields.io/badge/Docker-Engine-blue.svg)](https://www.docker.com/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15+-blue.svg)](https://www.postgresql.org/)

The **CodeCluster Auth Service** is responsible for authentication and authorization-related operations in the CodeCluster platform.

It handles user login, password verification, JWT generation, user roles, and institute-specific roles and membership information.

The service is designed to work with the other CodeCluster microservices through JWT-based authentication.

---

## Responsibilities

The Auth Service is responsible for:

* User authentication
* Login using username/email and password
* Password verification
* JWT access-token generation
* User role management
* Institute role management
* Adding institute information to JWT claims
* Providing authenticated user information
* Validating authentication-related data
* Providing authentication information to other CodeCluster services

---

## Authentication Flow

```text
Client
   |
   | Login Request
   | preferredId + password
   ↓
Auth Service
   |
   | Find User
   ↓
User Repository
   |
   | User Details
   ↓
Auth Service
   |
   | Verify Password
   ↓
Spring Security
   |
   | Generate JWT
   ↓
JWT Service
   |
   | Access Token
   ↓
Client
```

The client stores the returned access token and sends it with subsequent authenticated requests.

```http
Authorization: Bearer <access-token>
```

---

## Login API

### Endpoint

```http
POST /api/v1/auth/login
```

### Request

```json
{
  "preferredId": "username-or-email",
  "password": "password"
}
```

The `preferredId` can be used to identify the user using the supported login identifier.

### Response

```json
{
  "accessToken": "eyJhbGciOiJIUzUxMiJ9...",
  "tokenType": "Bearer",
  "expiresIn": 900,
  "user": {
    "id": "89e2e7b6-ec2c-4857-ae86-28327fd1e79e",
    "email": "user@example.com",
    "name": "User Name",
    "role": "USER",
    "createdAt": "2026-08-05T11:28:56.534712Z",
    "username": "username",
    "active": true
  }
}
```

---

# JWT Authentication

The service generates a signed JWT access token after successful authentication.

The token contains authentication and authorization information required by other CodeCluster services.

## JWT Claims

A typical token contains claims similar to:

```json
{
  "sub": "username",
  "userId": "89e2e7b6-ec2c-4857-ae86-28327fd1e79e",
  "userRole": "USER",
  "instituteId": "be28ce54-fed0-44da-b312-9c3dafb37c5a",
  "instituteRole": "student",
  "iat": 1785953693,
  "exp": 1785954593
}
```

### Claims Description

| Claim           | Description                        |
| --------------- | ---------------------------------- |
| `sub`           | Username of the authenticated user |
| `userId`        | Unique ID of the user              |
| `userRole`      | Global CodeCluster user role       |
| `instituteId`   | Institute associated with the user |
| `instituteRole` | User's role within the institute   |
| `iat`           | Token issued-at timestamp          |
| `exp`           | Token expiration timestamp         |

`instituteId` and `instituteRole` are used to identify the user's institute context.

For example:

```text
userRole      = USER
instituteRole = student
```

This allows downstream services to determine both **who the user is** and **what role the user has within a particular institute**.

---

# Authorization Header

Authenticated requests should include the JWT access token:

```http
Authorization: Bearer <access-token>
```

The API Gateway and other protected services can use the token to authenticate the request and obtain the user's claims.

---

# Technology Stack

* Java
* Spring Boot
* Spring Security
* Spring Data JPA
* JWT
* JJWT
* Maven
* Relational Database

---

# Project Structure

The service follows a layered Spring Boot architecture.

```text
src/
└── main/
    ├── java/
    │   └── com/
    │       └── codecluster/
    │           └── auth/
    │               ├── controller/
    │               ├── service/
    │               ├── repository/
    │               ├── entity/
    │               ├── dto/
    │               ├── security/
    │               ├── config/
    │               └── exception/
    │
    └── resources/
        └── application.yml
```

The exact package structure may change as the service evolves.

---

# Security

Passwords are not stored as plain text.

During authentication, the submitted password is verified against the stored password hash using Spring Security's password-encoding mechanism.

The service generates a signed JWT only after successful authentication.

The JWT should be treated as a credential and must not be exposed unnecessarily.

---

# Configuration

The service requires configuration for:

* Database connection
* JWT secret
* JWT expiration
* Server port
* Other Spring Boot configuration

Example:

```yaml
spring:
  datasource:
    url: jdbc:postgres://localhost:5432/codecluster_auth
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}

jwt:
  secret: ${JWT_SECRET}
  expiration: 900000
```

> **Note:** Do not commit real database credentials or JWT secrets to Git.

Use environment variables or an external configuration service.

---

# Running the Service

## Prerequisites

Make sure the following are installed:

* Java
* Maven
* MySQL or the configured relational database

Check Java:

```bash
java -version
```

Check Maven:

```bash
mvn -version
```

---

## Clone the Repository

```bash
git clone <repository-url>
```

Navigate into the project:

```bash
cd CodeCluster-Auth-Service
```

---

## Configure Environment

Configure the required database and JWT properties.

For example:

```text
DB_USERNAME
DB_PASSWORD
JWT_SECRET
```

The actual configuration depends on the environment in which CodeCluster is running.

---

## Run Using Maven

```bash
mvn spring-boot:run
```

Or build the project:

```bash
mvn clean package
```

Then run the generated JAR:

```bash
java -jar target/<application-name>.jar
```

---

# API Usage

After the service is running, clients can authenticate using:

```http
POST /api/v1/auth/login
```

Example using cURL:

```bash
curl -X POST http://localhost:<PORT>/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "preferredId": "username",
    "password": "password"
  }'
```

---

# Integration With CodeCluster

The Auth Service is part of the CodeCluster microservice architecture.

A typical request flow is:

```text
Frontend
   |
   ↓
API Gateway
   |
   ├──────────────→ Auth Service
   |
   ├──────────────→ User Service
   |
   ├──────────────→ Institute Service
   |
   ├──────────────→ Assessment Service
   |
   └──────────────→ Other Services
```

The Auth Service is responsible for issuing the JWT.

Other services can use the JWT claims to identify the authenticated user and their authorization context.

---

# Institute Context

CodeCluster allows a user to operate within an institute context.

The JWT can therefore contain:

```text
instituteId
instituteRole
```

For example:

```text
User
 ├── userId
 ├── userRole = USER
 │
 └── Institute
      ├── instituteId
      └── instituteRole = student
```

This allows services such as the Assessment Service to determine which institute the request belongs to.

For example, an assessment request can use the authenticated user's:

```text
instituteId
```

to retrieve assessments belonging to that institute.

---

# Frontend Integration

After successful login, the frontend receives the JWT.

Example:

```javascript
localStorage.setItem(
    "jwt",
    `${data.tokenType} ${data.accessToken}`
);
```

The frontend can decode the JWT payload to access non-sensitive claims required for UI behavior.

For example:

```javascript
localStorage.setItem(
    "instituteId",
    getInstituteIdFromJwt(data.accessToken)
);

localStorage.setItem(
    "instituteRole",
    getInstituteRoleFromJwt(data.accessToken)
);
```

The JWT itself should still be validated by the backend. Frontend-decoded claims must not be treated as trusted authorization data.

---

# Error Handling

Authentication failures should return an appropriate HTTP response instead of exposing sensitive information.

Typical authentication failure:

```http
401 Unauthorized
```

The client can then display a generic message such as:

```text
Invalid username or password
```

---

# Token Expiration

Access tokens are short-lived.

The login response contains:

```json
{
  "expiresIn": 900
}
```

which represents the token lifetime configured by the service.

When the access token expires, the client must authenticate again or use the refresh-token mechanism if one is configured.

---

# Development Notes

This service is currently being developed as part of the **CodeCluster microservices architecture**.

Authentication-related changes should be considered carefully because multiple services depend on the JWT structure.

When adding or changing JWT claims, verify the impact on:

* API Gateway
* Frontend
* User Service
* Institute Service
* Assessment Service
* Other services consuming JWT claims

---

# Future Improvements

Potential improvements include:

* Refresh-token support
* Token revocation
* Email verification
* Password reset
* Account activation/deactivation workflows
* Multi-factor authentication
* Improved role and permission management
* Login attempt tracking
* Security auditing
* OAuth2/OpenID Connect integration
* Centralized authentication configuration

---

# Maintainer

**CodeCluster Development Team**

Primary developer:

**Ritika Patil** ([@ritikapt](https://github.com/ritikapt))


---

## License

This project is currently part of the CodeCluster project and is intended for development and academic/project purposes.
