package com.quizz.app.enums;

public enum Roles {
    TEACHER("teacher"),
    ADM("adm"),
    USER("user");

    private String role;

    Roles(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }
}
