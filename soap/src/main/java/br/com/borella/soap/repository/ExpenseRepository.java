package br.com.borella.soap.repository;

import com.borella.soap.expenses.Expense;
import com.borella.soap.expenses.Status;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ExpenseRepository {

    private final List<Expense> expenses = new ArrayList<>();

    public ExpenseRepository() {
        expenses.add(createExpense("1", "Cliente A", "Compra de material", LocalDate.now(), new BigDecimal("150.50"), Status.PAID));
        expenses.add(createExpense("2", "Cliente B", "Serviço prestado", LocalDate.now(), new BigDecimal("300.00"), Status.PENDING));
        expenses.add(createExpense("3", "Cliente C", "Assinatura mensal", LocalDate.now().minusDays(1), new BigDecimal("99.99"), Status.PAID));
        expenses.add(createExpense("4", "Cliente D", "Manutenção", LocalDate.now(), new BigDecimal("450.75"), Status.CANCELLED));
    }

    private Expense createExpense(String id, String client, String description, LocalDate date, BigDecimal amount, Status status) {
        Expense e = new Expense();
        e.setId(id);
        e.setClient(client);
        e.setDescription(description);
        e.setDate(date.toString());
        e.setAmount(amount);
        e.setStatus(status);
        return e;
    }

    public List<Expense> findByDate(String date) {
        return expenses.stream()
                .filter(e -> e.getDate().equals(date))
                .collect(Collectors.toList());
    }
}

