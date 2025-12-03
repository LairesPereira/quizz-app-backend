package com.quizz.app.utils;

public enum DefaultQuizzValues {
    INVALID_SLUG("invalid-slug"),
    VALID_SLUG("valid-slug"),
    TITLE("title"),
    DESCRIPTION("description");

    private String value;

    DefaultQuizzValues(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
