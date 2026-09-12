# 🔎 Scriptum OCR

Serviço de reconhecimento óptico de caracteres (OCR) desenvolvido com
**Quarkus**, **Tesseract** e **RabbitMQ**.

O serviço pode receber imagens por HTTP ou processá-las de forma assíncrona
por uma fila.

## 📋 Pré-requisitos

- Java 21
- Docker e Docker Compose, somente para executar o ambiente completo
- RabbitMQ, somente para testar a integração real com a fila

O projeto possui Gradle Wrapper, portanto não é necessário instalar o Gradle:

```bash
./gradlew --version
```

## 🚀 Executar em modo desenvolvimento

O modo desenvolvimento habilita recarregamento automático:

```bash
./gradlew quarkusDev
```

Endpoints úteis:

- API OCR: <http://localhost:8080/ocr>
- Swagger UI: <http://localhost:8080/q/swagger-ui>
- Métricas Prometheus: <http://localhost:8080/q/metrics>
- Quarkus Dev UI: <http://localhost:8080/q/dev>

## 📦 Compilar e executar

Gerar o pacote Quarkus:

```bash
./gradlew build
```

Executar o artefato gerado:

```bash
java -jar build/quarkus-app/quarkus-run.jar
```

Gerar um JAR único:

```bash
./gradlew build -Dquarkus.package.jar.type=uber-jar
java -jar build/*-runner.jar
```

## 🌐 API REST

### `POST /ocr`

Processa uma imagem codificada em Base64.

Exemplo de requisição:

```json
{
  "documentId": 123,
  "language": "por",
  "imageBase64": "BASE64_DA_IMAGEM"
}
```

Idiomas disponíveis:

- `por` — português
- `eng` — inglês

Exemplo com `curl`:

```bash
curl -X POST http://localhost:8080/ocr \
  -H 'Content-Type: application/json' \
  -d '{"documentId":123,"language":"por","imageBase64":"BASE64_DA_IMAGEM"}'
```

Resposta esperada:

```json
{
  "documentId": 123,
  "paragraphs": ["Primeiro parágrafo", "Segundo parágrafo"],
  "confidence": 93.4
}
```

## 📨 Filas RabbitMQ

O serviço utiliza os seguintes canais:

| Canal | Finalidade |
|---|---|
| `ocr-processing` | Recebe solicitações de OCR |
| `ocr-reading-return` | Publica as respostas processadas |

As mensagens devem conter o mesmo JSON utilizado pela API REST.

### 🧪 Testar a fila sem RabbitMQ

O teste de integração utiliza o conector em memória:

```bash
./gradlew integrationTest --rerun-tasks
```

Esse teste valida o fluxo completo de entrada, processamento com Tesseract e
publicação da resposta.

### 🐇 Testar com RabbitMQ real

Na raiz do repositório, inicie RabbitMQ e o serviço OCR:

```bash
cd ../..
docker compose up -d rabbitmq ocr
```

Publique uma mensagem na fila de processamento:

```bash
curl -u admin:admin \
  -H 'content-type: application/json' \
  -X POST 'http://localhost:15672/api/exchanges/%2F/ocr.processing/publish' \
  --data-binary @- <<'JSON'
{
  "properties": {
    "content_type": "application/json"
  },
  "routing_key": "ocr.processing",
  "payload": "{\"documentId\":2026,\"language\":\"eng\",\"imageBase64\":\"BASE64_DA_IMAGEM\"}",
  "payload_encoding": "string"
}
JSON
```

Consulte a resposta publicada:

```bash
curl -u admin:admin \
  -H 'content-type: application/json' \
  'http://localhost:15672/api/queues/%2F/ocr-reading-return/get' \
  --data-binary @- <<'JSON'
{
  "count": 1,
  "ackmode": "ack_requeue_true",
  "encoding": "auto",
  "truncate": 50000
}
JSON
```

Painel do RabbitMQ: <http://localhost:15672>

- Usuário: `admin`
- Senha: `admin`

## 🧪 Testes

Executar todos os testes:

```bash
./gradlew test --rerun-tasks
```

Executar somente os testes unitários:

```bash
./gradlew unitTest --rerun-tasks
```

Executar somente os testes de integração:

```bash
./gradlew integrationTest --rerun-tasks
```

Os testes exibem:

- `[UNITÁRIO]` ou `[INTEGRAÇÃO]` ao iniciar
- `STARTED`, `PASSED`, `FAILED` ou `SKIPPED`
- Total de testes aprovados, falhos e ignorados

Relatórios HTML:

```text
build/reports/tests/test/index.html
build/reports/tests/unitTest/index.html
build/reports/tests/integrationTest/index.html
```

## 🐳 Executar com Docker Compose

Para iniciar o ambiente completo — OCR, RabbitMQ, Prometheus, Grafana e Kong:

```bash
cd ../..
cp .env.example .env
docker compose -f docker-compose.yml up --build
```

Por padrão, somente o gateway do Kong fica acessível no host. Para publicar
também as portas dos microsserviços, use o segundo arquivo:

```bash
docker compose -f docker-compose.external.yml up --build
```

O gateway do Kong fica sempre disponível em <http://localhost:8000>. A porta
administrativa do Kong (8001) permanece restrita ao host.

## 📊 Observabilidade

Os logs do serviço exibem horário e nível com cores no console, sem mostrar a
thread de execução. Mensagens de erro e validação são registradas em
português, sem expor o conteúdo Base64 das imagens.

## ⚙️ Configuração

As variáveis padrão de ambiente para RabbitMQ são:

| Variável | Padrão |
|---|---|
| `RABBITMQ_HOST` | `localhost` |
| `RABBITMQ_PORT` | `5672` |
| `RABBITMQ_USERNAME` | `admin` |
| `RABBITMQ_PASSWORD` | `admin` |

## 🧬 Build nativo

Com GraalVM configurado:

```bash
./gradlew build -Dquarkus.native.enabled=true
```

Usando build nativo em container:

```bash
./gradlew build \
  -Dquarkus.native.enabled=true \
  -Dquarkus.native.container-build=true
```
