# ─────────────────────────────────────────────────────────────────────────────
# Stage 1: Build stage — compile and package the Spring Boot fat-jar
# ─────────────────────────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

# Copy Maven wrapper and POM first for dependency caching
COPY pom.xml .

# Copy source and build
COPY src ./src
RUN apk add --no-cache maven \
    && mvn clean package -DskipTests

# ─────────────────────────────────────────────────────────────────────────────
# Stage 2: Runtime stage — lightweight JRE image
# ─────────────────────────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy the fat-jar from the builder stage
COPY --from=builder /app/target/*.jar app.jar

# Expose the service port
EXPOSE 8082

# Launch the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]
