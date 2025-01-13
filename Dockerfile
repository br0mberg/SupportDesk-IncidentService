FROM maven:3.9.4-eclipse-temurin-21 AS builder
WORKDIR /incident-service
COPY pom.xml ./
RUN mvn dependency:go-offline
COPY ./src ./src
RUN mvn clean install -e


FROM openjdk:21-jdk-slim
WORKDIR /incident-service
COPY --from=builder /incident-service/target/*.jar /incident-service/incident-service-app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/incident-service/incident-service-app.jar"]
