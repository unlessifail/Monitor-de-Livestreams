# Guia de Configuração Ngrok – Monitor de Livestreams

## Fundamentação Técnica

O monitoramento de eventos do YouTube via PubSubHubbub opera através de um mecanismo de notificação *push*. Quando ocorre um evento de transmissão, o Hub do Google atua como cliente HTTP e o seu servidor local assume o papel de receptor.

Em ambiente de desenvolvimento, o `localhost` encontra-se protegido por NAT e firewalls, tornando-o inacessível à internet pública. O Ngrok resolve essa limitação ao criar um **Secure Tunnel**, mapeando um endpoint público temporário diretamente para o serviço em execução localmente na porta `8080`.

---

## Procedimento de Instalação e Autenticação

### Download e Binário

- Realize o download do binário oficial do Ngrok de acordo com o seu sistema operacional.
- Adicione o executável ao seu `PATH` global para facilitar a execução via terminal.

### Autenticação (Recomendado)

Para evitar interrupções de sessão e liberar recursos adicionais (como maior estabilidade de túnel), registre-se no dashboard do Ngrok e configure o *authtoken*:

```bash
ngrok config add-authtoken <seu_token_aqui>
```

---

## Execução do Túnel

Para o ecossistema do **Monitor de Livestreams**, execute o comando abaixo:

```bash
ngrok http 8080
```

Após a inicialização, o Ngrok exibirá uma URL pública no formato:

```
https://xyz.ngrok-free.app
```

Essa URL representa o endpoint externo que encaminha requisições para o backend local.

---

## Integração com o Dashboard

A URL gerada pelo Ngrok deve ser utilizada como **Endpoint de Callback** no sistema de monitoramento.

### Fluxo de Handshake no Sistema

1. **Entrada**  
   O usuário insere a URL do Ngrok no painel de configuração do frontend.

2. **Persistência**  
   O sistema armazena a URL no `LocalStorage`, preservando o estado entre sessões do navegador.

3. **Transmissão**  
   Ao iniciar o monitoramento de um canal, o frontend envia essa URL para o backend Spring Boot.

4. **Inscrição**  
   O backend anexa o caminho `/api/v1/notifications` à URL base e a registra junto ao Hub do Google como o endpoint oficial para recebimento de payloads XML/Atom.

---

## Observações de Manutenção

### Sessões Temporárias

- Caso não seja utilizado um domínio fixo (*Static Domain*), o Ngrok gera uma nova URL a cada reinicialização do túnel.
- Sempre atualize o campo **Endpoint de Callback** no frontend após reiniciar o Ngrok.

### Inspeção de Tráfego

- O Ngrok disponibiliza uma interface local para inspeção de requisições.
- Acesse no navegador:

```
http://127.0.0.1:4040
```

Essa interface é essencial para depuração dos payloads XML enviados pelo YouTube durante eventos de live.

---

**Autor**: Kalil M. Santos  
**Cargo**: Project Architect  
**Organização**: Hexenkult – Coletivo de Desenvolvimento e Soluções Tecnológicas

