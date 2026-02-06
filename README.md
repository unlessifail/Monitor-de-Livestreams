<img width="1427" height="119" alt="banner_img" src="https://github.com/user-attachments/assets/00fdccfb-062e-492a-aefa-67e7bf73bb91" />

# Monitor de Livestreams

O **Monitor de Livestreams** é um ecossistema de monitoramento em tempo real que utiliza o protocolo **PubSubHubbub (WebSub)** para capturar eventos de transmissões ao vivo do YouTube. A arquitetura foi projetada com foco em **baixa latência**, eliminando a necessidade de requisições intermitentes (*polling*) e garantindo a integridade do recebimento de dados por meio de Webhooks.

---

## Arquitetura de Sistema

A solução segue o paradigma de **Event-Driven Architecture (EDA)** e é estruturada nas seguintes camadas:

### Camada de Persistência

- Implementada com **Spring Data JPA** e **H2 Database**.
- Responsável pelo gerenciamento de metadados como:
  - IDs de canais
  - Nomes de exibição
  - URLs associadas
  - Estado de monitoramento

### Camada de Serviço (Backend)

- Desenvolvida em **Java 17** com **Spring Boot 3**.
- Responsável por:
  - Handshake de verificação do WebSub
  - Gerenciamento de subscrições no Hub do Google
  - Processamento assíncrono das notificações *push* enviadas pelo YouTube

### Camada de Interface (Frontend)

- Construída com **Vanilla JavaScript** e **Tailwind CSS**.
- Interface baseada em **Glassmorphism**, oferecendo:
  - Baixa complexidade no cliente
  - Feedback visual imediato
  - Experiência fluida para operações de monitoramento

### Integração de Terceiros

- **YouTube Data API v3** para descoberta e validação de canais.
- **Google Hub (PubSubHubbub)** para subscrição e entrega de eventos de transmissão.

---

## Sinergia Tecnológica

A stack adotada permite que o projeto seja facilmente adaptado para diferentes contextos de mercado e escalas operacionais:

### Spring Boot & Java 17

- Robustez para lidar com fluxos críticos de dados.
- Segurança de tipos e maturidade do ecossistema.
- Adequado para sistemas que exigem alta disponibilidade e previsibilidade.

### WebSub Protocol

- Redução significativa de overhead de rede e CPU.
- O servidor só é acionado quando um evento real ocorre.
- Modelo ideal para sistemas reativos e orientados a eventos.

### Tailwind CSS

- Interface moderna e responsiva.
- Baixo custo de renderização no lado do cliente.
- Excelente adequação para dashboards operacionais e painéis de observabilidade.

Essa combinação tecnológica é aplicável não apenas a soluções de mídia, mas também a cenários como:

- Sistemas de IoT
- Rastreamento logístico
- Notificações financeiras em tempo real

---

## Requisitos de Execução

- **JDK 17+**
- **Maven 3.x**
- **Ngrok** para tunelamento do endpoint de callback
- **Chave de API válida da YouTube Data API v3**

Para instruções detalhadas de instalação, configuração e execução do ambiente local, consulte os seguintes documentos:

- `QUICKSTART.md`
- `NGROK_GUIDE.md`
- `TESTING.md`

---

## Desenvolvimento e Arquitetura

**Autor**: [Kalil M. Santos](https://www.linkedin.com/in/kalil-santos/)
**Cargo**: Project Architect  
**Organização**: Hexenkult – Coletivo de Desenvolvimento e Soluções Tecnológicas

<img width="1424" height="764" alt="panel_img" src="https://github.com/user-attachments/assets/738320c3-bc19-4561-9374-19611ae7f4e6" />
<img width="1414" height="329" alt="radar_img" src="https://github.com/user-attachments/assets/0666d583-c1ca-4e7a-abc9-87f244934359" />
<img width="896" height="181" alt="credits_img" src="https://github.com/user-attachments/assets/314760b4-3e83-4412-a2e7-ecfd2b5e92a5" />
