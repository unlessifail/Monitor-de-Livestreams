# 🚀 GUIA DE EXECUÇÃO - YouTube Live Monitor

## 📦 Estrutura do Projeto

```
youtube-live-monitor/
├── src/main/java/com/youtube/livemonitor/
│   ├── YouTubeLiveMonitorApplication.java   # Classe principal
│   ├── model/
│   │   └── Channel.java                     # Entidade JPA
│   ├── repository/
│   │   └── ChannelRepository.java           # Repository Spring Data
│   ├── service/
│   │   ├── YouTubeApiService.java           # Integração YouTube API
│   │   ├── PubSubHubbubService.java         # Gerenciamento de webhooks
│   │   ├── ChannelService.java              # Lógica de negócio
│   │   └── NotificationService.java         # Processamento de notificações
│   ├── controller/
│   │   ├── ChannelController.java           # Endpoints de canais
│   │   └── NotificationController.java      # Webhook callback
│   └── dto/
│       ├── ChannelSearchResponse.java
│       ├── ChannelSearchResult.java
│       └── ApiResponse.java
├── src/main/resources/
│   └── application.properties               # Configurações
├── pom.xml                                   # Dependências Maven
├── README.md                                 # Documentação completa
├── QUICKSTART.md                            # Guia rápido
└── test-api.sh                              # Script de testes

```

---

## 🔧 PASSO 1: Configuração Inicial

### 1.1 YouTube API Key

1. Acesse: https://console.cloud.google.com/
2. Crie um novo projeto (ou selecione existente)
3. No menu lateral, vá em "APIs e Serviços" > "Biblioteca"
4. Busque por "YouTube Data API v3" e clique em "ATIVAR"
5. Vá em "Credenciais" > "Criar Credenciais" > "Chave de API"
6. Copie a chave gerada

### 1.2 Editar application.properties

Abra o arquivo: `src/main/resources/application.properties`

Altere a linha:
```properties
youtube.api.key=SUA_YOUTUBE_API_KEY_AQUI
```

---

## 🌐 PASSO 2: Configurar Ngrok

### 2.1 Instalar Ngrok

**Windows (PowerShell como Admin):**
```powershell
# Via Chocolatey
choco install ngrok

# Ou baixe direto de: https://ngrok.com/download
```

**Linux:**
```bash
# Download
wget https://bin.equinox.io/c/bNyj1mQVY4c/ngrok-v3-stable-linux-amd64.tgz

# Extrair
sudo tar xvzf ngrok-v3-stable-linux-amd64.tgz -C /usr/local/bin

# Verificar instalação
ngrok version
```

**Mac:**
```bash
# Via Homebrew
brew install ngrok/ngrok/ngrok

# Verificar instalação
ngrok version
```

### 2.2 Autenticar Ngrok

1. Crie conta gratuita em: https://ngrok.com/
2. Obtenha seu authtoken em: https://dashboard.ngrok.com/get-started/your-authtoken
3. Configure:

```bash
ngrok config add-authtoken SEU_AUTH_TOKEN_AQUI
```

---

## ▶️ PASSO 3: Executar a Aplicação

### 3.1 Compilar o Projeto

```bash
# Navegue até a pasta do projeto
cd youtube-live-monitor

# Compile (primeira vez pode demorar - baixa dependências)
mvn clean install
```

### 3.2 Iniciar a Aplicação

**Terminal 1 - Spring Boot:**
```bash
mvn spring-boot:run
```

Aguarde até ver:
```
Started YouTubeLiveMonitorApplication in X.XXX seconds
```

A aplicação estará em: `http://localhost:8080`

### 3.3 Iniciar o Ngrok

**Terminal 2 - Ngrok:**
```bash
ngrok http 8080
```

Você verá algo como:

```
ngrok                                                                                           

Session Status                online
Account                       seu@email.com (Plan: Free)
Version                       3.x.x
Region                        United States (us)
Latency                       -
Web Interface                 http://127.0.0.1:4040
Forwarding                    https://abc123def456.ngrok-free.app -> http://localhost:8080

Connections                   ttl     opn     rt1     rt5     p50     p90
                              0       0       0.00    0.00    0.00    0.00
```

