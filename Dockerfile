# ==============================================================================
# Multi-Stage Dockerfile: Farm Equipment Rental System (AgriRent DevOps)
# Java 21 LTS + Spring Boot 3.3.4 + Production Hardened Alpine Runtime
# ==============================================================================

# ------------------------------------------------------------------------------
# Stage 1: Dependency Caching & Compilation (Build Stage)
# ------------------------------------------------------------------------------
FROM eclipse-temurin:21-jdk-alpine AS builder

LABEL maintainer="AgriRent DevOps Team <devops@agrirent.com>"
LABEL stage="builder"

WORKDIR /workspace

# Install curl and ca-certificates for Maven downloads
RUN apk add --no-cache curl ca-certificates

# Cache dependencies layer
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw* .
RUN sed -i 's/\r$//' mvnw && chmod +x mvnw

# Pre-fetch dependencies to leverage Docker layer caching
RUN ./mvnw dependency:go-offline -B || true

# Copy source code and build production artifact
COPY src ./src
RUN ./mvnw clean package -DskipTests=true -B

# ------------------------------------------------------------------------------
# Stage 2: Minimal Production JRE Runtime
# ------------------------------------------------------------------------------
FROM eclipse-temurin:21-jre-alpine AS runtime

LABEL name="agrirent-app"
LABEL version="1.0.0"
LABEL description="Dockerized Farm Equipment Rental System"

WORKDIR /app

# Security: Add dedicated unprivileged system group and user
RUN addgroup -S agrirent && adduser -S agrirent -G agrirent

# Install curl for container health check probes
RUN apk add --no-cache curl bash

# Copy executable jar from builder stage
COPY --from=builder /workspace/target/farm-equipment-rental-1.0.0.jar app.jar

# Set file ownership to non-root user
RUN chown -R agrirent:agrirent /app

# Switch to non-root user
USER agrirent:agrirent

# Application Port
EXPOSE 8080

# Environment Defaults
ENV SPRING_PROFILES_ACTIVE=prod \
    SERVER_PORT=8080 \
    JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC -XX:+ExitOnOutOfMemoryError"

# Container Healthcheck probe
HEALTHCHECK --interval=30s --timeout=5s --start-period=40s --retries=3 \
  CMD sh -c 'curl -f http://localhost:${PORT:-$SERVER_PORT}/api/health || exit 1'

# Launch Entrypoint with signal forwarding
ENTRYPOINT ["sh", "-c", "PORT=${PORT:-$SERVER_PORT}; exec java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar app.jar --server.port=$PORT"]
