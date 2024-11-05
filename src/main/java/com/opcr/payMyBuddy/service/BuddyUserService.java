package com.opcr.payMyBuddy.service;

import com.opcr.payMyBuddy.exception.BuddyUserDoesNotExistException;
import com.opcr.payMyBuddy.exception.EmailAlreadyExistsException;
import com.opcr.payMyBuddy.model.BuddyUser;
import com.opcr.payMyBuddy.repository.BuddyUserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class BuddyUserService {

    @Autowired
    private BuddyUserRepository buddyUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final Logger logger = LogManager.getLogger(BuddyUserService.class);

    /**
     * Add a new BuddyUser in the database. The email of the new BuddyUser must be unique or an exception is thrown.
     *
     * @param userName username of the new BuddyUser.
     * @param email    email of the new BuddyUser.
     * @param password password of the new BuddyUser.
     * @throws EmailAlreadyExistsException if the email is already used.
     */
    public void addUser(String userName, String email, String password) throws EmailAlreadyExistsException {
        if (doesUserExist(email)) {
            String errorMessage = "Email already taken : %s.".formatted(email);
            logger.error(errorMessage);
            throw new EmailAlreadyExistsException(errorMessage);
        } else {
            BuddyUser newBuddyUser = new BuddyUser();
            newBuddyUser.setUsername(userName);
            newBuddyUser.setEmail(email);
            newBuddyUser.setPassword(passwordEncoder.encode(password));

            buddyUserRepository.save(newBuddyUser);
            logger.info("BuddyUser added : %s.".formatted(newBuddyUser.toString()));
        }
    }

    /**
     * Update the information of the BuddyUser. The input must not be null and the email must not be unique to the BuddyUser.
     *
     * @param userEmail       email the BuddyUser to update.
     * @param updatedUserName new username.
     * @param updatedEmail    new email, must not already exist in the database.
     * @param UpdatePassword  new password.
     * @throws EmailAlreadyExistsException    if the email is already used.
     * @throws BuddyUserDoesNotExistException if the BuddyUser doesn't exist.
     */
    public void updateUser(String userEmail, String updatedUserName, String updatedEmail, String UpdatePassword) throws EmailAlreadyExistsException, BuddyUserDoesNotExistException {
        BuddyUser buddyUser = buddyUserRepository.findByEmail(userEmail);
        if (buddyUser == null) {
            String errorMessage = "BuddyUser does not exist : %s.".formatted(userEmail);
            logger.error(errorMessage);
            throw new BuddyUserDoesNotExistException(errorMessage);
        } else {
            if (doesUserExist(updatedEmail) && buddyUserRepository.findByEmail(updatedEmail).getId() != buddyUser.getId()) {
                String errorMessage = "Email already taken : %s.".formatted(updatedEmail);
                logger.error(errorMessage);
                throw new EmailAlreadyExistsException(errorMessage);
            } else {
                buddyUser.setUsername(updatedUserName);
                buddyUser.setEmail(updatedEmail);
                buddyUser.setPassword(passwordEncoder.encode(UpdatePassword));
                buddyUserRepository.save(buddyUser);
            }
        }
    }

    /**
     * Create a connection between two Users. The BuddyUser who want to create a connection need the email of the BuddyUser who he wants to connect with.
     *
     * @param userEmail          email of the BuddyUser who create the connection.
     * @param emailToConnectWith email of the BuddyUser to connect with.
     * @throws BuddyUserDoesNotExistException if one of the two BuddyUser does not exist.
     */
    public void addConnectionToUser(String userEmail, String emailToConnectWith) throws BuddyUserDoesNotExistException {
        BuddyUser buddyUser = buddyUserRepository.findByEmail(userEmail);
        BuddyUser buddyUserToConnectWith = buddyUserRepository.findByEmail(emailToConnectWith);
        if (buddyUser == null) {
            String errorMessage = "BuddyUser does not exist : %s.".formatted(userEmail);
            logger.error(errorMessage);
            throw new BuddyUserDoesNotExistException(errorMessage);
        } else if (buddyUserToConnectWith == null) {
            String errorMessage = "BuddyUser to connect with does not found : %s.".formatted(emailToConnectWith);
            logger.error(errorMessage);
            throw new BuddyUserDoesNotExistException(errorMessage);
        } else {
            buddyUser.getBuddyUserConnections().add(buddyUserToConnectWith);
            buddyUserToConnectWith.getBuddyUserConnections().add(buddyUser);
            buddyUserRepository.save(buddyUser);
            buddyUserRepository.save(buddyUserToConnectWith);
            logger.info("Connection between : %s && %s added.".formatted(buddyUser.getId(), buddyUserToConnectWith.getId()));
        }
    }

    /**
     * Check if a BuddyUser with that email exist in the database.
     *
     * @param email of the potential BuddyUser.
     * @return true if the BuddyUser exist otherwise false.
     */
    private boolean doesUserExist(String email) {
        return buddyUserRepository.findByEmail(email) != null;
    }

}
