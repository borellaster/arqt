package br.com.borella.camel.route;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class ExpenseRoute extends RouteBuilder {

    @Override
    public void configure() {

        from("timer://fetchExpenses?repeatCount=1")
                .routeId("fetch-expenses-route")
                .setHeader("CamelHttpMethod", constant("GET"))
                .to("http://localhost:8081/expenses/2025-09-16")
                .log("Response from ACL: ${body}")
                .to("rabbitmq:expenses-exchange"
                        + "?hostname=127.0.0.1"
                        + "&portNumber=5672"
                        + "&username=guest"
                        + "&password=guest"
                        + "&queue=expenses-queue"
                        + "&routingKey=expenses"
                        + "&autoDelete=false")
                .log("Mensagem publicada no RabbitMQ: ${body}");
    }
}
