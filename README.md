# Transaction Authorizer API

Este projeto implementa um autorizador de transações que processa diferentes categorias de benefícios (FOOD, MEAL, CASH) baseado no MCC (Merchant Category Code) e no nome do estabelecimento.

## Tecnologias Utilizadas

- Java 21
- Spring Boot 3.3.6
- Maven
- SpringDoc OpenAPI para documentação da API
- JUnit 5
- SLF4J e Logback para logging

## Como Executar o Projeto

### Pré-requisitos
- Java 21 instalado
- Maven instalado
- IDE de sua preferência (recomendo IntelliJ IDEA ou Eclipse)

### Passos para Execução

1. Clone o repositório:
```bash
git clone [url-do-repositório]
```

2. Entre na pasta do projeto:
```bash
cd transaction-authorizer
```

3. Execute o build com Maven:
```bash
mvn clean install
```

4. Execute a aplicação:
```bash
mvn spring-boot:run
```

A aplicação estará disponível em `http://localhost:8080`

### Documentação da API
A documentação da API é gerada automaticamente pelo OpenAPI e pode ser acessada em:
```
http://localhost:8080/v3/api-docs
```

## Estrutura do Projeto

O projeto está organizado nas seguintes classes principais:

- `TransactionAuthorizerApplication`: Classe principal que inicia a aplicação Spring Boot
- `AuthorizerController`: Controller REST que processa as requisições de autorização
- `Transaction`: Modelo que representa uma transação
- `OpenAPIConfig`: Configuração da documentação OpenAPI

### Endpoints

#### POST /authorize
Autoriza uma transação baseada nas regras de negócio definidas.

Exemplo de payload:
```json
{
    "id": "123",
    "accountId": "ACC001",
    "amount": 100.00,
    "merchant": "UBER EATS",
    "mcc": "5811"
}
```

Possíveis respostas:
- `{"code": "00"}` - Transação aprovada
- `{"code": "51"}` - Saldo insuficiente
- `{"code": "07"}` - Erro no processamento

## Resposta à Questão L4: Transações Simultâneas

Para lidar com o desafio de transações simultâneas no mesmo cartão, considerando o limite de 100ms para processamento, proporia a seguinte solução:

1. Implementaria um sistema de lock otimista usando números de versão ou timestamps para detectar modificações concorrentes.

2. Em caso de conflito durante o lock otimista, ao invés de tentar a transação novamente imediatamente, a transação seria serializada e colocada em uma fila para processamento posterior.

3. Um componente separado ficaria responsável por processar a fila de transações sequencialmente para cada conta.

4. Se durante o processamento for detectado um conflito (por exemplo, número de versão diferente), a transação pode ser tentada novamente com o estado atualizado da conta.

Esta abordagem híbrida combina os benefícios do lock otimista e da serialização, permitindo processamento concorrente de transações enquanto minimiza o overhead de bloqueio. A fila ajuda a gerenciar a ordem de processamento e oferece opções de escalabilidade.

Para atender ao requisito de 100ms, o sistema seria otimizado focando em:
- Queries otimizadas no banco de dados
- Minimização da latência de rede
- Processamento eficiente da fila de transações

Também seria importante implementar monitoramento e logging para:
- Acompanhar tempos de processamento
- Identificar gargalos
- Alertar quando o tempo de processamento ultrapassar o limite definido

## Testes

Para executar os testes:
```bash
mvn test
```

Os testes cobrem:
- Autorização de transações para diferentes categorias
- Fallback para saldo CASH
- Timeout de transações
- Transações com MCCs nulos ou inválidos
- Múltiplas transações sucessivas
