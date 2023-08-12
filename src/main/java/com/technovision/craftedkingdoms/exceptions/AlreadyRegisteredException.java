package com.technovision.craftedkingdoms.exceptions;

import java.io.Serial;

public class AlreadyRegisteredException extends Exception {

    @Serial
    private static final long serialVersionUID = -5711194812233792374L;

    public AlreadyRegisteredException(String message) {
        super(message);
    }

}
