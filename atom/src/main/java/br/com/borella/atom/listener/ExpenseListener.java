package br.com.borella.atom.listener;

import br.com.borella.atom.entity.Expense;
import br.com.borella.atom.repository.ExpenseRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class ExpenseListener {

    private final ExpenseRepository repository;
    private final ObjectMapper objectMapper;

    public ExpenseListener(ExpenseRepository repository) {
        this.repository = repository;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @RabbitListener(queues = "expenses-queue")
    public void receiveMessage(org.springframework.amqp.core.Message message) throws Exception {
        String messageBody = new String(message.getBody());
        System.out.println("📥 Mensagem recebida: " + messageBody);

        List<Expense> expenses = objectMapper.readValue(messageBody, new TypeReference<List<Expense>>() {});
        expenses.forEach(e -> {
            e.setId(null);
            if (e.getDate() == null) e.setDate(LocalDate.now());
        });
        repository.saveAll(expenses);
    }

}
