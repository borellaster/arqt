# Projeto Integrado: SOAP + ACL + Apache Camel + Atom

Este projeto demonstra uma **integração completa** de um serviço SOAP, um cliente REST/ACL, um fluxo Apache Camel e um módulo final que grava os dados em **Postgres**.

---

## 1. Estrutura do Projeto

O repositório contém quatro módulos/projetos:

```
.
├── soap       # Serviço SOAP que fornece informações sobre despesas
├── acl        # API REST que consome o SOAP e expõe JSON
├── camel      # Apache Camel que consome a API REST periodicamente
└── atom       # Novo módulo que consome mensagens RabbitMQ e grava no Postgres
```

---

## 2. SOAP - Expenses Web Service

### 2.1. Objetivo

Expor um serviço SOAP para consultar despesas de clientes por data.

### 2.2. XSD (`expenses.xsd`)

Define:

* Request: `getExpensesByDateRequest` (com a data)
* Response: `getExpensesByDateResponse` (lista de despesas)
* Tipo `Expense`:

  * `id` (String)
  * `client` (String)
  * `description` (String)
  * `date` (String)
  * `amount` (Decimal)
  * `status` (`PAID`, `PENDING`, `CANCELLED`)

### 2.3. Endpoint

```http
http://localhost:8080/ws/expenses
```

* Teste via SOAP:

```xml
<ns2:getExpensesByDateRequest xmlns:ns2="http://borella.com/soap/expenses">
    <ns2:date>2025-09-09</ns2:date>
</ns2:getExpensesByDateRequest>
```

* WSDL:

```http
http://localhost:8080/ws/expenses.wsdl
```

---

## 3. ACL - API REST JSON

### 3.1. Objetivo

Consumir o SOAP e expor os dados em **JSON** para clientes REST.

### 3.2. Controller (`ExpenseController`)

* URL para buscar despesas por data:

```http
GET http://localhost:8081/expenses/{date}
```

* Exemplo de resposta:

```json
[
    {
        "id": "1",
        "client": "Cliente A",
        "description": "Compra de materiais",
        "date": "2025-09-09",
        "amount": 123.45,
        "status": "PAID"
    },
    {
        "id": "2",
        "client": "Cliente B",
        "description": "Serviços prestados",
        "date": "2025-09-09",
        "amount": 678.90,
        "status": "PENDING"
    }
]
```

### 3.3. Client SOAP (`ExpenseClient`)

* Responsável por chamar o SOAP.
* Configuração do `WebServiceTemplate` com `Jaxb2Marshaller`.

---

## 4. Apache Camel

### 4.1. Objetivo

Consumir a API REST do ACL periodicamente e enviar as despesas para a fila RabbitMQ.

### 4.2. Configuração

Exemplo de rota Camel:

```java
from("timer://fetchExpenses?period=10000")
    .setHeader(Exchange.HTTP_METHOD, constant("GET"))
    .to("http://localhost:8081/expenses/2025-09-09")
    .unmarshal().json(JsonLibrary.Jackson)
    .to("rabbitmq://localhost:5672/expenses-exchange?queue=expenses-queue&routingKey=expenses");
```

* Executa **a cada 10 segundos**.
* Converte JSON em `List<Map<String,Object>>`.
* Publica cada despesa na fila `expenses-queue`.

---

## 5. Atom - Consumo RabbitMQ e gravação em Postgres

### 5.1. Objetivo

Consumir mensagens da fila RabbitMQ e gravar as despesas na base **Postgres**.

### 5.2. Entidade `Expense`

```java
@Entity
public class Expense {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;
    private String client;
    private String status;
    private Double amount;
    private LocalDate date;
}
```

### 5.3. Listener RabbitMQ (`ExpenseListener`)

```java
@Component
public class ExpenseListener {

    private final ExpenseRepository repository;
    private final ObjectMapper objectMapper;

    public ExpenseListener(ExpenseRepository repository) {
        this.repository = repository;
        this.objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule()) // Suporte para LocalDate
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @RabbitListener(queues = "expenses-queue")
    public void receive(String message) throws Exception {
        List<Expense> expenses = objectMapper.readValue(message, new TypeReference<List<Expense>>() {});
        expenses.forEach(e -> {
            e.setId(null); // força inserção
            if (e.getDate() == null) e.setDate(LocalDate.now());
        });
        repository.saveAll(expenses);
        System.out.println("✅ Mensagens salvas no Postgres");
    }
}
```

### 5.4. Repository (`ExpenseRepository`)

```java
public interface ExpenseRepository extends JpaRepository<Expense, Long> {}
```

---

## 6. Fluxo Geral

```
+----------------+       SOAP       +----------------+       REST       +----------------+
|     Cliente    |  <------------>  |     SOAP       |  <------------>  |      ACL       |
|  (ou Postman)  |                  |   Expenses     |                  |  JSON Output   |
+----------------+                  +----------------+                  +----------------+
                                                                      |
                                                                      v
                                                                +----------------+
                                                                |  Apache Camel  |
                                                                |  Envia para    |
                                                                |   RabbitMQ     |
                                                                +----------------+
                                                                      |
                                                                      v
                                                                +----------------+
                                                                |     Atom       |
                                                                |  Consome fila  |
                                                                |  e grava no    |
                                                                |   Postgres     |
                                                                +----------------+
```

* O **SOAP** fornece os dados.
* O **ACL** transforma em JSON.
* O **Camel** envia para a fila RabbitMQ.
* O **Atom** consome a fila e grava no Postgres.

---

## 7. Executando os Projetos

### 7.1. SOAP

```bash
cd soap
mvn spring-boot:run
```

* Porta padrão: `8080`

### 7.2. ACL

```bash
cd acl
mvn spring-boot:run
```

* Porta padrão: `8081`

### 7.3. Camel

```bash
cd camel
mvn spring-boot:run
```

* Porta padrão: `8082`
* Consome automaticamente a URL do ACL e publica na fila RabbitMQ.

### 7.4. Atom

```bash
cd atom
mvn spring-boot:run
```

* Porta padrão: `8083`
* Consome mensagens de `expenses-queue` e salva no Postgres.

---

## 8. Configuração do Postgres

Exemplo `application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/expensesdb
spring.datasource.username=postgres
spring.datasource.password=senha
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

* Cria automaticamente a tabela `expense` se não existir.
* Usa `LocalDate` corretamente com `Jackson`.

---

## 9. Observações

* O `Atom` garante que IDs duplicados não causem erro, sempre insere novos registros.
* O fluxo pode ser expandido para filtros, relatórios ou outras integrações.
* Teste rápido do fluxo completo:

  1. SOAP: `http://localhost:8080/ws/expenses.wsdl`
  2. ACL: `http://localhost:8081/expenses/2025-09-09`
  3. Camel: console mostra publicações na fila
  4. Atom: console mostra mensagens salvas no Postgres
