package com.inghubs.broker_firm.request;

import com.inghubs.broker_firm.enums.ROLE;

public class CreateUserRequest {

    private String username;

    private String password;

    private ROLE role;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ROLE getRole() {
        return role;
    }

    public void setRole(ROLE role) {
        this.role = role;
    }
}
