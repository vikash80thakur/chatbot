# 🚀 Organization Service – Microservices Backend
## 📌 Overview

This project is part of a microservices-based backend system where:

Organization Service manages organizations, users, and projects
API Gateway handles routing, authentication, and security

It demonstrates a real-world architecture with:

Spring Boot
Spring Cloud Gateway
JWT Authentication
Hybrid data access (JPA + JDBC Template)
PostgreSQL

---

## 🧠 Key Features

### 🔹 Organization Module (JPA)

* Full CRUD operations
* Automatic table creation using JPA
* Clean layered architecture (Controller → Service → Repository)

### 🔹 User Module (JDBC Template)

* Full CRUD operations
* Email validation using `@Email`
* Unique email constraint (DB + Service level)
* Manual SQL queries using JdbcTemplate

### 🔹 Project Module (JDBC Template)

* CRUD operations for projects
* Linked with organization via foreign key
* Supports aggregation with organization

---

## 🔐 Authentication & Security (NEW 🚀)

### ✅ JWT-based Authentication

* Users login via:

```
POST /auth/login
```

* Credentials are validated using the database
* JWT token is generated upon successful login

---

### ✅ API Gateway Security

* All requests pass through the API Gateway
* JWT token is validated using a custom Global Filter
* Unauthorized requests are blocked

---

### ✅ Header Propagation

* Gateway extracts user identity from JWT
* Passes it to microservices via headers:

```
X-User: <username>
```

---

### ✅ Centralized Error Handling

* All errors are handled at the Gateway level
* Returns consistent JSON responses

---

## 🔁 Request Flow (Important)

```
Client
   ↓
Login API (Organization Service)
   ↓
JWT Generated
   ↓
Client sends token
   ↓
API Gateway (JWT validation)
   ↓
Routes request to Organization Service
```




## 🔗 Database Design

* **Organization**
* **Users** (linked via `organization_id`)
* **Projects** (linked via `organization_id`)

---

## 🔥 Context API (Main Highlight)

### Endpoint:

```
GET /internal/context/{orgId}
```

### Description:

Aggregates data from multiple sources and returns:

* Organization details
* Associated users
* Associated projects

### Example Response:

```json
{
  "organization": {
    "id": 1,
    "name": "Google",
    "address": "Hyderabad"
  },
  "users": [
    {
      "id": 1,
      "name": "Vikash",
      "email": "vikash@gmail.com"
    }
  ],
  "projects": [
    {
      "id": 1,
      "name": "AI Platform",
      "description": "ML based project"
    }
  ]
}
```

---

## 🛠️ Tech Stack

* Java
* Spring Boot
* JDBC Template
* Spring Data JPA
* PostgreSQL
* Maven / Gradle

---

## ⚙️ Database Setup

Tables are auto-created using:

```
src/main/resources/schema.sql
```

Make sure to enable:

```
spring.sql.init.mode=always
```

---

## 🧪 API Endpoints

### Organization

* POST `/orgs`
* GET `/orgs`
* GET `/orgs/{id}`
* PUT `/orgs/{id}`
* DELETE `/orgs/{id}`

### Users

* POST `/users`
* GET `/users`
* GET `/users/{id}`
* PUT `/users/{id}`
* DELETE `/users/{id}`

### Projects

* POST `/projects`
* GET `/projects`

---

## ⚠️ Validation & Error Handling

* Email format validation using `@Email`
* Unique email constraint
* Global exception handling using `@ControllerAdvice`

---


---

## 🏗️ Architecture
<img width="1536" height="1024" alt="ChatGPT Image Apr 15, 2026, 11_49_43 AM" src="https://github.com/user-attachments/assets/9ad7d18e-0993-4a18-81c4-786ff9d66e4b" />

---

## 🐳 Docker Support (Optional)

Project is structured to support:

* Docker containerization
* docker-compose setup for multi-service environment

---

## 💡 Learning Highlights

* Hybrid approach (JPA + JDBC Template)
* Manual SQL handling
* Aggregation API design
* Clean architecture principles
* Real-world backend practices

---

## 🚀 Future Improvements

* Add Swagger documentation
* Implement Flyway for DB migrations
* Add authentication (JWT)
* Improve logging & monitoring

---

## 👨‍💻 Author

Vikash Thakur & Girish Hardia 
