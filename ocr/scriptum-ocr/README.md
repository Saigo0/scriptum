# scriptum-ocr

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: <https://quarkus.io/>.

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell script
./gradlew quarkusDev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at <http://localhost:8080/q/dev/>.

## Packaging and running the application

The application can be packaged using:

```shell script
./gradlew build
```

It produces the `quarkus-run.jar` file in the `build/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `build/quarkus-app/lib/` directory.

The application is now runnable using `java -jar build/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:

```shell script
./gradlew build -Dquarkus.package.jar.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar build/*-runner.jar`.

## Creating a native executable

You can create a native executable using:

```shell script
./gradlew build -Dquarkus.native.enabled=true
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using:

```shell script
./gradlew build -Dquarkus.native.enabled=true -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./build/scriptum-ocr-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult <https://quarkus.io/guides/gradle-tooling>.

## Provided Code

### REST

`POST /ocr` receives JSON with the document identifier, language (`por` or
`eng`) and the image encoded as Base64:

```json
{
  "documentId": 123,
  "language": "por",
  "imageBase64": "..."
}
```

The response contains the document identifier, one entry per paragraph and
the OCR confidence percentage:

```json
{
  "documentId": 123,
  "paragraphs": ["First paragraph", "Second paragraph"],
  "confidence": 93.4
}
```

RabbitMQ exposes `ocr-processing` and `ocr-reading-return` queues. The
processed response remains available in `ocr-reading-return` until it is
acknowledged by a consumer. It can be inspected in the RabbitMQ Management
UI under **Queues and Streams**. The application metrics are available at `/q/metrics`; the root
`docker-compose.yml` also starts Prometheus, Grafana and Kong. When using
Kong, send `apikey: scriptum-dev-key` with the OCR request.

[Related guide section...](https://quarkus.io/guides/getting-started-reactive#reactive-jax-rs-resources)
