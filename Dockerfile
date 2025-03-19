# Stage 1: Build the application
FROM maven:3-eclipse-temurin-23-alpine AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Create the runtime image
FROM eclipse-temurin:23-alpine
COPY --from=build /app/target/huellitas-backend-0.0.1-SNAPSHOT.jar.original app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]