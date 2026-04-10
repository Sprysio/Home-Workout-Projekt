# syntax=docker/dockerfile:1

# Build stage with Maven
FROM maven:3.9.14-eclipse-temurin-25-alpine@sha256:ce377824c0dccc129e5595e13468b74a3993e0a4d8db45335c6fe654387b07e7 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -q
COPY src/ src/
RUN mvn package -DskipTests -q && \
    java -Djarmode=tools -jar target/*-SNAPSHOT.jar extract --layers --launcher --destination extracted

# Runtime stage with minimal JRE
FROM eclipse-temurin:25-jre-alpine-3.23@sha256:f10d6259d0798c1e12179b6bf3b63cea0d6843f7b09c9f9c9c422c50e44379ec
LABEL maintainer="app-team" \
      version="1.0.0" \
      description="Auth Service - JWT authentication and user management"
WORKDIR /app
RUN apk upgrade --no-cache && addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser
COPY --from=builder /app/extracted/dependencies/ ./
COPY --from=builder /app/extracted/spring-boot-loader/ ./
COPY --from=builder /app/extracted/snapshot-dependencies/ ./
COPY --from=builder /app/extracted/application/ ./
EXPOSE 8080
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]