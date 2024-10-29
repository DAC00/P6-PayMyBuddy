package com.opcr.payMyBuddy.service;

import com.opcr.payMyBuddy.exception.EmailAlreadyExistsException;
import com.opcr.payMyBuddy.exception.UserDoesNotExistException;
import com.opcr.payMyBuddy.model.User;
import com.opcr.payMyBuddy.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private static final Logger logger = LogManager.getLogger(UserService.class);

    public Iterable<User> getUsers() {
        return userRepository.findAll();
    }

    /**
     * @param userName
     * @param password
     * @return
     */
    public boolean logIn(String userName, String password) {
        User userToLog = userRepository.findByUsername(userName);
        if (userToLog != null) {
            // pass w/ bcrypt
            if (userToLog.getPassword().equals(password)) {
                return true;
            } else {
                logger.debug("Wrong password for user : %s.".formatted(userName));
                // exception
            }
        } else {
            logger.debug("Username does not exist : %s.".formatted(userName));
            // exception
        }

        return userRepository.findByUsername(userName).getPassword().equals(password);
    }

    /**
     * Add a new User in the database. The email of the new User must be unique or an exception is thrown.
     *
     * @param userName username of the new User.
     * @param email email of the new User.
     * @param password password of the new User.
     * @throws EmailAlreadyExistsException if the email is already used.
     */
    public void addUser(String userName, String email, String password) throws EmailAlreadyExistsException {
        if (doesUserExist(email)) {
            String errorMessage = "Email already taken : %s.".formatted(email);
            logger.error(errorMessage);
            throw new EmailAlreadyExistsException(errorMessage);
        } else {
            User newUser = new User();
            newUser.setUsername(userName);
            newUser.setEmail(email);

            // pass w/ bcrypt
            newUser.setPassword(password);

            userRepository.save(newUser);
            logger.info("User added : %s.".formatted(newUser.toString()));
        }
    }

    /**
     * Update the information of the User. The input must not be null and the email must not be unique to the User.
     *
     * @param userId   id of the User to update.
     * @param userName new username.
     * @param email    new email, must not already exist in the database.
     * @param password new password.
     * @throws EmailAlreadyExistsException if the email is already used.
     * @throws UserDoesNotExistException   if the User doesn't exist.
     */
    public void updateUser(Integer userId, String userName, String email, String password) throws EmailAlreadyExistsException, UserDoesNotExistException {
        if (!userRepository.existsById(userId)) {
            String errorMessage = "User does not exist : %s.".formatted(userId);
            logger.error(errorMessage);
            throw new UserDoesNotExistException(errorMessage);
        } else {
            if (doesUserExist(email) && userRepository.findByEmail(email).getId() != userId) {
                String errorMessage = "Email already taken : %s.".formatted(email);
                logger.error(errorMessage);
                throw new EmailAlreadyExistsException(errorMessage);
            } else {
                User userUpdated = new User();
                userUpdated.setId(userId);
                userUpdated.setUsername(userName);
                userUpdated.setEmail(email);
                // pass
                userUpdated.setPassword(password);
                userRepository.save(userUpdated);
            }
        }
    }

    /**
     * Create a connection between two Users. The User who want to create a connection need the email of the User who he wants to connect with.
     *
     * @param userId             id of the User who create the connection.
     * @param emailToConnectWith email of the User to connect with.
     * @throws UserDoesNotExistException if one of the two User does not exist.
     */
    public void addConnectionToUser(Integer userId, String emailToConnectWith) throws UserDoesNotExistException {
        User user = userRepository.findById(userId).orElse(null);
        User userToConnectWith = userRepository.findByEmail(emailToConnectWith);
        if (user == null) {
            String errorMessage = "User does not exist : %s.".formatted(userId);
            logger.error(errorMessage);
            throw new UserDoesNotExistException(errorMessage);
        } else if (userToConnectWith == null) {
            String errorMessage = "User to connect with does not found : %s.".formatted(emailToConnectWith);
            logger.error(errorMessage);
            throw new UserDoesNotExistException(errorMessage);
        } else {
            user.getUserConnections().add(userToConnectWith);
            userToConnectWith.getUserConnections().add(user);
            userRepository.save(user);
            userRepository.save(userToConnectWith);
            logger.info("Connection between : %s && %s added.".formatted(user.getId(), userToConnectWith.getId()));
        }
    }

    /**
     * Check if a User with that email exist in the database.
     *
     * @param email of the potential User.
     * @return true if the User exist otherwise false.
     */
    private boolean doesUserExist(String email) {
        return userRepository.findByEmail(email) != null;
    }

}