**⚠️ IMPORTANTE:** Copie a URL `https://abc123def456.ngrok-free.app`

### 3.4 Atualizar Callback URL

1. Edite novamente `application.properties`
2. Altere a linha:

```properties
app.callback.url=https://abc123def456.ngrok-free.app/api/v1/notifications
```

3. **Reinicie a aplicação** (Ctrl+C no Terminal 1 e execute `mvn spring-boot:run` novamente)

---

## 🧪 PASSO 4: Testar a API

### Opção 1: Usar o Script de Testes

```bash
# Dar permissão de execução (se ainda não fez)
chmod +x test-api.sh

# Executar
./test-api.sh
```

### Opção 2: Comandos curl Manualmente

#### 4.1 Buscar Canais por Nome

```bash
curl -X GET "http://localhost:8080/api/v1/channels/search?query=gaules" | jq '.'
```

**Resposta Esperada:**
```json
{
  "success": true,
  "message": "Canais encontrados",
  "data": {
    "channels": [
      {
        "channelId": "UCd1y-FSzUvDxx7UP_j72mXQ",
        "displayName": "Gaules",
        "description": "Canal oficial...",
        "thumbnailUrl": "https://...",
        "url": "https://youtube.com/channel/UCd1y-FSzUvDxx7UP_j72mXQ",
        "subscriberCount": 3500000
      }
    ],
    "query": "gaules",
    "totalResults": 1
  }
}
```

#### 4.2 Buscar Canal por Link

```bash
curl -X GET "http://localhost:8080/api/v1/channels/search?query=youtube.com/@casimito" | jq '.'
```

#### 4.3 Confirmar e Adicionar Canal

**⚠️ Use o `channelId` obtido na busca anterior**

```bash
curl -X POST "http://localhost:8080/api/v1/channels/confirm/UCd1y-FSzUvDxx7UP_j72mXQ" | jq '.'
```

**Resposta Esperada:**
```json
{
  "success": true,
  "message": "Canal adicionado com sucesso",
  "data": {
    "id": "UCd1y-FSzUvDxx7UP_j72mXQ",
    "displayName": "Gaules",
    "url": "https://youtube.com/channel/UCd1y-FSzUvDxx7UP_j72mXQ",
    "lastSubscriptionDate": "2025-02-05T14:30:00",
    "subscriptionActive": true,
    "createdAt": "2025-02-05T14:30:00",
    "updatedAt": "2025-02-05T14:30:00"
  }
}
```

**Verifique os logs** - você deve ver:
```
INFO: Enviando subscribe request para canal UCd1y-FSzUvDxx7UP_j72mXQ no hub https://pubsubhubbub.appspot.com/subscribe
INFO: subscribe bem-sucedido para canal: UCd1y-FSzUvDxx7UP_j72mXQ
INFO: Inscrição ativa no hub para canal: Gaules
```

#### 4.4 Listar Todos os Canais

```bash
curl -X GET "http://localhost:8080/api/v1/channels" | jq '.'
```

#### 4.5 Listar Apenas Canais Ativos

```bash
curl -X GET "http://localhost:8080/api/v1/channels/active" | jq '.'
```

#### 4.6 Buscar Detalhes de um Canal

```bash
curl -X GET "http://localhost:8080/api/v1/channels/UCd1y-FSzUvDxx7UP_j72mXQ" | jq '.'
```

#### 4.7 Remover Canal

```bash
curl -X DELETE "http://localhost:8080/api/v1/channels/UCd1y-FSzUvDxx7UP_j72mXQ" | jq '.'
```

#### 4.8 Health Check do Webhook

```bash
curl -X GET "http://localhost:8080/api/v1/notifications/health"
```

---

## 🔔 PASSO 5: Aguardar Notificações de Live

Quando o canal monitorado iniciar uma live, você verá no console:

