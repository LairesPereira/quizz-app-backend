package com.quizz.app.errors;

public class ForbiddenException extends  RuntimeException {
    public ForbiddenException(String message) {
        super(message);
    }
}
