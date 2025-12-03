package com.quizz.app.utils;

public enum DefaultUserValues {
    FIRST_NAME("firstName"),
    LAST_NAME("lastName"),
    EMAIL("email"),
    PASSWORD("password");

    private final String value;

    DefaultUserValues(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
