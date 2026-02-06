# Quickstart Guide – Monitor de Livestreams

Este guia fornece os passos mínimos necessários para colocar o sistema de monitoramento de lives do YouTube em operação, desde a exposição do endpoint até o início do rastreamento de canais.

---

## 1. Preparação de Infraestrutura

Antes de iniciar a aplicação, é obrigatório estabelecer um túnel de comunicação externa para recebimento dos Webhooks do YouTube.

Em um terminal, execute:

```bash
ngrok http 8080
```

- Mantenha o terminal do Ngrok aberto durante toda a execução.
- Copie a URL HTTPS de encaminhamento gerada (exemplo: `https://8123-xyz.ngrok-free.app`).

Essa URL será utilizada como endpoint público de callback.

---

## 2. Inicialização do Ecossistema

O sistema depende da execução simultânea do backend e da interface de gerenciamento.

### Backend

Execute a aplicação Spring Boot via IDE ou terminal:

```bash
mvn spring-boot:run
```

Validações esperadas:
- Logs indicando inicialização bem-sucedida.
- Aplicação escutando na porta `8080`.

### Frontend

- Abra o arquivo `index.html` diretamente no navegador.
- Certifique-se de que a interface carregou corretamente e consegue se comunicar com o backend local.

---

## 3. Configuração de Handshake

Com a interface aberta, realize a configuração inicial do endpoint de callback:

1. No painel **Endpoint de Callback**, cole a URL HTTPS fornecida pelo Ngrok (obtida no Passo 1).
2. Clique em **Set Endpoint**.
3. O status do sistema deve mudar para **Online**.

A partir deste ponto, o sistema está apto a registrar Webhooks válidos junto ao Hub do Google (PubSubHubbub).

---

## 4. Ciclo de Monitoramento

Para iniciar o rastreamento de um canal:

### Busca

- Utilize o campo de pesquisa para localizar o canal desejado por **nome**, **handle** ou **ID**.

### Seleção

- Analise os resultados retornados.
- Clique em **Confirm Handshake** no canal correto.

### Verificação

- O canal confirmado aparecerá no painel **Active Radar**.
- Neste estágio, o backend já executou a inscrição (`subscribe`) no PubSubHubbub do YouTube.

---

## 5. Recebimento de Notificações

Com o canal ativo no radar:

- Sempre que o canal iniciar uma live, o YouTube enviará um payload XML para o endpoint público do Ngrok.
- O backend processará o XML recebido e executará a lógica de notificação configurada.

Para auditoria e inspeção das requisições Webhook:

- Acesse no navegador: `http://localhost:4040`
- Este painel exibe todo o tráfego interceptado pelo Ngrok.

---

## 6. Resumo de Comandos Rápidos

| Ação   | Comando / Local        |
|-------|------------------------|
| Túnel | `ngrok http 8080`      |
| Build | `mvn clean install`    |
| Run   | `mvn spring-boot:run`  |
| UI    | `index.html`           |

---

**Autor**: Kalil M. Santos  
**Cargo**: Project Architect  
**Organização**: Hexenkult – Coletivo de Desenvolvimento e Soluções Tecnológicas

