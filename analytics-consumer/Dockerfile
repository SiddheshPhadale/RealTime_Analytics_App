FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY commonModule ./commonModule
COPY analytics-consumer ./analytics-consumer

RUN mvn -B -f commonModule/pom.xml install -DskipTests
WORKDIR /workspace/analytics-consumer
RUN mvn -B package -DskipTests

FROM eclipse-temurin:21-jre-alpine AS runtime
WORKDIR /app
COPY --from=build /workspace/analytics-consumer/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
