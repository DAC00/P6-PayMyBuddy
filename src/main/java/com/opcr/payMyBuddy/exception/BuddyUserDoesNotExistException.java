package com.opcr.payMyBuddy.exception;

public class BuddyUserDoesNotExistException extends Exception {
    public BuddyUserDoesNotExistException(String exceptionMessage) {
        super(exceptionMessage);
    }
}
