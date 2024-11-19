package com.opcr.payMyBuddy.service;

import com.opcr.payMyBuddy.exception.BuddyUserDoesNotExistException;
import com.opcr.payMyBuddy.model.BuddyUser;
import com.opcr.payMyBuddy.model.Transaction;
import com.opcr.payMyBuddy.repository.BuddyUserRepository;
import com.opcr.payMyBuddy.repository.TransactionRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private BuddyUserRepository buddyUserRepository;

    private static final Logger logger = LogManager.getLogger(TransactionService.class);

    /**
     * Return a list of all the transaction sent by the BuddyUser with his email.
     *
     * @param emailUser unique email of the BuddyUser.
     * @return a list of the Transaction sent.
     * @throws BuddyUserDoesNotExistException if no BuddyUser exist with the userId.
     */
    public List<Transaction> getTransactionSent(String emailUser) throws BuddyUserDoesNotExistException {
        BuddyUser buddyUser = buddyUserRepository.findByEmail(emailUser);
        if (buddyUser == null) {
            throw new BuddyUserDoesNotExistException("BuddyUser : %s not found.".formatted(emailUser));
        }
        return transactionRepository.findBySenderId(buddyUser.getId());
    }

    /**
     * Create a new transaction between two Users.
     * The transaction must contain a description and an amount.
     *
     * @param emailSender   email of the BuddyUser who want to send an amount.
     * @param emailReceiver email of the BuddyUser who receive an amount.
     * @param description   the reason of the transaction.
     * @param amount        the amount of the transaction.
     * @throws BuddyUserDoesNotExistException if one of the BuddyUser does not exist.
     **/
    @Transactional
    public void addTransaction(String emailSender, String emailReceiver, String description, double amount) throws BuddyUserDoesNotExistException {
        BuddyUser sender = buddyUserRepository.findByEmail(emailSender);
        BuddyUser receiver = buddyUserRepository.findByEmail(emailReceiver);

        if (sender == null) {
            throw new BuddyUserDoesNotExistException("Sender %s not found.".formatted(emailSender));
        } else if (receiver == null) {
            throw new BuddyUserDoesNotExistException("Receiver %s not found.".formatted(emailReceiver));
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
