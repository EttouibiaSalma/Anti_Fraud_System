package com.example.anti_fraud_system.Enum;

public enum Roles {
    MERCHANT("ROLE_MERCHANT"),
    ADMINISTRATOR("ROLE_ADMINISTRATOR"),
    SUPPORT("ROLE_SUPPORT");

    private final String role;

    Roles(String role) {
        this.role = role;
    }
    @Override
    public String toString() {
        return this.role;
    }
}
