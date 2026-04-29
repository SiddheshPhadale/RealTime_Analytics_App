FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY commonModule ./commonModule
COPY analytics-producer ./analytics-producer

RUN mvn -B -f commonModule/pom.xml install -DskipTests
WORKDIR /workspace/analytics-producer
RUN mvn -B package -DskipTests

FROM eclipse-temurin:21-jre-alpine AS runtime
WORKDIR /app
COPY --from=build /workspace/analytics-producer/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
