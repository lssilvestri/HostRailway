FROM maven:3-eclipse-temurin-22-alpine
COPY target/huellitas-backend-0.0.1-SNAPSHOT.jar.original app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]