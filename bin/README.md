# 🎥 YouTube Live Monitor

Microserviço Spring Boot para monitoramento automático de lives no YouTube utilizando webhooks PubSubHubbub.

## 📋 Funcionalidades

- **Busca Inteligente de Canais**: Busque por nome ou URL (@handle ou /channel/ID)
- **Sistema de Confirmação**: Selecione o canal correto antes de adicionar ao monitoramento
- **Monitoramento em Tempo Real**: Recebe notificações instantâneas via PubSubHubbub
- **Filtro de Lives**: Diferencia vídeos comuns de transmissões ao vivo
- **Renovação Automática**: Renova subscrições a cada 48 horas
- **Banco H2 em Memória**: Fácil para testes e desenvolvimento

## 🛠️ Tecnologias Utilizadas

- **Spring Boot 3.2.0** - Framework principal
- **Spring Data JPA** - Persistência de dados
- **H2 Database** - Banco de dados em memória
- **YouTube Data API v3** - Integração com YouTube
- **Lombok** - Redução de boilerplate
- **PubSubHubbub** - Webhooks do Google

## 📦 Pré-requisitos

- **Java 17** ou superior
- **Maven 3.6+**
- **Chave da YouTube Data API** (https://console.cloud.google.com/)
- **Ngrok** (para expor localhost publicamente)

## 🚀 Como Rodar o Projeto

### 1. Configurar a YouTube API Key

Edite o arquivo `src/main/resources/application.properties`:

```properties
youtube.api.key=SUA_CHAVE_API_AQUI
```

### 2. Compilar o projeto

```bash
mvn clean install
```

### 3. Iniciar a aplicação

```bash
mvn spring-boot:run
```

A aplicação estará rodando em `http://localhost:8080`

### 4. Expor com Ngrok

Em outro terminal, execute:

```bash
ngrok http 8080
```

Você verá uma saída como:

```
Forwarding   https://abc123.ngrok.io -> http://localhost:8080
```

### 5. Atualizar a Callback URL

Edite novamente o `application.properties` com a URL do ngrok:

```properties
app.callback.url=https://abc123.ngrok.io/api/v1/notifications
```

**Importante**: Reinicie a aplicação após alterar a callback URL.

## 📝 Exemplos de Uso com CURL

### 1. Buscar Canais

**Por nome:**
```bash
curl -X POST "http://localhost:8080/api/v1/channels/search?query=MrBeast"
```

**Por URL com @handle:**
```bash
curl -X POST "http://localhost:8080/api/v1/channels/search?query=https://youtube.com/@MrBeast"
```

**Por URL com ID do canal:**
```bash
curl -X POST "http://localhost:8080/api/v1/channels/search?query=https://youtube.com/channel/UCX6OQ3DkcsbYNE6H8uQQuVA"
```

**Resposta esperada:**
```json
{
  "success": true,
  "message": "Canal encontrado! Use /channels/confirm/{channelId} para adicionar ao monitoramento.",
  "data": [
    {
      "channelId": "UCX6OQ3DkcsbYNE6H8uQQuVA",
      "displayName": "MrBeast",
      "url": "https://youtube.com/channel/UCX6OQ3DkcsbYNE6H8uQQuVA",
      "thumbnailUrl": "https://...",
      "description": "...",
      "subscriberCount": 123456789
    }
  ]
}
```

### 2. Confirmar e Adicionar Canal

```bash
curl -X POST "http://localhost:8080/api/v1/channels/confirm/UCX6OQ3DkcsbYNE6H8uQQuVA"
```

**Resposta esperada:**
```json
{
  "success": true,
  "message": "Canal adicionado com sucesso ao monitoramento!",
  "data": {
    "id": "UCX6OQ3DkcsbYNE6H8uQQuVA",
    "displayName": "MrBeast",
    "url": "https://youtube.com/channel/UCX6OQ3DkcsbYNE6H8uQQuVA",
    "lastSubscriptionDate": "2026-02-05T14:30:00",
    "subscribed": true
  }
}
```

### 3. Listar Todos os Canais Monitorados

```bash
curl "http://localhost:8080/api/v1/channels"
```

### 4. Listar Apenas Canais com Subscrição Ativa

```bash
curl "http://localhost:8080/api/v1/channels/active"
```

### 5. Obter Detalhes de um Canal Específico

```bash
curl "http://localhost:8080/api/v1/channels/UCX6OQ3DkcsbYNE6H8uQQuVA"
```

### 6. Remover um Canal do Monitoramento

```bash
curl -X DELETE "http://localhost:8080/api/v1/channels/UCX6OQ3DkcsbYNE6H8uQQuVA"
```

## 🔔 Como Funciona o Monitoramento

1. **Subscrição**: Quando você confirma um canal, o serviço se inscreve no Hub do Google (PubSubHubbub)
2. **Verificação**: O Google envia um GET para `/api/v1/notifications` com um challenge
3. **Notificação**: Quando há novo conteúdo, o Google envia um POST com XML
4. **Filtragem**: O serviço verifica se o vídeo é uma live ativa usando a YouTube API
5. **Alerta**: Se for live, aparece no console:

```
[ALERTA DE LIVE] O canal MrBeast está ao vivo agora! Link: https://youtube.com/watch?v=abc123
```

## 📊 Acessar o Console H2

Acesse: `http://localhost:8080/h2-console`

**Configurações:**
- JDBC URL: `jdbc:h2:mem:youtubedb`
- Username: `sa`
- Password: (deixe em branco)

## ⚙️ Endpoints da API

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/api/v1/channels/search?query={query}` | Busca canais |
| POST | `/api/v1/channels/confirm/{channelId}` | Adiciona canal ao monitoramento |
| GET | `/api/v1/channels` | Lista todos os canais |
| GET | `/api/v1/channels/active` | Lista canais ativos |
| GET | `/api/v1/channels/{channelId}` | Detalhes de um canal |
| DELETE | `/api/v1/channels/{channelId}` | Remove canal |
| GET | `/api/v1/notifications` | Validação do webhook |
| POST | `/api/v1/notifications` | Recebimento de notificações |

## 📄 Estrutura do Projeto

```
youtube-live-monitor/
├── src/main/java/com/youtube/livemonitor/
│   ├── YouTubeLiveMonitorApplication.java
│   ├── config/
│   │   └── YouTubeConfig.java
│   ├── controller/
│   │   ├── ChannelController.java
│   │   └── NotificationController.java
│   ├── dto/
│   │   ├── ApiResponse.java
│   │   └── ChannelSearchResult.java
│   ├── entity/
│   │   └── Channel.java
│   ├── repository/
│   │   └── ChannelRepository.java
│   └── service/
│       ├── ChannelService.java
│       ├── NotificationService.java
│       ├── PubSubHubbubService.java
│       └── YouTubeService.java
├── src/main/resources/
│   └── application.properties
└── pom.xml
```

## 🐛 Troubleshooting

### Erro: "Invalid API Key"
- Verifique se a chave da API está correta em `application.properties`
- Certifique-se de que a YouTube Data API v3 está habilitada no seu projeto do Google Cloud

### Não recebo notificações
- Verifique se o ngrok está rodando e a URL está atualizada
- Confirme que a aplicação foi reiniciada após atualizar a callback URL
- Verifique os logs para ver se o Hub do Google está validando a subscrição

### Subscrição não ativa
- Verifique sua conexão com a internet
- Veja os logs para identificar erros na chamada ao Hub do Google
- Certifique-se de que a callback URL é acessível publicamente
