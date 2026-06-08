package com.LGB.global.security;

public enum RoleType {

    STUDENT,
    ADMIN;

    public String asAuthority() {
        return "ROLE_" + name();
    }

    public boolean isAdmin() {
        return this == ADMIN;
    }
}
