package com.technovision.civilization.exceptions;

public class AlreadyRegisteredException extends Exception {

    private static final long serialVersionUID = -5711194812233792374L;

    public AlreadyRegisteredException(String message) {
        super(message);
    }

}
