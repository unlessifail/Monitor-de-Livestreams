# 🌐 Guia Completo de Configuração do Ngrok

## O que é o Ngrok?

Ngrok é uma ferramenta que cria um túnel seguro entre sua máquina local e a internet, permitindo que serviços externos (como o Google PubSubHubbub) acessem sua aplicação rodando em localhost.

## 📥 Instalação do Ngrok

### Windows

1. Baixe em: https://ngrok.com/download
2. Extraia o arquivo `ngrok.exe`
3. Adicione ao PATH ou execute direto da pasta

### macOS

```bash
brew install ngrok/ngrok/ngrok
```

### Linux

```bash
curl -s https://ngrok-agent.s3.amazonaws.com/ngrok.asc | sudo tee /etc/apt/trusted.gpg.d/ngrok.asc >/dev/null
echo "deb https://ngrok-agent.s3.amazonaws.com buster main" | sudo tee /etc/apt/sources.list.d/ngrok.list
sudo apt update
sudo apt install ngrok
```

## 🔑 Autenticação (Opcional mas Recomendado)

1. Crie uma conta gratuita em https://ngrok.com/
2. Copie seu authtoken do dashboard
3. Execute:

```bash
ngrok config add-authtoken YOUR_AUTH_TOKEN
```

**Benefícios da conta gratuita:**
- Sessões mais longas
- Múltiplos túneis
- Domínio customizado

## 🚀 Como Usar

### Passo 1: Inicie sua aplicação Spring Boot

```bash
cd youtube-live-monitor
mvn spring-boot:run
```

Aguarde até ver:
```
Started YouTubeLiveMonitorApplication in X seconds
```

### Passo 2: Inicie o Ngrok

Em um **novo terminal**, execute:

```bash
ngrok http 8080
```

### Passo 3: Copie a URL

Você verá algo assim:

```
ngrok

Session Status                online
Account                       seu-email@example.com (Plan: Free)
Version                       3.x.x
Region                        United States (us)
Latency                       -
Web Interface                 http://127.0.0.1:4040
Forwarding                    https://abc123def456.ngrok.io -> http://localhost:8080

Connections                   ttl     opn     rt1     rt5     p50     p90
                              0       0       0.00    0.00    0.00    0.00
```

**Copie a URL HTTPS** (ex: `https://abc123def456.ngrok.io`)

### Passo 4: Atualize o application.properties

Edite `src/main/resources/application.properties`:

```properties
app.callback.url=https://abc123def456.ngrok.io/api/v1/notifications
```

### Passo 5: Reinicie a Aplicação

Pare a aplicação (Ctrl+C) e inicie novamente:

```bash
mvn spring-boot:run
```

## 🔍 Monitorando Requisições

O Ngrok oferece um dashboard web em: http://127.0.0.1:4040

Aqui você pode:
- Ver todas as requisições HTTP
- Inspecionar headers e body
- Replay requisições
- Ver status codes

## ⚠️ Observações Importantes

### URL Muda a Cada Reinício

Na versão gratuita, cada vez que você reinicia o ngrok, uma nova URL é gerada. Para URL fixa, considere:

- **Plano Pago do Ngrok**: Permite domínios customizados
- **Alternativas Gratuitas**: localhost.run, serveo.net

### Sessão Expira

Sessões gratuitas duram:
- **Sem conta**: 2 horas
- **Com conta**: 8 horas

### Configuração para Produção

Para ambiente de produção, NÃO use ngrok. Considere:

- Servidor VPS (DigitalOcean, AWS, etc.)
- Domínio próprio
- HTTPS com Let's Encrypt

## 🎯 Testando a Configuração

### 1. Teste se o ngrok está acessível

```bash
curl https://SEU-NGROK-URL.ngrok.io/api/v1/channels
```

Deve retornar a lista de canais (vazia inicialmente).

### 2. Teste o endpoint de notificações

```bash
curl "https://SEU-NGROK-URL.ngrok.io/api/v1/notifications?hub.mode=subscribe&hub.topic=test&hub.challenge=abc123"
```

Deve retornar: `abc123`

## 🛠️ Troubleshooting

### Erro: "Failed to listen on port 4040"

Outra instância do ngrok está rodando. Feche-a ou use outra porta:

```bash
ngrok http 8080 --web-interface=127.0.0.1:4041
```

### Erro: "Tunnel not found"

Pode ser que o túnel tenha expirado. Reinicie o ngrok.

### Erro: "ERR_NGROK_3200"

Limite de conexões atingido (plano gratuito). Aguarde ou faça upgrade.

### URL ngrok não responde

1. Verifique se a aplicação Spring está rodando na porta 8080
2. Teste o localhost primeiro: `curl http://localhost:8080/api/v1/channels`
3. Verifique firewall/antivírus

## 📱 Alternativas ao Ngrok

Se o ngrok não funcionar, considere:

### 1. localhost.run

```bash
ssh -R 80:localhost:8080 nokey@localhost.run
```

### 2. serveo.net

```bash
ssh -R 80:localhost:8080 serveo.net
```

### 3. Cloudflare Tunnel

```bash
cloudflared tunnel --url http://localhost:8080
```

## 💡 Dicas Profissionais

1. **Salve sua URL**: Anote a URL do ngrok em algum lugar para não perder
2. **Use o dashboard**: Monitore requisições em tempo real
3. **Logs são seus amigos**: Sempre confira os logs da aplicação
4. **Teste antes de adicionar canais**: Garanta que tudo está funcionando

## 📞 Suporte

Se tiver problemas:
1. Verifique os logs da aplicação
2. Verifique o dashboard do ngrok (http://127.0.0.1:4040)
3. Teste com curl antes de usar com o YouTube
