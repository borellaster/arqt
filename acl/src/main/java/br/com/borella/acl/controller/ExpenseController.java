package br.com.borella.acl.controller;

import br.com.borella.acl.client.ExpenseClient;
import br.com.borella.acl.wsdl.expenses.Expense;
import br.com.borella.acl.wsdl.expenses.GetExpensesByDateResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/expenses")
public class ExpenseController {

    private final ExpenseClient expenseClient;

    public ExpenseController(ExpenseClient expenseClient) {
        this.expenseClient = expenseClient;
    }

    // Listar despesas por data
    @GetMapping("/{date}")
    public List<Map<String, Object>> getExpensesByDate(@PathVariable String date) {
        GetExpensesByDateResponse response = expenseClient.getExpensesByDate(date);

        return response.getExpense().stream()
                .map(this::toMap)
                .collect(Collectors.toList());
    }

    // Listar todas as despesas
    @GetMapping
    public List<Map<String, Object>> getAllExpenses() {
        GetExpensesByDateResponse response = expenseClient.getAllExpenses();

        return response.getExpense().stream()
                .map(this::toMap)
                .collect(Collectors.toList());
    }

    private Map<String, Object> toMap(Expense e) {
        return Map.of(
                "id", e.getId(),
                "client", e.getClient(),
                "description", e.getDescription(),
                "date", e.getDate(),
                "amount", e.getAmount(),
                "status", e.getStatus().value()
        );
    }
}
