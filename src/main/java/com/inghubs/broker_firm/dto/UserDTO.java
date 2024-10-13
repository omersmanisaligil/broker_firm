package com.inghubs.broker_firm.dto;

import com.inghubs.broker_firm.enums.ROLE;

import java.util.UUID;

public class UserDTO {

    private UUID id;

    private String username;

    private ROLE role;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public ROLE getRole() {
        return role;
    }

    public void setRole(ROLE role) {
        this.role = role;
    }
}
