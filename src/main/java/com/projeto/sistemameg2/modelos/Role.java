package com.projeto.sistemameg2.modelos;

public enum Role {
    ADMIN,
    USER,
    GUEST;

    public String getAuthority() {
        return "ROLE_" + name();
    }
}
