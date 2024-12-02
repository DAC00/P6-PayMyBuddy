package com.opcr.payMyBuddy.controller;

import com.opcr.payMyBuddy.model.BuddyUser;
import com.opcr.payMyBuddy.service.BuddyUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class ProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BuddyUserService buddyUserService;

    @Test
    @WithMockUser(username = "alice@mail.com")
    @Transactional
    public void updateProfileTest() throws Exception {
        String emailAlice = "alice@mail.com";
        String newUsername = "TEST";

        this.mockMvc.perform(post("/profile")
                        .param("email", "")
                        .param("password", "")
                        .param("username", newUsername)
                        .with(csrf()))
                .andExpect(status().isOk());

        BuddyUser buddyAlice = buddyUserService.getBuddyUser(emailAlice);

        assertNotNull(buddyAlice);
        assertEquals(buddyAlice.getUsername(), newUsername);
        assertEquals(buddyAlice.getEmail(), emailAlice);
    }
}
