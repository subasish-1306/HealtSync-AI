# HealthSync AI Backend

AI-Driven Health Center & Supply Chain Management Platform.

## Technology Stack
* Java 21 (Eclipse Temurin JRE)
* Spring Boot 3.4
* PostgreSQL & Flyway
* Spring Security (JWT-based Stateless Auth)
* Lombok & ModelMapper
* Swagger / OpenAPI 3

---

## Getting Started

### Local Setup (Development)
1. Start local PostgreSQL instance and create database `healthsync_db`.
2. Configure environment variables or use the default `application-dev.yml` settings.
3. Build and run:
   ```bash
   mvn clean spring-boot:run
   ```

### Compile & Run Tests
To run all 9 domain integration tests:
```bash
mvn clean test
```

---

## Docker Deployment (Production)

### Multi-stage Build
The `Dockerfile` is optimized to build the code and run the executable JAR inside a secure, lightweight Alpine JRE environment as a non-root `appuser`.

### Build & Run via Docker Compose
To build and start both the PostgreSQL database and backend service:
```bash
docker-compose up --build -d
```

### Health Check
Once running, check the container status:
```bash
curl http://localhost:8080/api/health
```

---

## API Documentation
Once the application starts, Swagger/OpenAPI documentation is available at:
* Swagger UI: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
* API Docs JSON: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)
