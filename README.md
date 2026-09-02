# Evangelizae API

Backend Java/Spring do Evangelizae. O escopo inicial e deliberadamente pequeno: receber a liturgia diaria de um provedor normalizado e entrega-la ao frontend sem inventar conteudo nem apresentar uma data antiga como se fosse a atual.

## Tecnologia

- Java 21
- Spring Boot 4.1
- Maven
- configuracao em `src/main/resources/application.yml`
- contrato OpenAPI em `openapi/evangelizae-v1.openapi.yml`

## Executar localmente

Requisitos: JDK 21+ e Maven 3.9+.

```bash
cp .env.example .env
set -a && source .env && set +a
mvn spring-boot:run
```

A API inicia em `http://localhost:8080/api/v1`. O health check fica em `http://localhost:8080/api/v1/actuator/health`.

Tambem e possivel executar sem Maven local:

```bash
docker build -t evangelizae-api .
docker run --rm --env-file .env -p 8080:8080 evangelizae-api
```

## Endpoint $inicial$

```http
GET /api/v1/liturgy/today?timezone=America/Sao_Paulo&locale=pt-BR
Accept: application/json
```

O backend calcula a data no fuso pedido, consulta o provedor e valida a resposta. Se o provedor falhar, somente um cache da mesma data e do mesmo locale pode ser retornado com `source.freshness: CACHED`. Sem esse cache, a API responde `503 LITURGY_UNAVAILABLE`.

## Contrato do provedor

Defina `LITURGY_PROVIDER_ENABLED=true` e `LITURGY_PROVIDER_BASE_URL` com a URL de um provedor que devolva JSON normalizado. A API acrescenta os query parameters `date=YYYY-MM-DD` e `locale=pt-BR`.

Exemplo estrutural (texto liturgico omitido intencionalmente):

```json
{
  "date": "2026-08-24",
  "title": "Titulo liturgico fornecido por fonte autorizada",
  "color": "GREEN",
  "prayers": {},
  "groups": [
    {
      "kind": "GOSPEL",
      "items": [
        {
          "title": "Proclamacao do Evangelho",
          "reference": "Referencia fornecida pela fonte",
          "text": "Texto licenciado fornecido pela fonte"
        }
      ]
    }
  ]
}
```

Valores aceitos:

- `color`: `GREEN`, `WHITE`, `RED`, `PURPLE`, `ROSE`
- `kind`: `FIRST_READING`, `PSALM`, `SECOND_READING`, `GOSPEL`, `EXTRA`

Nenhum texto catolico e embutido como fallback. A escolha da fonte, sua autorizacao de uso e o mapeamento do formato original devem ser definidos antes de habilitar producao.

## Verificacao

```bash
mvn test
mvn package
```
