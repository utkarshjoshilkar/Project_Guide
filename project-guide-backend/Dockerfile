# =============================================================================
# Dockerfile — Spring Boot Backend (project-guide-backend)
# Multi-stage build: Stage 1 compiles, Stage 2 is the lean runtime image.
# =============================================================================

# ── Stage 1: Build ─────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

# Copy Maven wrapper and POM first — cached layer if source hasn't changed
COPY mvnw pom.xml ./
COPY .mvn .mvn

# Download dependencies (cached unless pom.xml changes)
RUN ./mvnw dependency:go-offline -B

# Copy source and build the fat JAR (skip tests — they run in CI, not Docker build)
COPY src ./src
RUN ./mvnw package -DskipTests -B

# ── Stage 2: Runtime ────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine AS runtime

WORKDIR /app

# Create non-root user for security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Copy only the fat JAR from the build stage
COPY --from=builder /app/target/*.jar app.jar

# Change ownership
RUN chown appuser:appgroup app.jar

USER appuser

# Expose the default Spring Boot port
EXPOSE 8080

# Health check — Docker will poll this every 30s
# The app must respond 200 to /api/auth/health before accepting traffic.
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD wget -qO- http://localhost:8080/api/auth/health || exit 1

# Start Spring Boot with the prod profile
# Override SPRING_PROFILES_ACTIVE via docker-compose environment section
ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-jar", "app.jar"]
