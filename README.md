# Projeto Integrado: SOAP + ACL + Apache Camel

Este projeto demonstra uma **integração completa** de um serviço SOAP, um cliente REST/ACL e um fluxo Apache Camel para consumir e processar os dados de despesas de clientes.

---

## 1. Estrutura do Projeto

O repositório contém três módulos/projetos:

```
.
├── soap       # Serviço SOAP que fornece informações sobre despesas
├── acl        # API REST que consome o SOAP e expõe JSON
└── camel      # Apache Camel que consome a API REST periodicamente
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

Consumir a API REST do ACL periodicamente e processar as despesas.

### 4.2. Configuração

Exemplo de rota Camel:

```java
from("timer://fetchExpenses?period=10000")
    .setHeader(Exchange.HTTP_METHOD, constant("GET"))
    .to("http://localhost:8081/expenses/2025-09-09")
    .unmarshal().json(JsonLibrary.Jackson)
    .process(exchange -> {
        List<Map<String,Object>> expenses = exchange.getMessage().getBody(List.class);
        expenses.forEach(System.out::println);
    });
```

* Executa **a cada 10 segundos** (`period=10000` ms).
* Converte JSON em `List<Map<String,Object>>`.
* Processa cada despesa (ex: imprime no console ou envia para outro sistema).

---

## 5. Fluxo Geral

```
+----------------+       SOAP       +----------------+       REST       +----------------+
|     Cliente    |  <------------>  |     SOAP       |  <------------>  |      ACL       |
|  (ou Postman)  |                  |   Expenses     |                  |  JSON Output   |
+----------------+                  +----------------+                  +----------------+
                                                                      |
                                                                      |
                                                                      v
                                                                +----------------+
                                                                |  Apache Camel  |
                                                                |  Process Flow  |
                                                                +----------------+
```

* O **SOAP** fornece os dados.
* O **ACL** transforma em JSON.
* O **Camel** consome JSON e processa automaticamente a cada intervalo.

---

## 6. Executando os Projetos

### 6.1. SOAP

```bash
cd soap
mvn spring-boot:run
```

* Porta padrão: `8080`

### 6.2. ACL

```bash
cd acl
mvn spring-boot:run
```

* Porta padrão: `8081`

### 6.3. Camel

```bash
cd camel
mvn spring-boot:run
```

* Porta padrão: `8082` (não interfere nas demais)
* O Camel vai consumir a URL do ACL automaticamente.

---

## 7. Observações

* O SOAP é apenas **simulado**, com dados fictícios.
* O ACL pode ser expandido para filtros, autenticação e outras entidades.
* O Apache Camel pode processar, transformar e enviar os dados para bancos, filas ou relatórios.
* Todos os projetos usam **Java 17** e **Spring Boot 3.5.5**.

---

## 8. Testes Rápidos

1. SOAP: `http://localhost:8080/ws/expenses.wsdl`
2. ACL: `http://localhost:8081/expenses/2025-09-09`
3. Camel: observe o console para ver as despesas processadas a cada 10 segundos.
