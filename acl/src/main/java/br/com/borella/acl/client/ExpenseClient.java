package br.com.borella.acl.client;

import br.com.borella.acl.wsdl.expenses.GetExpensesByDateRequest;
import br.com.borella.acl.wsdl.expenses.GetExpensesByDateResponse;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;

@Component
public class ExpenseClient {

    private final WebServiceTemplate webServiceTemplate;

    public ExpenseClient(WebServiceTemplate webServiceTemplate) {
        this.webServiceTemplate = webServiceTemplate;
    }

    // Busca despesas por data
    public GetExpensesByDateResponse getExpensesByDate(String date) {
        GetExpensesByDateRequest request = new GetExpensesByDateRequest();
        request.setDate(date);
        return (GetExpensesByDateResponse) webServiceTemplate.marshalSendAndReceive(
                "http://localhost:8080/ws/expenses", request
        );
    }

    // Busca todas as despesas
    public GetExpensesByDateResponse getAllExpenses() {
        GetExpensesByDateRequest request = new GetExpensesByDateRequest();
        request.setDate("ALL"); // ou deixe null se o SOAP aceitar
        return (GetExpensesByDateResponse) webServiceTemplate.marshalSendAndReceive(
                "http://localhost:8080/ws/expenses", request
        );
    }
}
