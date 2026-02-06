# 🧪 Guia de Testes Práticos

## 📋 Pré-requisitos para Testes

- ✅ Aplicação rodando em `http://localhost:8080`
- ✅ Ngrok rodando e URL configurada
- ✅ YouTube API Key configurada

## 🎯 Cenários de Teste

### Teste 1: Buscar Canal por Nome

```bash
curl -X POST "http://localhost:8080/api/v1/channels/search?query=Alanzoka"
```

**Resultado Esperado:**
- Status: 200 OK
- JSON com lista de canais encontrados
- Cada canal tem: channelId, displayName, url, subscriberCount

### Teste 2: Buscar Canal por URL com @handle

```bash
curl -X POST "http://localhost:8080/api/v1/channels/search?query=https://youtube.com/@alanzoka"
```

**Resultado Esperado:**
- Status: 200 OK
- JSON com 1 canal específico (mais preciso que busca por nome)

### Teste 3: Buscar Canal por URL com Channel ID

```bash
curl -X POST "http://localhost:8080/api/v1/channels/search?query=https://youtube.com/channel/UCu4bD0bssKf8FspfOs15EDA"
```

**Resultado Esperado:**
- Status: 200 OK
- JSON com o canal exato

### Teste 4: Adicionar Canal ao Monitoramento

Primeiro, pegue o channelId de um dos testes anteriores, depois:

```bash
curl -X POST "http://localhost:8080/api/v1/channels/confirm/UCu4bD0bssKf8FspfOs15EDA"
```

**Resultado Esperado:**
- Status: 200 OK
- Canal salvo no banco
- Subscrição ativa = true
- Logs mostram: "Subscrevendo canal no Hub"

### Teste 5: Listar Todos os Canais

```bash
curl "http://localhost:8080/api/v1/channels"
```

**Resultado Esperado:**
- Status: 200 OK
- Array com todos os canais salvos

### Teste 6: Listar Apenas Canais Ativos

```bash
curl "http://localhost:8080/api/v1/channels/active"
```

**Resultado Esperado:**
- Status: 200 OK
- Array com canais onde subscribed = true

### Teste 7: Ver Detalhes de um Canal

```bash
curl "http://localhost:8080/api/v1/channels/UCu4bD0bssKf8FspfOs15EDA"
```

**Resultado Esperado:**
- Status: 200 OK
- Objeto com todos os dados do canal

### Teste 8: Remover Canal

```bash
curl -X DELETE "http://localhost:8080/api/v1/channels/UCu4bD0bssKf8FspfOs15EDA"
```

**Resultado Esperado:**
- Status: 200 OK
- Canal removido do banco
- Logs mostram: "Desinscrevendo canal do Hub"

## 🔔 Testando Notificações

### Teste Manual do Endpoint de Validação

Simula a validação que o Google faz:

```bash
curl "http://localhost:8080/api/v1/notifications?hub.mode=subscribe&hub.topic=https://www.youtube.com/xml/feeds/videos.xml?channel_id=TEST&hub.challenge=abc123&hub.lease_seconds=432000"
```

**Resultado Esperado:**
- Status: 200 OK
- Response body: `abc123`

### Teste Manual de Notificação

Simula uma notificação do YouTube:

```bash
curl -X POST "http://localhost:8080/api/v1/notifications" \
  -H "Content-Type: application/atom+xml" \
  -d '<?xml version="1.0" encoding="UTF-8"?>
<feed xmlns:yt="http://www.youtube.com/xml/schemas/2015" xmlns="http://www.w3.org/2005/Atom">
  <entry>
    <yt:videoId>dQw4w9WgXcQ</yt:videoId>
    <yt:channelId>UCu4bD0bssKf8FspfOs15EDA</yt:channelId>
    <title>Test Video</title>
    <link rel="alternate" href="https://www.youtube.com/watch?v=dQw4w9WgXcQ"/>
  </entry>
</feed>'
```

**Resultado Esperado:**
- Status: 200 OK
- Logs mostram processamento da notificação
- Se o vídeo estiver ao vivo, aparece: `[ALERTA DE LIVE]`

## 🎪 Cenário Completo de Teste

Execute este fluxo completo para validar todo o sistema:

