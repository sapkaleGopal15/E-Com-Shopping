# ============================
# Stage 1: Build Application
# ============================
FROM eclipse-temurin:21-jdk AS builder

WORKDIR /app

# Copy Maven Wrapper (if available)
COPY mvnw .
COPY .mvn .mvn

# Copy pom.xml
COPY pom.xml .

# Download dependencies
RUN chmod +x mvnw && ./mvnw dependency:go-offline

# Copy source code
COPY src src

# Build Spring Boot application
RUN ./mvnw clean package -DskipTests

# ============================
# Stage 2: Run Application
# ============================
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy generated JAR
COPY --from=builder /app/target/Gopal-Shopping-0.0.1-SNAPSHOT.jar app.jar

# Render provides PORT environment variable
ENV PORT=10000

EXPOSE 10000

ENTRYPOINT ["java","-Dserver.port=${PORT}","-jar","app.jar"]
