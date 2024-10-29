package com.opcr.payMyBuddy.service;

import com.opcr.payMyBuddy.exception.UserDoesNotExistException;
import com.opcr.payMyBuddy.model.Transaction;
import com.opcr.payMyBuddy.model.User;
import com.opcr.payMyBuddy.repository.TransactionRepository;
import com.opcr.payMyBuddy.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    private static final Logger logger = LogManager.getLogger(TransactionService.class);

    public Iterable<Transaction> getTransactions() {
        return transactionRepository.findAll();
    }

    /**
     * Return a list of all the transaction sent by the User with userId.
     *
     * @param userId id of the User.
     * @return a list of the Transaction sent.
     * @throws UserDoesNotExistException if no User exist with the userId.
     */
    public List<Transaction> getTransactionSent(Integer userId) throws UserDoesNotExistException {
        if (!userRepository.existsById(userId)) {
            String errorMessage = "User with id %s not found.".formatted(userId);
            logger.error(errorMessage);
            throw new UserDoesNotExistException(errorMessage);
        }
        return transactionRepository.findBySenderId(userId);
    }

    /**
     * Create a new transaction between two Users.
     * The transaction must contain a description and an amount.
     *
     * @param idSender    id of the User who want to send an amount.
     * @param idReceiver  id of the User who receive an amount.
     * @param description the reason of the transaction.
     * @param amount      the amount of the transaction.
     * @throws UserDoesNotExistException if one of the User does not exist.
     **/
    public void addTransaction(Integer idSender, Integer idReceiver, String description, double amount) throws UserDoesNotExistException {
        User sender = userRepository.findById(idSender).orElse(null);
        User receiver = userRepository.findById(idReceiver).orElse(null);

        if (sender == null) {
            String errorMessage = "Sender with id %s not found.".formatted(idSender);
            logger.error(errorMessage);
            throw new UserDoesNotExistException(errorMessage);
        } else if (receiver == null) {
            String errorMessage = "Receiver with id %s not found.".formatted(idReceiver);
            logger.error(errorMessage);
            throw new UserDoesNotExistException(errorMessage);
        } else {
            Transaction newTransaction = new Transaction();
            newTransaction.setDescription(description);
            newTransaction.setAmount(amount);
            newTransaction.setSender(sender);
            newTransaction.setReceiver(receiver);
            transactionRepository.save(newTransaction);
            logger.info("Transaction saved : %s.".formatted(newTransaction));
        }
    }
}
