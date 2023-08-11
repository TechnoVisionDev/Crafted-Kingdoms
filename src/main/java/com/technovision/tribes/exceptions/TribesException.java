package com.technovision.tribes.exceptions;

import java.io.Serial;

public class TribesException extends Exception {

    @Serial
    private static final long serialVersionUID = 4124107018314337603L;

    public TribesException(String message) {
        super(message);
    }
}