```bash
# 1. Buscar um canal brasileiro popular
curl -X POST "http://localhost:8080/api/v1/channels/search?query=Casimiro"

# 2. Copie o channelId da resposta e adicione ao monitoramento
# Exemplo: UCiP8-vGuXIHz1REqultEqcA
curl -X POST "http://localhost:8080/api/v1/channels/confirm/UCiP8-vGuXIHz1REqultEqcA"

# 3. Verifique se foi adicionado
curl "http://localhost:8080/api/v1/channels"

# 4. Verifique canais ativos
curl "http://localhost:8080/api/v1/channels/active"

# 5. Aguarde uma live começar (ou use o teste manual)

# 6. Remova o canal quando terminar os testes
curl -X DELETE "http://localhost:8080/api/v1/channels/UCiP8-vGuXIHz1REqultEqcA"
```

## 📊 Verificando no Console H2

1. Acesse: `http://localhost:8080/h2-console`
2. Configure:
   - JDBC URL: `jdbc:h2:mem:youtubedb`
   - Username: `sa`
   - Password: (vazio)
3. Execute queries:

```sql
-- Ver todos os canais
SELECT * FROM channels;

-- Ver apenas canais subscritos
SELECT * FROM channels WHERE subscribed = true;

-- Contar canais
SELECT COUNT(*) FROM channels;

-- Ver última data de subscrição
SELECT id, display_name, last_subscription_date 
FROM channels 
ORDER BY last_subscription_date DESC;
```

## 🐛 Troubleshooting de Testes

### Erro 400 Bad Request

**Possível causa:** Query inválida ou vazia

**Solução:** Verifique se está passando o parâmetro `query`

### Erro 500 Internal Server Error

**Possível causa:** YouTube API key inválida ou limite excedido

**Solução:** 
1. Verifique a API key em `application.properties`
2. Verifique quota no Google Cloud Console

### Canal não recebe notificações

**Possíveis causas:**
1. Callback URL incorreta
2. Ngrok não está rodando
3. Canal não está fazendo lives

**Solução:**
1. Teste o endpoint de validação manualmente
2. Verifique logs para ver se a subscrição foi aceita
3. Use o dashboard do ngrok para ver se requisições estão chegando

### Subscrição não fica ativa

**Possível causa:** Falha na comunicação com o Hub do Google

**Solução:**
1. Verifique conexão com internet
2. Verifique se ngrok URL está acessível externamente
3. Tente resubscrever manualmente

## 📝 Checklist de Validação

Use este checklist para garantir que tudo está funcionando:

- [ ] Aplicação inicia sem erros
- [ ] Busca por nome retorna resultados
- [ ] Busca por URL funciona
- [ ] Canal é adicionado com sucesso
- [ ] Subscrição fica ativa (subscribed = true)
- [ ] Canal aparece na lista
- [ ] Endpoint de validação responde corretamente
- [ ] Logs mostram "Subscrevendo canal no Hub"
- [ ] Remoção de canal funciona
- [ ] Console H2 está acessível

## 🎓 Canais Brasileiros Recomendados para Teste

Estes canais fazem lives regularmente:

- **Casimiro**: UCiP8-vGuXIHz1REqultEqcA
- **Alanzoka**: UCu4bD0bssKf8FspfOs15EDA  
- **Gaules**: UCDhyi0skDY8LnxJJ1GiX_Lg
- **Loud Coringa**: UCtbmjdN0MZu5mwuG-GqPKkA

**Dica:** Escolha streamers que fazem live diariamente para testar mais rápido!

## 💡 Dicas de Teste

1. **Use Postman ou Insomnia**: Mais fácil que curl para testes manuais
2. **Monitore os logs**: Sempre acompanhe o terminal da aplicação
3. **Dashboard do Ngrok**: Veja as requisições em tempo real
4. **Teste aos poucos**: Valide cada passo antes de prosseguir
5. **Documente erros**: Anote qualquer comportamento estranho

## 🚀 Teste de Carga (Opcional)

Para testar com múltiplos canais:

```bash
# Adicione vários canais de uma vez
for channel in UCiP8-vGuXIHz1REqultEqcA UCu4bD0bssKf8FspfOs15EDA UCDhyi0skDY8LnxJJ1GiX_Lg; do
  curl -X POST "http://localhost:8080/api/v1/channels/confirm/$channel"
  sleep 2
done

# Verifique todos
curl "http://localhost:8080/api/v1/channels"
```
