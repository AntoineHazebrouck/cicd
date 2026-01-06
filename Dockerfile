FROM maven:3.9.12-eclipse-temurin-21-alpine
WORKDIR /app
COPY . /app
RUN mvn -B package -DskipTests
CMD ["java", "-jar", "/app/target/cicd-0.0.1-SNAPSHOT.jar"]
