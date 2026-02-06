# 🚀 Guia de Início Rápido

## Configuração em 5 Passos

### 1️⃣ Obter YouTube API Key

```bash
1. Acesse: https://console.cloud.google.com/
2. Crie um projeto
3. Ative "YouTube Data API v3"
4. Crie uma API Key em "Credenciais"
5. Copie a chave gerada
```

### 2️⃣ Configurar a Aplicação

Edite `src/main/resources/application.properties`:

```properties
youtube.api.key=SUA_CHAVE_AQUI
```

### 3️⃣ Instalar Ngrok

**Windows:**
```bash
choco install ngrok
```

**Linux/Mac:**
```bash
# Linux
wget https://bin.equinox.io/c/bNyj1mQVY4c/ngrok-v3-stable-linux-amd64.tgz
sudo tar xvzf ngrok-v3-stable-linux-amd64.tgz -C /usr/local/bin

# Mac
brew install ngrok/ngrok/ngrok
```

**Configurar:**
```bash
# Cadastre-se em ngrok.com e obtenha seu authtoken
ngrok config add-authtoken SEU_AUTH_TOKEN
```

### 4️⃣ Executar a Aplicação

**Terminal 1 - Aplicação:**
```bash
mvn spring-boot:run
```

**Terminal 2 - Ngrok:**
```bash
ngrok http 8080
```

Copie a URL do ngrok (ex: `https://abc123.ngrok-free.app`)

### 5️⃣ Atualizar Callback URL

Edite `application.properties` novamente:

```properties
app.callback.url=https://abc123.ngrok-free.app/api/v1/notifications
```

**Reinicie a aplicação!**

---

## 📝 Exemplos de Uso

### Buscar Canal
```bash
curl "http://localhost:8080/api/v1/channels/search?query=gaules"
```

### Adicionar Canal
```bash
# Pegue o channelId da resposta anterior
curl -X POST "http://localhost:8080/api/v1/channels/confirm/CHANNEL_ID_AQUI"
```

### Listar Canais
```bash
curl "http://localhost:8080/api/v1/channels"
```

---

## 🎯 Testar Tudo de Uma Vez

```bash
chmod +x test-api.sh
./test-api.sh
```

---

## ✅ Checklist

- [ ] YouTube API Key configurada
- [ ] Ngrok instalado e configurado
- [ ] Aplicação rodando (localhost:8080)
- [ ] Ngrok rodando e URL copiada
- [ ] Callback URL atualizada no application.properties
- [ ] Aplicação reiniciada após atualizar URL
- [ ] Canal adicionado via API
- [ ] Verificando logs para confirmação de assinatura

---

## 🔔 Como Saber se Está Funcionando?

1. Após adicionar um canal, verifique os logs:
   ```
   INFO: subscribe bem-sucedido para canal: CHANNEL_ID
   INFO: Inscrição ativa no hub para canal: Nome do Canal
   ```

2. Quando uma live iniciar, você verá:
   ```
   ╔════════════════════════════════════╗
   ║      🔴 ALERTA DE LIVE! 🔴        ║
   ╠════════════════════════════════════╣
   ║  Canal: Nome do Canal             ║
   ║  Link: https://youtube.com/...    ║
   ╚════════════════════════════════════╝
   ```

---

## ❓ Problemas Comuns

### "Webhook não recebe notificações"
✅ Certifique-se que reiniciou a aplicação após configurar o ngrok URL

### "Erro ao buscar canais"
✅ Verifique se a API Key está correta e a API está ativada

### "Canal não encontrado"
✅ Tente buscar pelo link completo: `youtube.com/@nomecanal`

---

## 📚 Próximos Passos

- Consulte o [README.md](README.md) completo
- Execute `./test-api.sh` para testar todos os endpoints
- Monitore os logs em tempo real: `tail -f logs/application.log`
