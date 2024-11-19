package com.opcr.payMyBuddy.controller;

import com.opcr.payMyBuddy.exception.EmailAlreadyExistsException;
import com.opcr.payMyBuddy.model.BuddyUser;
import com.opcr.payMyBuddy.service.BuddyUserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class SignupController {

    @Autowired
    private BuddyUserService buddyUserService;

    private static final Logger logger = LogManager.getLogger(SignupController.class);

    @GetMapping("/signup")
    public String signupView(Model model) {
        model.addAttribute("buddyUser",new BuddyUser());
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(@ModelAttribute("buddyUser") BuddyUser newBuddyUser, Model model){
        try {
            buddyUserService.addUser(newBuddyUser.getUsername(),newBuddyUser.getEmail(),newBuddyUser.getPassword());
            logger.info("New user created : %s".formatted(newBuddyUser.getEmail()));
            return "redirect:/login?signup=true";
        } catch (EmailAlreadyExistsException e) {
            model.addAttribute("returnMessage", "Email already taken.");
            logger.error(e.getMessage());
            return "signup";
        }
    }
}
