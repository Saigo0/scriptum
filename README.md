# Scriptum

Ambiente completo do Scriptum, composto pelo serviço de OCR, RabbitMQ,
PostgreSQL, Kong, Prometheus e Grafana.

## 1. Pré-requisitos

- Docker Engine ou Docker Desktop;
- Docker Compose V2;
- Git, caso o projeto seja obtido de um repositório.

Verifique a instalação:

```bash
docker --version
docker compose version
```

O Java e o Gradle não são necessários para executar o ambiente completo,
porque o OCR é compilado dentro do Docker.

## 2. Configurar as variáveis de ambiente

Copie o arquivo de exemplo:

```bash
cp .env.example .env
```

Edite o `.env` para configurar banco, credenciais e portas:

```env
POSTGRES_DB=pagefolio
POSTGRES_USER=postgres
POSTGRES_PASSWORD=postgres

RABBITMQ_USERNAME=admin
RABBITMQ_PASSWORD=admin

GRAFANA_ADMIN_USER=admin
GRAFANA_ADMIN_PASSWORD=admin
```

O arquivo `.env` é lido automaticamente pelo Docker Compose. Ele não define
qual Compose será usado; a escolha é feita pelo arquivo informado no comando.

## 3. Escolher o Compose

Existem dois arquivos independentes:

### Portas privadas — `docker-compose.yml`

Use este arquivo quando os microsserviços devem ficar acessíveis somente
entre os containers. O gateway do Kong continua publicado no host.

```bash
docker compose -f docker-compose.yml up --build -d
```

### Portas publicadas — `docker-compose.external.yml`

Use este arquivo quando as portas do PostgreSQL, RabbitMQ, OCR, Prometheus e
Grafana também devem ficar acessíveis pelo host ou por uma rede externa.

```bash
docker compose -f docker-compose.external.yml up --build -d
```

## 4. Portas e serviços

| Serviço | Compose privado | Compose com portas publicadas |
| --- | --- | --- |
| Kong Gateway | `localhost:8000` | `localhost:8000` |
| Kong Admin | `127.0.0.1:8001` | `127.0.0.1:8001` |
| OCR | somente rede interna | `localhost:8080` |
| RabbitMQ AMQP | somente rede interna | `localhost:5672` |
| RabbitMQ Management | somente rede interna | `localhost:15672` |
| Prometheus | somente rede interna | `localhost:9090` |
| Grafana | somente rede interna | `localhost:3000` |
| PostgreSQL | somente rede interna | `localhost:5432` |

O gateway do Kong fica disponível em qualquer modo:

```text
http://localhost:8000
```

A API administrativa do Kong é restrita à máquina local:

```text
http://127.0.0.1:8001
```

## 5. Alterar portas publicadas

As portas do arquivo externo podem ser alteradas no `.env`:

```env
POSTGRES_PORT=5432
RABBITMQ_PORT=5672
RABBITMQ_MANAGEMENT_PORT=15672
OCR_PORT=8080
PROMETHEUS_PORT=9090
GRAFANA_PORT=3000
```

Essas variáveis só têm efeito quando o ambiente é iniciado com
`docker-compose.external.yml`.

## 6. Verificar o ambiente

Listar os containers:

```bash
docker compose -f docker-compose.yml ps
```

Para o arquivo externo:

```bash
docker compose -f docker-compose.external.yml ps
```

Ver os logs de todos os serviços:

```bash
docker compose -f docker-compose.yml logs -f
```

Ver somente os logs do OCR:

```bash
docker compose -f docker-compose.yml logs -f ocr
```

Substitua `docker-compose.yml` por `docker-compose.external.yml` quando esse
for o arquivo utilizado para iniciar o ambiente.

## 7. Acessar o OCR

### Acesso direto

O acesso direto fica disponível somente com o Compose externo:

```text
POST http://localhost:8080/ocr
```

Exemplo:

```bash
curl -X POST http://localhost:8080/ocr \
  -H 'Content-Type: application/json' \
  -d '{"documentId":123,"language":"por","imageBase64":"BASE64_DA_IMAGEM"}'
```

### Acesso pelo Kong

O Kong encaminha `/ocr` para o serviço OCR e exige a chave configurada em
`kong/kong.yml`:

```bash
curl -X POST http://localhost:8000/ocr \
  -H 'Content-Type: application/json' \
  -H 'apikey: scriptum-dev-key' \
  -d '{"documentId":123,"language":"por","imageBase64":"BASE64_DA_IMAGEM"}'
```

O acesso pelo Kong é o caminho recomendado quando as portas dos
microsserviços não devem ser publicadas.

## 8. Observabilidade

O Prometheus coleta as métricas do OCR pelo endereço interno:

```text
http://ocr:8080/q/metrics
```

No Compose externo, o Prometheus também fica disponível em:

```text
http://localhost:9090
```

O Grafana fica disponível em `http://localhost:3000` no Compose externo e
usa o Prometheus provisionado pelo ambiente.

## 9. Parar e remover o ambiente

Parar os containers sem remover dados:

```bash
docker compose -f docker-compose.yml stop
```

Remover containers e redes:

```bash
docker compose -f docker-compose.yml down
```

Remover também os volumes persistentes:

```bash
docker compose -f docker-compose.yml down -v
```

Use sempre o mesmo arquivo Compose usado para iniciar os serviços.
`down -v` apaga os dados locais do PostgreSQL, RabbitMQ, Prometheus e Grafana.

## 10. Solução de problemas

### Porta já está em uso

Altere a porta no `.env`, por exemplo:

```env
OCR_PORT=18080
```

Depois recrie o ambiente externo:

```bash
docker compose -f docker-compose.external.yml down
docker compose -f docker-compose.external.yml up --build -d
```

### O OCR não inicia

Consulte os logs:

```bash
docker compose -f docker-compose.yml logs --tail=200 ocr
docker compose -f docker-compose.yml logs --tail=200 rabbitmq
```

O OCR depende do RabbitMQ saudável antes de iniciar.

### Alterei o `.env`

Recrie os containers:

```bash
docker compose -f docker-compose.yml up -d --force-recreate
```

Se a alteração envolver o Dockerfile ou dependências do OCR:

```bash
docker compose -f docker-compose.yml up --build -d --force-recreate
```

## 11. Execução local somente do OCR

Para desenvolver o OCR sem iniciar todo o Compose:

```bash
cd ocr/scriptum-ocr
./gradlew quarkusDev
```

Nesse modo, o RabbitMQ precisa estar disponível e as variáveis usadas pelo
OCR devem ser configuradas conforme
`ocr/scriptum-ocr/src/main/resources/application.properties`.
