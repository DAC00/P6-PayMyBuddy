package com.opcr.payMyBuddy.service;

import com.opcr.payMyBuddy.exception.EmailAlreadyExistsException;
import com.opcr.payMyBuddy.exception.BuddyUserDoesNotExistException;
import com.opcr.payMyBuddy.model.BuddyUser;
import com.opcr.payMyBuddy.repository.BuddyUserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class BuddyUserServiceTest {

    @Autowired
    private BuddyUserRepository buddyUserRepository;

    @Autowired
    private BuddyUserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void addUserTest() throws Exception {
        userService.addUser("Test", "test@test.test", "test");
        BuddyUser buddyUserFromRepo = buddyUserRepository.findByUsername("Test");

        assertNotNull(buddyUserFromRepo);
        assertTrue(buddyUserFromRepo.getId() > 0);
        assertEquals("Test", buddyUserFromRepo.getUsername());
        assertEquals("test@test.test", buddyUserFromRepo.getEmail());
        assertTrue(passwordEncoder.matches("test", buddyUserFromRepo.getPassword()));

        buddyUserRepository.deleteById(buddyUserFromRepo.getId());
    }

    @Test
    public void addUserWithAlreadyUsedEmailTest() {
        String email = "dave@mail.com";
        EmailAlreadyExistsException exception = assertThrows(EmailAlreadyExistsException.class,
                () -> userService.addUser("Test", email, "test"));
        assertEquals("Email already taken : %s.".formatted(email), exception.getMessage());
    }

    @Test
    public void updateUserTest() throws Exception {
        BuddyUser buddyUserDave = buddyUserRepository.findByEmail("dave@mail.com");
        assertNotNull(buddyUserDave);

        userService.updateUser(buddyUserDave.getEmail(), buddyUserDave.getUsername(), "updated@mail.com", "update");
        BuddyUser buddyUserDaveUpdated = buddyUserRepository.findById(4).orElse(null);
        assertNotNull(buddyUserDaveUpdated);
        assertEquals(buddyUserDaveUpdated.getUsername(), buddyUserDave.getUsername());
        assertEquals(buddyUserDaveUpdated.getEmail(), "updated@mail.com");
        assertTrue(passwordEncoder.matches("update", buddyUserDaveUpdated.getPassword()));

        buddyUserRepository.save(buddyUserDave);
    }

    @Test
    public void updateUserWhenUserDoesNotExistTest() {
        String email = "test@test.test";
        BuddyUserDoesNotExistException exception = assertThrows(BuddyUserDoesNotExistException.class,
                () -> userService.updateUser(email, "Test", "test@test.test", "test"));
        assertEquals("BuddyUser does not exist : %s.".formatted(email), exception.getMessage());
    }

    @Test
    public void updateUserWithAlreadyUsedEmailTest() {
        String emailUpdated = "bob@mail.com";
        EmailAlreadyExistsException exception = assertThrows(EmailAlreadyExistsException.class,
                () -> userService.updateUser("dave@mail.com", "Test", emailUpdated, "test"));
        assertEquals("Email already taken : %s.".formatted(emailUpdated), exception.getMessage());
    }

    @Test
    public void addConnectionToUserTest() throws Exception {
        userService.addUser("Test", "test@test.test", "test");
        BuddyUser buddyUserOne = buddyUserRepository.findByUsername("Test");

        userService.addConnectionToUser(buddyUserOne.getEmail(), "dave@mail.com");

        BuddyUser buddyUserOneUpdated = buddyUserRepository.findByUsername("Test");
        BuddyUser buddyUserTwoUpdated = buddyUserRepository.findByUsername("dave");

        assertTrue(buddyUserOneUpdated.getBuddyUserConnections().stream().anyMatch(user -> user.getEmail().equals(buddyUserTwoUpdated.getEmail())));
        assertTrue(buddyUserTwoUpdated.getBuddyUserConnections().stream().anyMatch(user -> user.getEmail().equals(buddyUserOneUpdated.getEmail())));

        buddyUserRepository.delete(buddyUserOne);
    }

    @Test
    public void addConnectionToUserWhenUserDoesNotExistTest() {
        String email = "test@test.test";
        BuddyUserDoesNotExistException exception = assertThrows(BuddyUserDoesNotExistException.class,
                () -> userService.addConnectionToUser(email, "dave@mail.com"));
        assertEquals("BuddyUser does not exist : %s.".formatted(email), exception.getMessage());
    }

    @Test
    public void addConnectionToUserWhenUserToConnectNotFoundTest() {
        String emailToConnect = "test@test.test";
        BuddyUserDoesNotExistException exception = assertThrows(BuddyUserDoesNotExistException.class,
                () -> userService.addConnectionToUser("dave@mail.com", emailToConnect));
        assertEquals("BuddyUser to connect with does not found : %s.".formatted(emailToConnect), exception.getMessage());
    }
}
