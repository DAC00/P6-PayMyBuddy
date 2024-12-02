package com.opcr.payMyBuddy.controller;

import com.opcr.payMyBuddy.model.BuddyUser;
import com.opcr.payMyBuddy.repository.BuddyUserRepository;
import com.opcr.payMyBuddy.service.BuddyUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AddConnectionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BuddyUserService userService;

    @Autowired
    private BuddyUserRepository buddyUserRepository;

    @Test
    @WithMockUser(username = "dave@mail.com")
    @Transactional
    public void addConnectionBetweenUsersTest() throws Exception {

        this.mockMvc.perform(post("/add_connection")
                        .param("email", "bob@mail.com")
                        .with(csrf()))
                .andExpect(status().isOk());

        BuddyUser buddyUserOneUpdated = buddyUserRepository.findByUsername("dave");
        BuddyUser buddyUserTwoUpdated = buddyUserRepository.findByUsername("bob");

        assertTrue(buddyUserOneUpdated.getBuddyUserConnections().stream().anyMatch(user -> user.getEmail().equals(buddyUserTwoUpdated.getEmail())));
        assertTrue(buddyUserTwoUpdated.getBuddyUserConnections().stream().anyMatch(user -> user.getEmail().equals(buddyUserOneUpdated.getEmail())));
    }
}
