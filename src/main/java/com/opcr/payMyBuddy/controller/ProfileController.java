package com.opcr.payMyBuddy.controller;

import com.opcr.payMyBuddy.exception.BuddyUserDoesNotExistException;
import com.opcr.payMyBuddy.exception.EmailAlreadyExistsException;
import com.opcr.payMyBuddy.exception.NoInfoToUpdateException;
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
public class ProfileController {

    @Autowired
    private BuddyUserService buddyUserService;

    private static final Logger logger = LogManager.getLogger(ProfileController.class);

    @GetMapping("/profile")
    public String profileView(Model model) {
        BuddyUser buddyUser = buddyUserService.getBuddyUser(SecurityContextHolder.getContext().getAuthentication().getName());
        model.addAttribute("buddyUser", buddyUser);
        return "profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@ModelAttribute("buddyUser") BuddyUser buddyUser, Model model) {
        try {
            buddyUserService.updateUser(SecurityContextHolder.getContext().getAuthentication().getName(), buddyUser.getUsername(), buddyUser.getEmail(), buddyUser.getPassword());
            model.addAttribute("returnMessage", "Information mise à jour.");
            logger.info("Information updated for : %s.".formatted(SecurityContextHolder.getContext().getAuthentication().getName()));
        } catch (EmailAlreadyExistsException e) {
            logger.error(e.getMessage());
            model.addAttribute("returnMessage", "Le mail est déjà pris.");
        } catch (NoInfoToUpdateException e) {
            logger.error(e.getMessage());
            model.addAttribute("returnMessage", "Il n'y a rien à mettre à jour.");
        } catch (BuddyUserDoesNotExistException e) {
            logger.error(e.getMessage());
        }
        return "/profile";
    }
}
