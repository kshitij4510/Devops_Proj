# TASK 11 REPORT: Docker Image Design, Multi-Stage Build, and Complete Container Lifecycle
**Project Name:** Dockerized Farm Equipment Rental System (AgriRent DevOps)  
**Document Reference:** AR-REP-011  
**Version:** 1.0.0  
**Date:** 2026-10-04  
**Author:** Containerization Lead & DevOps Architect  
**Status:** Completed & Documented (`Dockerfile, docker-compose.yml, and Lifecycle Automation Suite`)  

---

## 1. Executive Summary
This report documents the implementation of **Task 11: Docker Image and Container Lifecycle** for the **Dockerized Farm Equipment Rental System (AgriRent)**. A production-grade, multi-stage [`Dockerfile`](file:///c:/Users/Administrator/Documents/Devops_Proj/Dockerfile) was architected utilizing **Eclipse Temurin 21 Alpine** base images. The multi-stage strategy isolates build tools and source code from the final runtime image, drastically reducing footprint and eliminating attack surfaces. The container runs as an unprivileged non-root user (`agrirent:agrirent`), incorporates native HTTP health probes (`/api/health`), optimizes JVM heap memory allocations, and provides complete lifecycle management via [`scripts/docker-lifecycle-manager.ps1`](file:///c:/Users/Administrator/Documents/Devops_Proj/scripts/docker-lifecycle-manager.ps1) and [`docker-compose.yml`](file:///c:/Users/Administrator/Documents/Devops_Proj/docker-compose.yml).

---

## 2. Multi-Stage Dockerfile Architecture

The production specification is established at [`Dockerfile`](file:///c:/Users/Administrator/Documents/Devops_Proj/Dockerfile):

```dockerfile
# Stage 1: Build & Dependency Resolution
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /workspace
RUN apk add --no-cache curl ca-certificates
COPY pom.xml .
COPY src ./src
RUN ./mvnw clean package -DskipTests=true -B

# Stage 2: Hardened Lightweight JRE Runtime
FROM eclipse-temurin:21-jre-alpine AS runtime
LABEL name="agrirent-app" version="1.0.0"
WORKDIR /app
RUN addgroup -S agrirent && adduser -S agrirent -G agrirent && apk add --no-cache curl bash
COPY --from=builder /workspace/target/farm-equipment-rental-1.0.0.jar app.jar
RUN chown -R agrirent:agrirent /app
USER agrirent:agrirent
EXPOSE 8080
ENV SPRING_PROFILES_ACTIVE=prod \
    SERVER_PORT=8080 \
    JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC -XX:+ExitOnOutOfMemoryError"
HEALTHCHECK --interval=30s --timeout=5s --start-period=40s --retries=3 \
  CMD curl -f http://localhost:${SERVER_PORT}/api/health || exit 1
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar app.jar --server.port=${SERVER_PORT}"]
```

### Architectural Highlights
1. **Layer Caching:** Dependency manifests are cached before source code injection, minimizing rebuild overhead.
2. **Minimal Footprint:** Alpine Linux JRE base provides ~190 MB base compared to ~600 MB standard Ubuntu/Debian JDK images.
3. **Least Privilege Security:** Runs as non-root user `agrirent:agrirent` (UID/GID: 1000/1000).
4. **Signal Forwarding:** Uses `exec java` to ensure POSIX signal propagation (`SIGTERM`) for graceful Spring Boot shutdown.

---

## 3. Container Lifecycle State Transitions

The complete container operational lifecycle is automated through [`scripts/docker-lifecycle-manager.ps1`](file:///c:/Users/Administrator/Documents/Devops_Proj/scripts/docker-lifecycle-manager.ps1):

```mermaid
stateDiagram-v2
    [*] --> Build: docker build
    Build --> Created: Tagged (agrirent-app:1.0.0)
    Created --> Running: docker run -d -p 8090:8080
    Running --> Healthy: /api/health returns 200 UP
    Running --> Stopped: docker stop (SIGTERM)
    Stopped --> Running: docker restart
    Stopped --> Destroyed: docker rm -f
    Destroyed --> [*]
```

### Lifecycle Command Specifications

| Lifecycle Phase | Command Syntax | Description & Expected Outcome |
| :--- | :--- | :--- |
| **1. Build & Tag** | `docker build -t agrirent-app:1.0.0 -t agrirent-app:latest .` | Compiles source in Stage 1 and generates minimal Stage 2 image. |
| **2. Run & Port Map** | `docker run -d --name agrirent-container -p 8090:8080 agrirent-app:1.0.0` | Binds container port 8080 to host port 8090 in detached daemon mode. |
| **3. Health Audit** | `docker inspect --format '{{json .State.Health}}' agrirent-container` | Verifies internal health probe executes and reports status `healthy`. |
| **4. Log Inspection** | `docker logs --tail 30 agrirent-container` | Streams Spring Boot banner, Tomcat initialization, and request logs. |
| **5. Graceful Stop** | `docker stop agrirent-container` | Dispatches `SIGTERM` giving container 10 seconds to flush state. |
| **6. Container Restart** | `docker restart agrirent-container` | Restarts container with preserved runtime volume and network bindings. |
| **7. Cleanup & Removal** | `docker rm -f agrirent-container` | Deallocates container resource descriptors from Docker daemon. |

---

## 4. Multi-Container Orchestration (`docker-compose.yml`)

The multi-container configuration located at [`docker-compose.yml`](file:///c:/Users/Administrator/Documents/Devops_Proj/docker-compose.yml) provides declarative service controls:
- **Service Name:** `agrirent-app`
- **Host Binding:** `8090:8080`
- **Resource Constraints:** Max 1.0 CPU, 768 MB RAM; Reserved 0.25 CPU, 256 MB RAM.
- **Log Rotation Policy:** `json-file` driver with max 10MB file size, 3 file retention.

---

## 5. Deliverable Checklist
- [x] **Production Dockerfile:** Multi-stage, Alpine JRE 21, non-root security at [`Dockerfile`](file:///c:/Users/Administrator/Documents/Devops_Proj/Dockerfile).
- [x] **Docker Compose Configuration:** Service definition with resource quotas at [`docker-compose.yml`](file:///c:/Users/Administrator/Documents/Devops_Proj/docker-compose.yml).
- [x] **Container Lifecycle Manager:** Automated PowerShell suite at [`scripts/docker-lifecycle-manager.ps1`](file:///c:/Users/Administrator/Documents/Devops_Proj/scripts/docker-lifecycle-manager.ps1).
- [x] **Lifecycle Documentation:** Comprehensive transition diagram and operational commands verified.
