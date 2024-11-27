package com.opcr.payMyBuddy.controller;

import com.opcr.payMyBuddy.exception.BuddyUserDoesNotExistException;
import com.opcr.payMyBuddy.model.Transaction;
import com.opcr.payMyBuddy.service.BuddyUserService;
import com.opcr.payMyBuddy.service.TransactionService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private BuddyUserService buddyUserService;

    private static final Logger logger = LogManager.getLogger(TransactionController.class);

    @GetMapping("/transaction")
    public String transactionView(Model model) {
        fillTransactionList(model);
        return "transaction";
    }

    @PostMapping("/transaction")
    public String newTransaction(@RequestParam("receiver") String receiver, @RequestParam("description") String description,
                                 @RequestParam("amount") double amount, Model model) {
        try {
            transactionService.addTransaction(SecurityContextHolder.getContext().getAuthentication().getName(), receiver, description, amount);
            model.addAttribute("returnMessage", "Transaction envoyée.");
            fillTransactionList(model);
        } catch (BuddyUserDoesNotExistException e) {
            model.addAttribute("returnMessage", "Erreur.");
            logger.error(e.getMessage());
        }
        return "transaction";
    }

    private void fillTransactionList(Model model) {
        try {
            String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            List<Transaction> transactionList = transactionService.getTransactionSent(userEmail);
            model.addAttribute("buddyUser", buddyUserService.getBuddyUser(userEmail).getBuddyUserConnections());
            model.addAttribute("transactionSend", transactionList);
        } catch (BuddyUserDoesNotExistException e) {
            logger.error(e.getMessage());
        }
    }
}
