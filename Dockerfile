# Multi-stage build: build the JAR with Maven, then run it with a slim JRE image
FROM maven:3.9.9-eclipse-temurin-21-alpine AS build

WORKDIR /app

# Copy Maven descriptor and sources
COPY pom.xml .
COPY src ./src

# Build the application JAR (skip tests for faster container builds)
RUN mvn -B -DskipTests clean package

# Runtime image
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy the built JAR from the build stage
COPY --from=build /app/target/InvoiceGenerator-0.0.1-SNAPSHOT.jar app.jar

# Expose the application port (matches server.port)
EXPOSE 9090

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]
