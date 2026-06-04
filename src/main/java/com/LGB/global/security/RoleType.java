package com.LGB.global.security;

public enum RoleType {

    GUEST,
    USER,
    MANAGER,
    ADMIN;

    public String asAuthority() {
        return "ROLE_" + name();
    }

    public boolean isAdmin() {
        return this == ADMIN;
    }

    public boolean isManagerOrAdmin() {
        return this == MANAGER || this == ADMIN;
    }

    public boolean isUserOrHigher() {
        return this == USER || this == MANAGER || this == ADMIN;
    }
}