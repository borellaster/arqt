package br.com.borella.camel.route;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class ExpenseRoute extends RouteBuilder {

    @Override
    public void configure() {

        from("timer://fetchExpenses?period=10000") // roda uma vez ao iniciar
                .routeId("fetch-expenses-route")
                .setHeader("CamelHttpMethod", constant("GET"))
                .to("http://localhost:8081/expenses/2025-09-09") // data fixa, pode parametrizar
                .log("Response from ACL: ${body}");
    }
}

