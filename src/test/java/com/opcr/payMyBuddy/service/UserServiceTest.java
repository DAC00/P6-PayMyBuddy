package com.opcr.payMyBuddy.service;

import com.opcr.payMyBuddy.exception.EmailAlreadyExistsException;
import com.opcr.payMyBuddy.exception.UserDoesNotExistException;
import com.opcr.payMyBuddy.model.User;
import com.opcr.payMyBuddy.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Test
    public void addUserTest() throws Exception {
        userService.addUser("Test", "test@test.test", "test");
        User userFromRepo = userRepository.findByUsername("Test");

        assertNotNull(userFromRepo);
        assertTrue(userFromRepo.getId() > 0);
        assertEquals("Test", userFromRepo.getUsername());
        assertEquals("test@test.test", userFromRepo.getEmail());
        assertEquals("test", userFromRepo.getPassword());

        userRepository.deleteById(userFromRepo.getId());
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
        User userDave = userRepository.findById(4).orElse(null);
        assertNotNull(userDave);

        userService.updateUser(userDave.getId(), userDave.getUsername(), "updated@mail.com", "update");
        User userDaveUpdated = userRepository.findById(4).orElse(null);
        assertNotNull(userDaveUpdated);
        assertEquals(userDaveUpdated.getUsername(), userDave.getUsername());
        assertEquals(userDaveUpdated.getEmail(), "updated@mail.com");
        assertEquals(userDaveUpdated.getPassword(), "update");

        userRepository.save(userDave);
    }

    @Test
    public void updateUserWhenUserDoesNotExistTest() {
        Integer userId = 99;
        UserDoesNotExistException exception = assertThrows(UserDoesNotExistException.class,
                () -> userService.updateUser(userId, "Test", "test@test.test", "test"));
        assertEquals("User does not exist : %s.".formatted(userId), exception.getMessage());
    }

    @Test
    public void updateUserWithAlreadyUsedEmailTest() {
        String email = "dave@mail.com";
        EmailAlreadyExistsException exception = assertThrows(EmailAlreadyExistsException.class,
                () -> userService.updateUser(1, "Test", email, "test"));
        assertEquals("Email already taken : %s.".formatted(email), exception.getMessage());
    }

    @Test
    public void addConnectionToUserTest() throws Exception {
        userService.addUser("Test", "test@test.test", "test");
        User userOne = userRepository.findByUsername("Test");
        String emailOfUserTwo = "dave@mail.com";

        userService.addConnectionToUser(userOne.getId(), emailOfUserTwo);

        User userOneUpdated = userRepository.findByUsername("Test");
        User userTwoUpdated = userRepository.findByUsername("dave");

        assertTrue(userOneUpdated.getUserConnections().stream().anyMatch(user -> user.getEmail().equals(userTwoUpdated.getEmail())));
        assertTrue(userTwoUpdated.getUserConnections().stream().anyMatch(user -> user.getEmail().equals(userOneUpdated.getEmail())));

        userRepository.delete(userOne);
    }

    @Test
    public void addConnectionToUserWhenUserDoesNotExistTest() {
        Integer userId = 99;
        UserDoesNotExistException exception = assertThrows(UserDoesNotExistException.class,
                () -> userService.addConnectionToUser(userId, "dave@mail.com"));
        assertEquals("User does not exist : %s.".formatted(userId), exception.getMessage());
    }

    @Test
    public void addConnectionToUserWhenUserToConnectNotFoundTest() {
        String emailToConnect = "test@test.test";
        UserDoesNotExistException exception = assertThrows(UserDoesNotExistException.class,
                () -> userService.addConnectionToUser(4, emailToConnect));
        assertEquals("User to connect with does not found : %s.".formatted(emailToConnect), exception.getMessage());
    }
}
