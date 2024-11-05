package com.opcr.payMyBuddy.service;

import com.opcr.payMyBuddy.model.BuddyUser;
import com.opcr.payMyBuddy.repository.BuddyUserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class BuddyUserDetailsService implements UserDetailsService {

    private static final Logger logger = LogManager.getLogger(BuddyUserDetailsService.class);

    @Autowired
    private BuddyUserRepository buddyUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        BuddyUser buddyUser = buddyUserRepository.findByEmail(username);
        if (buddyUser != null) {
            return User.builder()
                    .username(buddyUser.getEmail())
                    .password(buddyUser.getPassword())
                    .build();
        } else {
            String errorMessage = "BuddyUser not found : %s.".formatted(username);
            logger.error(errorMessage);
            throw new UsernameNotFoundException(errorMessage);
        }
    }
}
