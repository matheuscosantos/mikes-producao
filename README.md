# Mikes - Produção
[![Coverage Status](https://s3.amazonaws.com/assets.coveralls.io/badges/coveralls_90.svg)](https://s3.amazonaws.com/assets.coveralls.io/badges/coveralls_90.svg)
[![License](http://img.shields.io/:license-apache-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)

Serviço responsável por gerenciar a produção dos pedidos.

[Desenho da arquitetura](https://drive.google.com/file/d/12gofNmXk8W2QnhxiFWCI4OmvVH6Vsgun/view?usp=drive_link)

## 🚀 Começando

Essas instruções permitirão que você obtenha uma cópia do projeto em operação na sua máquina local para fins de desenvolvimento e teste.

### 📋 Pré-requisitos

Para construir e executar o aplicativo você precisa?

- [JDK 21](https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html)
- [Gradle](https://gradle.org/install/)
- [Docker](https://docs.docker.com/get-docker/)

### 🔧 Executando o aplicativo localmente

Existem várias maneiras de executar um aplicativo Spring Boot em sua máquina local. Uma maneira é executar o método `main` na classe `br.com.fiap.mikes.production.ProductionApplication` do seu IDE. Se for preciso você pode confifurar as variaveis de ambiente na configuração da sua IDE.

```
DB_PORT=5433;
DB_USER=postgres;
DB_HOST=localhost;
DB_NAME=postgres;
DB_PASSWORD=mysecretpassword;
AWS_REGION=sa-east-1;
AWS_ENDPOINT_HOST=http://localhost:4566;
SQS_INIT_PRODUCTION_URL=local-queue;
SNS_PRODUCTION_STATUS_CHANGED_ARN=local-topic
```

Alternativamente, você pode usar o [docker-compose](https://docs.docker.com/compose/) dentro da pasta raiz do projeto assim:

```
docker-compose up -d
```

## ⚙️ Executando os testes

Na aplicação existes dois tipos de testes, os testes unitários e os testes de comportamento. Para executar os testes unitários execute o comando abaixo:

### 🔩 Testes unitários

Os testes unitários testam as classes individualmente, sem dependências externas. Para executar os testes unitários execute o comando abaixo:

```
./gradlew test
```

### ⌨️ Testes de comportamento

Os testes de comportamento testam a aplicação como um todo, simulando as requisições HTTP e o recebimento e envio de mensagens utilizando SQS e SNS. Para executar os testes de comportamento execute o comando abaixo:

```
./gradlew behaviorTest
```

## 📄 Arquitetura da aplicação

A aplicação foi desenvolvida utilizando o padrão de arquitetura hexagonal, onde a camada `application` é o centro da aplicação e as camadas `infrastructure` e `adapter` são periféricas. A camada `application` não conhece as camadas periféricas, mas as camadas periféricas conhecem a camada `application`. A camada `adapter` é responsável por receber as requisições HTTP, enviar as mensagens para para o tópico SNS e receber as mensagens que são enviadas para a fila SQS. A camada de `infrascruture` é responsável por montar os beans e realizar as configurações necessárias para utilizar os serviços da AWS.

### Integração com SQS

A aplicação fica escutando a fila SQS `iniciar_producao` e quando uma mensagem é recebida, a aplicação processa a mensagem, válida as informações e caso tenha sucesso salva a informação registrando uma nova produção de um pedido.

### Integração com SNS

A aplicação caso salve com sucesso a ação de produzir um novo pedido envia uma mensagem para o tópico SNS `status_producao_alterado` com o status da produção.

### Api rest para alteração de status da produção

A aplicação disponibiliza uma api rest para alterar o status da produção de um pedido. A api rest recebe o id do pedido e o status que deve ser alterado. A aplicação valida se o pedido existe e se o status é válido, caso seja válido a aplicação altera o status da produção do pedido e envia uma mensagem para o tópico SNS `status_producao_alterado` com o status da produção.

````shell
curl --request POST \
  --url https://kyy8s6wh4i.execute-api.us-east-2.amazonaws.com/dev/production-history \
  --header 'Authorization: Bearer token' \
  --header 'Content-Type: application/json' \
  --data '{
	"orderId": "695b2a87-c6b2-4400-a208-0a183d3ff833",
	"status": "PREPARING"
}'
````

## Padrão SAGA Coreografado

O padrão Saga Coreografado foi utilizado no projeto pois é usado em aplicações distribuídas e microserviços para garantir a consistência em transações que envolvem múltiplos serviços. Nesse padrão, cada serviço envolvido em uma transação realiza uma parte da operação e emite eventos para indicar seu estado. Outros serviços ou um coordenador monitoram esses eventos e coordenam as operações para garantir que a transação seja concluída com sucesso ou revertida de forma consistente.

Existem várias razões para usar o padrão Saga Coreografado em aplicações como as descritas nos repositórios do projeto "Mikes":

Consistência distribuída: Como as transações envolvem vários serviços, é importante garantir que eles estejam em um estado consistente, mesmo em caso de falhas.
Escalabilidade e desempenho: O padrão permite que as operações sejam distribuídas entre vários serviços, melhorando a escalabilidade e o desempenho do sistema.
Resiliência: O padrão ajuda a tornar o sistema mais resiliente a falhas, pois permite que as transações sejam revertidas de forma consistente em caso de falha em um dos serviços.
Visibilidade e monitoramento: Como cada serviço emite eventos para indicar seu estado, é mais fácil monitorar e diagnosticar problemas no sistema.
Flexibilidade e manutenção: O padrão torna o sistema mais flexível, pois permite que novos serviços sejam adicionados ou alterados sem alterar a lógica de negócios existente.
Em resumo, o padrão Saga Coreografado é usado em aplicações distribuídas e microserviços para garantir a consistência e a integridade das transações, mesmo em um ambiente distribuído e com alta escalabilidade.