package com.opcr.payMyBuddy.controller;

import com.opcr.payMyBuddy.model.Transaction;
import com.opcr.payMyBuddy.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TransactionService transactionService;

    @Test
    @WithMockUser(username = "alice@mail.com")
    @Transactional
    public void newTransactionTest() throws Exception {

        String emailSender = "alice@mail.com";
        String emailReceiver = "bob@mail.com";
        String description = "Test";
        double amount = 99.99;

        this.mockMvc.perform(post("/transaction")
                        .param("receiver", emailReceiver)
                        .param("description", description)
                        .param("amount", String.valueOf(amount))
                        .with(csrf()))
                .andExpect(status().isOk());

        Transaction newTransaction = transactionService.getTransactionSent(emailSender).stream()
                .filter(transaction -> transaction.getDescription().equals(description)).findFirst().orElse(null);

        assertNotNull(newTransaction);
        assertEquals(2, newTransaction.getReceiver().getId());
        assertEquals(description, newTransaction.getDescription());
        assertEquals(amount, newTransaction.getAmount());
    }
}
