package com.quizz.app.utils;

public enum DefaultParticipantValues {
    VALID_EMAIL("valid-email"),
    MISSMATCH_EMAIL("missmatch-email"),
    NAME("name"),
    VALID_ID("1");

    private final String value;

    DefaultParticipantValues(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
