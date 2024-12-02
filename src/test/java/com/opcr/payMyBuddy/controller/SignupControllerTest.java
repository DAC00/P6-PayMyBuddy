package com.opcr.payMyBuddy.controller;

import com.opcr.payMyBuddy.model.BuddyUser;
import com.opcr.payMyBuddy.service.BuddyUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class SignupControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BuddyUserService buddyUserService;

    @Test
    @Transactional
    public void signupTest() throws Exception {
        String buddyUsername = "test";
        String buddyEmail = "test@test.test";

        this.mockMvc.perform(post("/signup")
                        .param("username", buddyUsername)
                        .param("email", buddyEmail)
                        .param("password", "pass")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());
        BuddyUser user = buddyUserService.getBuddyUser(buddyEmail);

        assertNotNull(user);
        assertEquals(user.getEmail(), buddyEmail);
        assertEquals(user.getUsername(), buddyUsername);
    }
}
