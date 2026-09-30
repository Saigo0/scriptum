# syntax=docker/dockerfile:1.7

# FASE 1: Construção (Build)
FROM gradle:8.14-jdk21 AS build
WORKDIR /workspace

COPY --chmod=755 gradlew gradlew
COPY gradle gradle
COPY build.gradle settings.gradle gradle.properties ./
RUN sed -i 's/\r$//' gradlew
RUN ./gradlew --no-daemon dependencies

COPY src src
RUN ./gradlew --no-daemon clean build -x test

# FASE 2: Rodar o projeto (Runtime)
FROM eclipse-temurin:21-jre-jammy AS runtime
ENV JAVA_TOOL_OPTIONS="-XX:InitialRAMPercentage=25.0 -XX:MaxRAMPercentage=75.0"

WORKDIR /app
# O Quarkus sempre gera a pasta quarkus-app, não importa qual seja o microsserviço
COPY --from=build /workspace/build/quarkus-app/ ./

# O serviço auth vai rodar na 8082
EXPOSE 8082

ENTRYPOINT ["java", "-jar", "quarkus-run.jar"]