```
╔════════════════════════════════════════════════════════════════╗
║                    🔴 ALERTA DE LIVE! 🔴                      ║
╠════════════════════════════════════════════════════════════════╣
║  Canal: Gaules                                                ║
║  Título: CS2 - CLASSIFICATÓRIAS AO VIVO                       ║
║  Link: https://youtube.com/watch?v=dQw4w9WgXcQ                ║
╚════════════════════════════════════════════════════════════════╝

[ALERTA DE LIVE] O canal Gaules está ao vivo agora! Link: https://youtube.com/watch?v=dQw4w9WgXcQ
```

---

## 📊 Verificar Banco de Dados H2

Acesse: http://localhost:8080/h2-console

**Configurações:**
- JDBC URL: `jdbc:h2:file:./data/youtube-monitor`
- User Name: `sa`
- Password: (deixe em branco)

**Tabela Principal:**
```sql
SELECT * FROM channels;
```

---

## 🔍 Monitorar Logs em Tempo Real

```bash
# Se quiser ver os logs de forma contínua
tail -f nohup.out
```

---

## ⚙️ Renovação Automática de Assinaturas

A aplicação renova automaticamente as assinaturas a cada **48 horas**.

Você verá nos logs:
```
INFO: === Iniciando renovação automática de assinaturas ===
INFO: Renovando 3 assinaturas
INFO: Assinatura renovada: Gaules - UCd1y-FSzUvDxx7UP_j72mXQ
INFO: === Renovação de assinaturas concluída ===
```

---

## 🐛 Resolução de Problemas

### Problema: "Webhook não recebe notificações"

**Soluções:**

1. ✅ Verificar se o ngrok está rodando
```bash
# Deve retornar 200 OK
curl https://SUA_URL_NGROK.ngrok-free.app/api/v1/notifications/health
```

2. ✅ Confirmar que a URL está correta no `application.properties`

3. ✅ Reiniciar a aplicação após alterar a URL

4. ✅ Verificar logs do Google no terminal do ngrok (Terminal 2)
   - Acesse: http://localhost:4040 (Web Interface do Ngrok)

### Problema: "Erro ao buscar canais"

**Soluções:**

1. ✅ Verificar se a API Key é válida
2. ✅ Verificar se a YouTube Data API v3 está ativada
3. ✅ Verificar quota da API em: https://console.cloud.google.com/apis/api/youtube.googleapis.com/quotas

### Problema: "Canal não encontrado"

**Soluções:**

1. ✅ Tente buscar pelo nome exato
2. ✅ Use o link completo: `youtube.com/@nomecanal`
3. ✅ Verifique se o canal realmente existe

---

## 📝 Notas Importantes

1. **Ngrok Free:** A URL muda toda vez que você reinicia o ngrok
   - Você precisa atualizar `app.callback.url` e reiniciar a aplicação

2. **YouTube API Quota:** A conta gratuita tem limite de 10.000 unidades/dia
   - Cada busca = ~100 unidades
   - Cada verificação de vídeo = ~3 unidades

3. **Delay nas Notificações:** O Google pode levar alguns minutos para:
   - Validar a inscrição inicial
   - Enviar notificações de novas lives

4. **Persistência:** O banco H2 está configurado para salvar em arquivo
   - Seus canais ficam salvos mesmo após reiniciar

---

## 🎯 Canais Populares para Testar

```bash
# Gaules (CS, Valorant)
curl -X GET "http://localhost:8080/api/v1/channels/search?query=gaules"

# Casimito
curl -X GET "http://localhost:8080/api/v1/channels/search?query=casimito"

# Alanzoka
curl -X GET "http://localhost:8080/api/v1/channels/search?query=alanzoka"

# Nobru
curl -X GET "http://localhost:8080/api/v1/channels/search?query=nobru"
```

---

## 📚 Recursos Adicionais

- **README.md:** Documentação completa do projeto
- **QUICKSTART.md:** Guia rápido de configuração
- **YouTube-Live-Monitor.postman_collection.json:** Collection do Postman
- **test-api.sh:** Script automatizado de testes

---

## 🎉 Pronto!

Seu microserviço de monitoramento está funcionando! 🚀

Qualquer dúvida, verifique os logs ou consulte a documentação completa.
