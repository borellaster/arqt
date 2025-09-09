package br.com.borella.soap.endpoint;

import com.borella.soap.expenses.Expense;
import com.borella.soap.expenses.Status;
import com.borella.soap.expenses.GetExpensesByDateRequest;
import com.borella.soap.expenses.GetExpensesByDateResponse;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import java.math.BigDecimal;

@Endpoint
public class ExpensesEndpoint {

    private static final String NAMESPACE_URI = "http://borella.com/soap/expenses";

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getExpensesByDateRequest")
    @ResponsePayload
    public GetExpensesByDateResponse getExpensesByDate(@RequestPayload GetExpensesByDateRequest request) {
        GetExpensesByDateResponse response = new GetExpensesByDateResponse();

        Expense e1 = new Expense();
        e1.setId("1");
        e1.setClient("Cliente A");
        e1.setDescription("Almoço");
        e1.setDate(request.getDate());
        e1.setAmount(new BigDecimal("120.50"));
        e1.setStatus(Status.PAID);

        Expense e2 = new Expense();
        e2.setId("2");
        e2.setClient("Cliente B");
        e2.setDescription("Táxi");
        e2.setDate(request.getDate());
        e2.setAmount(new BigDecimal("45.00"));
        e2.setStatus(Status.PENDING);

        Expense e3 = new Expense();
        e3.setId("3");
        e3.setClient("Cliente C");
        e3.setDescription("Conta Celular");
        e3.setDate(request.getDate());
        e3.setAmount(new BigDecimal("75.85"));
        e3.setStatus(Status.PAID);

        response.getExpense().add(e1);
        response.getExpense().add(e2);
        response.getExpense().add(e3);

        return response;
    }
}

