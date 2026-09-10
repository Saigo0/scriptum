FROM gradle:8.14-jdk21 AS build
WORKDIR /workspace
COPY . .
RUN ./gradlew --no-daemon clean build -x test

FROM eclipse-temurin:21-jre
RUN apt-get update \
    && apt-get install -y --no-install-recommends tesseract-ocr libtesseract-dev libleptonica-dev \
    && rm -rf /var/lib/apt/lists/*
WORKDIR /app
COPY --from=build /workspace/build/quarkus-app/ /app/
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/quarkus-run.jar"]
