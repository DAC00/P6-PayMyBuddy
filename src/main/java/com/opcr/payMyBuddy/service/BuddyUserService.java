package com.opcr.payMyBuddy.service;

import com.opcr.payMyBuddy.exception.*;
import com.opcr.payMyBuddy.model.BuddyUser;
import com.opcr.payMyBuddy.repository.BuddyUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BuddyUserService {

    @Autowired
    private BuddyUserRepository buddyUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Add a new BuddyUser in the database. The email of the new BuddyUser must be unique or an exception is thrown.
     *
     * @param userName username of the new BuddyUser.
     * @param email    email of the new BuddyUser.
     * @param password password of the new BuddyUser.
     * @throws EmailAlreadyExistsException if the email is already used.
     */
    @Transactional
    public void addUser(String userName, String email, String password) throws EmailAlreadyExistsException {
        if (doesUserExist(email)) {
            throw new EmailAlreadyExistsException("Email already taken : %s.".formatted(email));
        } else {
            BuddyUser newBuddyUser = new BuddyUser();
            newBuddyUser.setUsername(userName);
            newBuddyUser.setEmail(email);
            newBuddyUser.setPassword(passwordEncoder.encode(password));
            buddyUserRepository.save(newBuddyUser);
        }
    }

    /**
     * Update the information of the BuddyUser. The input must not be null and the email must not be unique to the BuddyUser.
     * The buddyUser is updated if at least one parameter is different from before and not blank.
     *
     * @param userEmail       email the BuddyUser to update.
     * @param updatedUserName new username.
     * @param updatedEmail    new email, must not already exist in the database.
     * @param updatePassword  new password.
     * @throws EmailAlreadyExistsException    if the email is already used.
     * @throws BuddyUserDoesNotExistException if the BuddyUser doesn't exist.
     * @throws NoInfoToUpdateException        if no new information.
     */
    @Transactional
    public void updateUser(String userEmail, String updatedUserName, String updatedEmail, String updatePassword)
            throws EmailAlreadyExistsException, BuddyUserDoesNotExistException, NoInfoToUpdateException {
        BuddyUser buddyUser = buddyUserRepository.findByEmail(userEmail);
        if (buddyUser == null) {
            throw new BuddyUserDoesNotExistException("BuddyUser does not exist : %s.".formatted(userEmail));
        } else {
            if (doesUserExist(updatedEmail) && buddyUserRepository.findByEmail(updatedEmail).getId() != buddyUser.getId()) {
                throw new EmailAlreadyExistsException("Email already taken : %s.".formatted(updatedEmail));
            } else {
                if ((updatedUserName.equals(buddyUser.getUsername()) || updatedUserName.isBlank())
                        && (updatedEmail.equals(buddyUser.getEmail()) || updatedEmail.isBlank())
                        && (passwordEncoder.matches(updatePassword, buddyUser.getPassword()) || updatePassword.isBlank())) {
                    throw new NoInfoToUpdateException("No information to update for : %s.".formatted(userEmail));
                } else {
                    if (!updatedUserName.isBlank()) buddyUser.setUsername(updatedUserName);
                    if (!updatedEmail.isBlank()) buddyUser.setEmail(updatedEmail);
                    if (!updatePassword.isBlank()) buddyUser.setPassword(passwordEncoder.encode(updatePassword));
                    buddyUserRepository.save(buddyUser);
                }
            }
        }
    }

    /**
     * Create a connection between two Users. The BuddyUser who want to create a connection need the email of the BuddyUser who he wants to connect with.
     *
     * @param userEmail          email of the BuddyUser who create the connection.
     * @param emailToConnectWith email of the BuddyUser to connect with.
     * @throws BuddyUserDoesNotExistException         if one of the two BuddyUser does not exist.
     * @throws BuddyUserAlreadyConnectedWithException if the two BuddyUser are already connected.
     */
    @Transactional
    public void addConnectionToUser(String userEmail, String emailToConnectWith)
            throws BuddyUserDoesNotExistException, BuddyUserAlreadyConnectedWithException, BuddyUserConnectWithHimselfException {
        BuddyUser buddyUser = buddyUserRepository.findByEmail(userEmail);
        BuddyUser buddyUserToConnectWith = buddyUserRepository.findByEmail(emailToConnectWith);
        if (buddyUser == null) {
            throw new BuddyUserDoesNotExistException("BuddyUser does not exist : %s.".formatted(userEmail));
        } else if (buddyUserToConnectWith == null) {
            throw new BuddyUserDoesNotExistException("BuddyUser to connect with does not found : %s.".formatted(emailToConnectWith));
        } else if (userEmail.equals(emailToConnectWith)) {
            throw new BuddyUserConnectWithHimselfException("BuddyUser try to connect with himself : %s".formatted(userEmail));
        } else if (buddyUser.getBuddyUserConnections().stream().anyMatch(b -> b.getEmail().equals(buddyUserToConnectWith.getEmail()))) {
            throw new BuddyUserAlreadyConnectedWithException("BuddyUser already connected with : %s.".formatted(emailToConnectWith));
        } else {
            buddyUser.getBuddyUserConnections().add(buddyUserToConnectWith);
            buddyUserToConnectWith.getBuddyUserConnections().add(buddyUser);
            buddyUserRepository.save(buddyUser);
            buddyUserRepository.save(buddyUserToConnectWith);
        }
    }

    /**
     * Get the BuddyUser with email in param.
     *
     * @param email of the user.
     * @return a BuddyUser.
     */
    public BuddyUser getBuddyUser(String email) {
        return buddyUserRepository.findByEmail(email);
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
