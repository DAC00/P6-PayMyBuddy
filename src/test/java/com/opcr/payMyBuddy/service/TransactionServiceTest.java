package com.opcr.payMyBuddy.service;

import com.opcr.payMyBuddy.exception.BuddyUserDoesNotExistException;
import com.opcr.payMyBuddy.model.Transaction;
import com.opcr.payMyBuddy.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class TransactionServiceTest {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionService transactionService;

    @Test
    public void getTransactionSentTest() throws Exception {
        String emailUser = "alice@mail.com";
        List<Transaction> transactions = transactionService.getTransactionSent(emailUser);
        assertEquals(2, transactions.size());
        assertEquals("Transaction 1", transactions.getFirst().getDescription());
        assertEquals(100, transactions.getFirst().getAmount());
        assertEquals("bob", transactions.getFirst().getReceiver().getUsername());
        assertEquals("Transaction 4", transactions.get(1).getDescription());
        assertEquals(75, transactions.get(1).getAmount());
        assertEquals("dave", transactions.get(1).getReceiver().getUsername());
    }

    @Test
    public void getTransactionSentUserDoesExist() {
        String emailUser = "test@test.test";
        BuddyUserDoesNotExistException exception = assertThrows(BuddyUserDoesNotExistException.class,
                () -> transactionService.getTransactionSent(emailUser));
        assertEquals("BuddyUser : %s not found.".formatted(emailUser), exception.getMessage());
    }

    @Test
    public void addTransactionTest() throws Exception {
        String emailSender = "alice@mail.com";
        String emailReceiver = "bob@mail.com";
        transactionService.addTransaction(emailSender, emailReceiver, "TEST", 99);

        Transaction newTransaction = transactionService.getTransactionSent(emailSender).stream()
                .filter(transaction -> transaction.getDescription().equals("TEST")).findFirst().orElse(null);
        assertNotNull(newTransaction);
        assertEquals(2, newTransaction.getReceiver().getId());
        assertEquals("TEST", newTransaction.getDescription());
        assertEquals(99, newTransaction.getAmount());

        transactionRepository.delete(newTransaction);
    }

    @Test
    public void addTransactionWhenSenderDoesNotExistTest() {
        String emailSender = "test@test.test";
        String emailReceiver = "alice@mail.com";
        BuddyUserDoesNotExistException exception = assertThrows(BuddyUserDoesNotExistException.class,
                () -> transactionService.addTransaction(emailSender, emailReceiver, "TEST", 100));
        assertEquals("Sender %s not found.".formatted(emailSender), exception.getMessage());
    }

    @Test
    public void addTransactionWhenReceiverDoesNotExistTest() {
        String emailSender = "alice@mail.com";
        String emailReceiver = "test@test.test";
        BuddyUserDoesNotExistException exception = assertThrows(BuddyUserDoesNotExistException.class,
                () -> transactionService.addTransaction(emailSender, emailReceiver, "TEST", 100));
        assertEquals("Receiver %s not found.".formatted(emailReceiver), exception.getMessage());
    }
}
