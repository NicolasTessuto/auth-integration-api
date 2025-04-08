# Authorization and Integration API Platform

[English](#english) | [Português](#português)
## Português

### API de Autorização e Integração com Plataformas de Salvamento de Leads

Esta API foi desenvolvida com foco na integração e autorização com plataformas de salvamento de leads. Ela foi projetada para ser facilmente extensível, permitindo a adição de novos _authorization providers_ ou _targets_ de contato com o mínimo esforço, tornando-a adequada para ambientes de produção.

## Funcionalidades

- Sistema de autenticação com múltiplos provedores
- Arquitetura de integração extensível para diversas plataformas de gestão de leads
- Processamento assíncrono de mensagens com mecanismos automáticos de retry
- Proteção contra limitação de taxa (rate-limiting) com sistema inteligente de filas
- Autenticação baseada em tokens
- Sincronização de dados de leads em tempo real

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
- [Contribuição](#contribuição)
- [Licença](#licença)

---

## Tecnologias Utilizadas

- **Linguagem:** Java 21
- **Build & Dependency Management:** Gradle
- **Framework:** Spring Boot com Spring Webflux
- **Persistência:** JPA e banco H2 (poderá ser substituído por um banco de produção, como MongoDB ou DynamoDB)
- **Mensageria:** RabbitMQ (para fallback em caso de retorno 429 pela API do Hubspot)
- **Utilitários:** Lombok


## Pré-requisitos

- Docker e Docker Compose
- Java 21 JDK (para desenvolvimento local)
- Gradle 8.x
- Git

## Configuração do Ambiente

1. Clone o repositório
2. Crie um arquivo `.env` na raiz do projeto com as seguintes variáveis:

```env
# Adicione suas variáveis de ambiente aqui
# Exemplo:
# HUBSPOT_CLIENT_ID=seu_client_id
# HUBSPOT_CLIENT_SECRET=seu_client_secret
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
  A API foi construída de forma modular. Foi implementado um serviço que utiliza um `genericClient`, permitindo que os clientes (ou _providers_) de autorização ou alvos de criação de contatos estendam esse client de forma simples. Essa abordagem garante que, no futuro, a adição de um novo provider ou target seja ágil e com o mínimo de alterações no fluxo existente.

- **Persistência e Estado:**  
  Utilizei o banco H2 para armazenar a sessão do usuário em memória. Para ambientes de produção, recomenda-se a migração para um banco robusto ou um banco não relacional (como MongoDB ou DynamoDB), dependendo da natureza dos dados e requisitos do projeto.

- **Gerenciamento de Falhas com RabbitMQ:**  
  Em caso de retorno 429 (Too Many Requests) pela API do Hubspot, as mensagens são encaminhadas para uma fila de delay com TTL de 60 segundos. Após esse período, a mensagem é redirecionada para a fila principal, onde o fluxo de reprocessamento tenta novamente inserir o contato. Se persistir o erro, a mensagem é movida para uma fila DQL, permitindo o tratamento posterior. Uma melhoria futura prevista é a implementação de um TTL dinâmico, que se ajustaria conforme a quantidade de mensagens, garantindo um reprocessamento mais escalonado.

---

## Documentação da API

### Autorização

#### Obter URL de Login
```http
GET /api/v1/auth/login-url
```
Retorna uma URL para o usuário acessar o sistema, baseada no _authorization provider_ desejado (quando houver mais de um, o parâmetro `provider` pode ser informado na request).

#### Listar Providers Disponíveis
```http
GET /api/v1/auth/available-providers
```
Retorna os _authorization providers_ disponíveis para uso.

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

#### Listar Targets Disponíveis
```http
GET /api/v1/contacts/available-targets
```
Retorna uma lista das plataformas de gestão de contatos suportadas.

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
   - Implementação de clustering de banco de dados
   - Otimização de pool de conexões

3. Melhorias na Autenticação
   - Provedores OAuth adicionais
   - Gerenciamento aprimorado de tokens
   - Implementação de refresh token

4. Monitoramento e Observabilidade
   - Integração com ferramentas de monitoramento
   - Sistema de logging aprimorado
   - Coleta de métricas de desempenho

5. Melhorias de Segurança
   - Limitação de taxa por cliente
   - Mecanismos de autenticação aprimorados
   - Implementação de headers de segurança

## Contribuição

Por favor, leia nossas diretrizes de contribuição antes de enviar pull requests para o projeto.

## Licença

Este projeto está licenciado sob a Licença MIT - consulte o arquivo LICENSE para obter detalhes.

[Voltar ao topo](#authorization-and-integration-api-platform)


---

## English

A robust, scalable API platform that handles authentication and lead management integration with multiple platforms. Built with enterprise-grade architecture to support high-throughput environments and seamless platform integrations.

## Features

- Multi-provider authentication system
- Extensible integration architecture for multiple lead management platforms
- Asynchronous message processing with automatic retry mechanisms
- Rate-limiting protection with intelligent queuing system
- Token-based authentication
- Real-time lead data synchronization

## Technical Stack

- Java 21
- Spring Boot with WebFlux for reactive programming
- Spring Data JPA for data persistence
- RabbitMQ for message queuing and async processing
- H2 Database (with easy migration path to production databases)
- Gradle for build automation
- Docker for containerization
- Lombok for reduced boilerplate code

## Prerequisites

- Docker and Docker Compose
- Java 21 JDK (for local development)
- Gradle 8.x
- Git

## Environment Setup

1. Clone the repository
2. Create a `.env` file in the project root with the following variables:

```env
# Add your environment variables here
# Example:
# HUBSPOT_CLIENT_ID=your_client_id
# HUBSPOT_CLIENT_SECRET=your_client_secret
```

## Installation and Running

The application is containerized and can be easily deployed using Docker Compose:

```bash
docker compose up --build
```

This will start all necessary services:
- API Server (default: port 8080)
- RabbitMQ (ports: 5672, 15672)
- H2 Database

## API Documentation

### Authentication Endpoints

#### Get Login URL
```http
GET /api/v1/auth/login-url
```
Returns an authentication URL for user authorization.

#### List Available Providers
```http
GET /api/v1/auth/available-providers
```
Returns a list of supported authentication providers.

### Contact Management

#### Create Contact
```http
POST /api/v1/contacts
Header: Authorization: Bearer {token}
```
```json
{
    "email": "example@domain.com",
    "firstName": "John",
    "lastName": "Doe"
}
```

#### List Available Targets
```http
GET /api/v1/contacts/available-targets
```
Returns a list of supported contact management platforms.

## Development Tools

### Database Access
- H2 Console: http://localhost:8080/h2-console
- JDBC URL: jdbc:h2:mem:testdb
- Username: sa
- Password: (leave empty)

### RabbitMQ Management
- Console: http://localhost:15672
- Username: guest
- Password: guest

## Technical Decisions and Architecture

### Generic Client Architecture
The API implements a generic client architecture that allows for easy integration of new authentication providers and lead management platforms. This design decision enables:
- Rapid integration of new platforms
- Consistent interface across different providers
- Reduced code duplication
- Simplified maintenance

### Asynchronous Processing
The system utilizes RabbitMQ for reliable message processing with the following features:
- Rate limit handling with automatic retries
- TTL-based delay queues for backoff strategies
- Dead Letter Queue (DLQ) for failed message handling
- Transparent error handling for end users

### Database Selection
Currently using H2 for session management with easy migration paths to:
- Production-grade SQL databases for relational data needs
- MongoDB for document-based storage
- Amazon DynamoDB for serverless scenarios

## Future Improvements

1. Dynamic TTL Implementation
    - Implement exponential backoff for retry mechanisms
    - Add configurable delay parameters

2. Database Enhancements
    - Migration to production-grade databases
    - Implementation of database clustering
    - Connection pooling optimization

3. Authentication Enhancements
    - Additional OAuth providers
    - Enhanced token management
    - Refresh token implementation

4. Monitoring and Observability
    - Integration with monitoring tools
    - Enhanced logging system
    - Performance metrics collection

5. Security Enhancements
    - Rate limiting per client
    - Enhanced authentication mechanisms
    - Security headers implementation

## Contributing

Please read our contributing guidelines before submitting pull requests to the project.

## License

This project is licensed under the MIT License - see the LICENSE file for details.

[Back to top](#authorization-and-integration-api-platform)