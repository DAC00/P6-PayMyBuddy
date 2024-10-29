package com.opcr.payMyBuddy.service;

import com.opcr.payMyBuddy.exception.UserDoesNotExistException;
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
        List<Transaction> transactions = transactionService.getTransactionSent(1);
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
        Integer userId = 99;
        UserDoesNotExistException exception = assertThrows(UserDoesNotExistException.class,
                () -> transactionService.getTransactionSent(userId));
        assertEquals("User with id %s not found.".formatted(userId), exception.getMessage());
    }

    @Test
    public void addTransactionTest() throws Exception {
        transactionService.addTransaction(1, 2, "TEST", 99);

        Transaction newTransaction = transactionService.getTransactionSent(1).stream()
                .filter(transaction -> transaction.getDescription().equals("TEST")).findFirst().orElse(null);
        assertNotNull(newTransaction);
        assertEquals(2, newTransaction.getReceiver().getId());
        assertEquals("TEST", newTransaction.getDescription());
        assertEquals(99, newTransaction.getAmount());

        transactionRepository.delete(newTransaction);
    }

    @Test
    public void addTransactionWhenSenderDoesNotExistTest() {
        Integer senderId = 99;
        UserDoesNotExistException exception = assertThrows(UserDoesNotExistException.class,
                () -> transactionService.addTransaction(senderId, 1, "TEST", 100));
        assertEquals("Sender with id %s not found.".formatted(senderId), exception.getMessage());
    }

    @Test
    public void addTransactionWhenReceiverDoesNotExistTest() {
        Integer receiverId = 99;
        UserDoesNotExistException exception = assertThrows(UserDoesNotExistException.class,
                () -> transactionService.addTransaction(1, receiverId, "TEST", 100));
        assertEquals("Receiver with id %s not found.".formatted(receiverId), exception.getMessage());
    }
}
