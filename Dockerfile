FROM maven:3-eclipse-temurin-23-alpine
COPY . .
RUN mvn clean package -DskipTests

FROM clipse-temurin:23-alpine
COPY --from=build /target/*.jar demo.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar","demo.jar"]