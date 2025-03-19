# Stage 1: Build the application
FROM maven:3-eclipse-temurin-23-alpine AS build
COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Create the runtime image
FROM eclipse-temurin:23-alpine
#Copy only the needed jar, and rename it to app.jar
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]