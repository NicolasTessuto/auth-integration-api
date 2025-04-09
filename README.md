# Authorization and Integration API Platform

### API de Autorização e Integração com Plataformas de gerenciamento de Leads

Esta API foi desenvolvida com foco na integração e autorização com plataformas de gerenciamento de leads. Ela foi projetada para ser facilmente extensível, permitindo a adição de novos _authorization providers_ ou _targets_ de contato com o mínimo esforço, tornando-a adequada para ambientes de produção.

## Sumário

- [Funcionalidades](#funcionalidades)
- [Tecnologias Utilizadas](#tecnologias-utilizadas)
- [Pré-requisitos](#pré-requisitos)
- [Configuração do Ambiente](#configuração-do-ambiente)
- [Instalação e Execução](#instalação-e-execução)
- [Documentação da API](#documentação-da-api)
- [Arquitetura e Decisões Técnicas](#arquitetura-e-decisões-técnicas)
- [Ferramentas de Desenvolvimento](#ferramentas-de-desenvolvimento)
- [Melhorias Futuras](#melhorias-futuras)

## Funcionalidades

- Sistema de autenticação com múltiplos provedores
- Arquitetura de integração extensível para diversas plataformas de gestão de leads
- Processamento assíncrono de mensagens com mecanismos automáticos de retry
- Proteção contra limitação de taxa (rate-limiting) com sistema inteligente de filas
- Autenticação baseada em tokens
- Sincronização de dados de leads em tempo real
- Webhook para Notificação de Novo Contato



---

## Tecnologias Utilizadas

- **Linguagem:** Java 21
- **Build & Dependency Management:** Gradle
- **Framework:** Spring Boot com Spring Webflux
- **Persistência:** JPA e banco H2 (podendo ser substituído por um banco de produção, como MongoDB ou DynamoDB)
- **Mensageria:** RabbitMQ (para fallback em caso de retorno 429 pela API do Hubspot)
- **Integração de Testes de Webhook:** Ngrok, para estabelecer um canal HTTPS temporário entre a web e a API
- **Utilitários:** Lombok


## Pré-requisitos

- Docker e Docker Compose
- Java 21 JDK (para desenvolvimento local)
- Gradle 8.x
- Git

## Configuração do Ambiente

1. Clone o repositório
2. Atualize o arquivo `.env` na raiz do projeto com as seguintes variáveis:

```env
# HubSpot Integration Credentials
#HUBSPOT_CLIENT_ID= xxxxx
#HUBSPOT_CLIENT_SECRET= xxxxx
#HUBSPOT_REDIRECT_URI= xxxxx
#HUBSPOT_EXCHANGE_FOR_TOKEN_URL= xxxxx

# Rabbit integration credentials
#RABBITMQ_USERNAME= xxxxx
#RABBITMQ_PASSWORD= xxxxx

# Rabbit fallback queue
#RABBIT_HUBSPOT_CONTACT_FALLBACK_QUEUE= xxxxx
#RABBIT_HUBSPOT_CONTACT_DELAY_FALLBACK_QUEUE= xxxxx
#RABBIT_HUBSPOT_CONTACT_FALLBACK_ERROR_QUEUE= xxxxx
```

## Instalação e Execução

A aplicação está containerizada e pode ser facilmente implantada usando Docker Compose:

```bash
docker compose up --build
```

Isso iniciará todos os serviços necessários:
- Servidor API (padrão: porta 8080)
- RabbitMQ (portas: 5672, 15672)
- Banco de dados H2

---

## Arquitetura e Decisões Técnicas

- **Extensibilidade:**  
  A API foi construída de forma modular. A API conta com um serviço que utiliza um `genericClient`, permitindo que os clientes (ou _providers_) de autorização ou alvos de criação de contatos estendam esse client de forma simples. Essa abordagem garante que, no futuro, a adição de um novo provider ou target seja ágil e com o mínimo de alterações no fluxo existente.

- **Persistência e Estado:**  
  Utilizei o banco H2 para armazenar a sessão do usuário em memória. Para ambientes de produção, recomenda-se a migração para um banco robusto ou um banco não relacional (como MongoDB ou DynamoDB), dependendo da natureza dos dados e requisitos do projeto.

- **Gerenciamento de Falhas com RabbitMQ:**  
  Em caso de retorno 429 (Too Many Requests) pela API do Hubspot, as mensagens são encaminhadas para uma fila de delay com TTL de 60 segundos. Após esse período, a mensagem é redirecionada para a fila principal, onde o fluxo de reprocessamento tenta novamente inserir o contato. Se persistir o erro, a mensagem é movida para uma fila DLQ, permitindo o tratamento posterior. Uma melhoria futura prevista é a implementação de um TTL dinâmico, que se ajustaria conforme a quantidade de mensagens, garantindo um reprocessamento mais escalonado.

- **Integração com Hubspot via Webhook e Ngrok:**
  Para facilitar a visualização e o teste do fluxo de notificação, foi criado um novo endpoint que espera chamadas do Hubspot sempre que um novo contato é cadastrado. Com a integração do Ngrok, é possível simular um ambiente HTTPS, permitindo que o webhook configurado no Hubspot seja acionado corretamente.

---

## Documentação da API

### Autorização

#### Obter URL de Login
```http
GET /api/v1/auth/login-url?provider=HUBSPOT
```
Retorna uma URL para o usuário acessar o sistema, baseada no _authorization provider_ desejado (quando houver mais de um, o parâmetro `provider` pode ser informado na requisição).

**Exemplo de Resposta:**
```json
{
  "link": "https://app.hubspot.com/oauth/authorize?client_id=xxxx&scope=contacts&redirect_uri=http://localhost:8080/api/v1/auth/callback"
}
```

#### Listar Providers Disponíveis
```http
GET /api/v1/auth/available-providers
```
Retorna os _authorization providers_ disponíveis para uso.

**Exemplo de Resposta:**
```json
{
  "providers": ["HUBSPOT"]
}
```

#### Callback de Autorização
```http
GET /api/v1/auth/callback?code=xyz&state=HUBSPOT
```
Endpoint usado após a autenticação do usuário no provedor, gera tokens de acesso.

**Exemplo de Resposta:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

### Gestão de Contatos

#### Criar Contato
```http
POST /api/v1/contacts
Header: Authorization: Bearer {token}
```
```json
{
    "email": "exemplo@dominio.com",
    "firstName": "João",
    "lastName": "Silva"
}
```
Recebe um objeto contendo os dados do contato e, caso ocorra um retorno 429 pelo provider (por exemplo, Hubspot), a requisição é encaminhada para a fila de delay e posteriormente reprocessada.

**Exemplo de Resposta:**
```json
{
  "email": "exemplo@dominio.com",
  "firstName": "João",
  "lastName": "Silva"
}
```

#### Listar Targets Disponíveis
```http
GET /api/v1/contacts/available-targets
```
Retorna uma lista das plataformas de gestão de contatos suportadas.

**Exemplo de Resposta:**
```json
{
  "targets": ["HUBSPOT"]
}
```

### Webhook para Notificação de Novo Contato
Este endpoint foi criado para receber chamadas do Hubspot sempre que um novo contato é cadastrado. Com a integração do Ngrok, é possível testar este fluxo como se a API estivesse operando em um ambiente HTTPS
```http
POST /api/v1/webhooks/contacts
```
```json
[
  {
  "eventId": 1534991568,
  "subscriptionId": 3434176,
  "portalId": 49645859,
  "appId": 10457515,
  "occurredAt": 1744159176740,
  "subscriptionType": "contact.creation",
  "attemptNumber": 6,
  "objectId": 112558566461,
  "changeFlag": "CREATED",
  "changeSource": "INTEGRATION",
  "sourceId": "10457515"
  }
]
```
**Exemplo de Resposta (log no código):**
```
-=-=-=-=-NEW CONTACT CREATED-=-=-=-=-
[
    ContactIntegrationCallback [
    eventId=2973775008,
    subscriptionId=3434176,
    portalId=49645859,
    appId=10457515,
    occurredAt=1744160564627,
    subscriptionType=contact.creation,
    attemptNumber=0,
    objectId=112572146736,
    changeFlag=CREATED,
    changeSource=INTEGRATION,
    sourceId=10457515
    ]
]
-=-=-=-=-NEW CONTACT CREATED-=-=-=-=-
```


## Ferramentas de Desenvolvimento

### Acesso ao Banco de Dados
- Console H2: http://localhost:8080/h2-console
- JDBC URL: jdbc:h2:mem:testdb
- Usuário: sa 
- Senha: (deixar em branco)

### Gerenciamento do RabbitMQ
- Console: http://localhost:15672
- Usuário: guest
- Senha: guest

## Melhorias Futuras

1. Implementação de TTL Dinâmico
   - Implementar backoff exponencial para mecanismos de retry
   - Adicionar parâmetros de delay configuráveis


2. Melhorias no Banco de Dados
   - Migração para bancos de dados de produção


3. Melhorias na Autenticação
   - Provedores OAuth adicionais
   - Gerenciamento aprimorado de tokens
   - Implementação de refresh token


4. Monitoramento e Observabilidade
   - Integração com ferramentas de monitoramento
   

5. Melhorias de Segurança
   - Limitação de taxa por cliente
   - Mecanismos de autenticação aprimorados
   - Implementação de headers de segurança

[Voltar ao topo](#authorization-and-integration-api-platform)
