package com.opcr.payMyBuddy.controller;

import com.opcr.payMyBuddy.exception.BuddyUserAlreadyConnectedWithException;
import com.opcr.payMyBuddy.exception.BuddyUserConnectWithHimselfException;
import com.opcr.payMyBuddy.exception.BuddyUserDoesNotExistException;
import com.opcr.payMyBuddy.model.BuddyUser;
import com.opcr.payMyBuddy.service.BuddyUserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AddConnectionController {

    @Autowired
    private BuddyUserService buddyUserService;

    private static final Logger logger = LogManager.getLogger(AddConnectionController.class);

    @GetMapping("/add_connection")
    public String addConnectionView(Model model) {
        model.addAttribute("buddyUser", new BuddyUser());
        return "add_connection";
    }

    @PostMapping("/add_connection")
    public String addConnectionBetweenUsers(@ModelAttribute("buddyUser") BuddyUser userToConnectWith, Model model) {
        String emailUser = SecurityContextHolder.getContext().getAuthentication().getName();
        logger.info("POST : Trying to connect %s & %s.".formatted(emailUser, userToConnectWith.getEmail()));
        try {
            buddyUserService.addConnectionToUser(emailUser, userToConnectWith.getEmail());
            model.addAttribute("returnMessage","Connection added.");
            logger.info("POST : Connection added between %s & %s.".formatted(emailUser, userToConnectWith.getEmail()));
            return "add_connection";
        } catch (BuddyUserDoesNotExistException e) {
            model.addAttribute("returnMessage", "User not found.");
            logger.error(e.getMessage());
            return "add_connection";
        } catch (BuddyUserAlreadyConnectedWithException e) {
            model.addAttribute("returnMessage", "Already a connection.");
            logger.error(e.getMessage());
            return "add_connection";
        } catch (BuddyUserConnectWithHimselfException e) {
            model.addAttribute("returnMessage", "Can't connect with yourself.");
            logger.error(e.getMessage());
            return "add_connection";
        }
    }
}
