package com.opcr.payMyBuddy.exception;

public class UserDoesNotExistException extends Exception {
    public UserDoesNotExistException(String exceptionMessage) {
        super(exceptionMessage);
    }
}
