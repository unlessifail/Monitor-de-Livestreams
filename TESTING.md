# Protocolo de Testes de Engenharia – Monitor de Livestreams

Este documento estabelece os procedimentos para validação técnica da API e do fluxo de eventos PubSubHubbub do microserviço de monitoramento de lives do YouTube.

---

## 1. Pré-requisitos de Infraestrutura

Antes de iniciar os testes, certifique-se de que os seguintes requisitos estão atendidos:

- **Servidor Backend**: Aplicação Spring Boot ativa em `http://localhost:8080`.
- **Ingress (Webhook)**: Túnel Ngrok operacional apontando para a porta `8080`.
- **Credenciais**: Chave da YouTube Data API v3 configurada corretamente no arquivo `application.properties`.

---

## 2. Cenários de Teste – API REST

### 2.1 Discovery por Nome ou Handle

Valida a integração entre o serviço local e a YouTube Data API v3 para busca de canais.

```bash
curl -X POST "http://localhost:8080/api/v1/channels/search?query=Alanzoka"
```

**Expectativa**:
- Status HTTP: `200 OK`
- Corpo da resposta: Objeto JSON contendo uma lista de canais com os campos:
  - `id`
  - `displayName`
  - `thumbnailUrl`

---

### 2.2 Handshake Dinâmico (Subscrição)

Testa a lógica de confirmação do canal e a injeção dinâmica da URL do túnel Ngrok no processo de subscrição ao Hub do Google.

```bash
# Substitua {NGROK_URL} pela URL HTTPS gerada pelo Ngrok
curl -X POST "http://localhost:8080/api/v1/channels/confirm/UCu4bD0bssKf8FspfOs15EDA?callback={NGROK_URL}"
```

**Expectativa**:
- Status HTTP: `200 OK`
- Logs da aplicação devem indicar o envio da requisição de subscrição (`hub.mode=subscribe`) ao Hub do Google.

---

### 2.3 Gestão de Estado de Monitoramento

#### Listagem de canais monitorados

```bash
curl "http://localhost:8080/api/v1/channels"
```

**Expectativa**:
- Status HTTP: `200 OK`
- Retorno com a lista de canais atualmente persistidos e ativos.

#### Encerramento de monitoramento (Unsubscribe)

```bash
curl -X DELETE "http://localhost:8080/api/v1/channels/UCu4bD0bssKf8FspfOs15EDA"
```

**Expectativa**:
- Status HTTP: `200 OK`
- Logs indicando o envio da requisição de cancelamento (`hub.mode=unsubscribe`) ao Hub do Google.

---

## 3. Simulação de Eventos – WebSub (PubSubHubbub)

### 3.1 Validação de Desafio (GET Handshake)

Simula a requisição de verificação enviada pelo Google para validar a acessibilidade do endpoint de callback.

```bash
curl -G "http://localhost:8080/api/v1/notifications" \
  --data-urlencode "hub.mode=subscribe" \
  --data-urlencode "hub.challenge=hexenkult_verify_123" \
  --data-urlencode "hub.topic=https://www.youtube.com/xml/feeds/videos.xml?channel_id=UCtest"
```

**Expectativa**:
- Status HTTP: `200 OK`
- Corpo da resposta: texto puro (`text/plain`) contendo exclusivamente o valor `hexenkult_verify_123`.

---

### 3.2 Notificação de Payload (POST Notification)

Simula o recebimento de uma notificação Atom/XML indicando a publicação de um novo vídeo ou live.

```bash
curl -X POST "http://localhost:8080/api/v1/notifications" \
  -H "Content-Type: application/atom+xml" \
  -d '<?xml version="1.0" encoding="UTF-8"?>
<feed xmlns:yt="http://www.youtube.com/xml/schemas/2015" xmlns="http://www.w3.org/2005/Atom">
  <entry>
    <yt:videoId>dQw4w9WgXcQ</yt:videoId>
    <yt:channelId>UCu4bD0bssKf8FspfOs15EDA</yt:channelId>
    <title>Stream Test</title>
    <link rel="alternate" href="https://www.youtube.com/watch?v=dQw4w9WgXcQ"/>
  </entry>
</feed>'
```

**Expectativa**:
- Status HTTP: `200 OK`
- Logs da aplicação indicando:
  - Processamento do payload
  - Chamada subsequente à YouTube Data API para verificação do status do vídeo
  - Geração do alerta apenas se o conteúdo estiver ao vivo

---

## 4. Auditoria de Dados – H2 Console

Para inspeção direta da camada de persistência:

- **URL**: `http://localhost:8080/h2-console`
- **JDBC URL**: `jdbc:h2:mem:youtubedb`
- **Usuário**: `sa`
- **Senha**: (em branco)

Consulta SQL recomendada:

```sql
SELECT id, display_name, last_subscription_date, is_active
FROM channels;
```

---

## 5. Diagnóstico de Falhas (Troubleshooting)

| Sintoma                | Causa Provável                   | Resolução                                                                 |
|------------------------|----------------------------------|---------------------------------------------------------------------------|
| Erro 403 no Ngrok      | Bloqueio de host                 | Iniciar o Ngrok com `--host-header="localhost:8080"`                    |
| Handshake falho        | URL de callback inválida          | Garantir que a URL configurada utilize HTTPS                              |
| Busca sem resultados   | Quota da API excedida             | Verificar limites da API Key no Google Cloud Console                       |

---

**Autor**: Kalil M. Santos  
**Cargo**: Project Architect  
**Organização**: Hexenkult – Coletivo de Desenvolvimento e Soluções Tecnológicas

