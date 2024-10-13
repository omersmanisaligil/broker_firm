package com.inghubs.broker_firm.dto;

import com.inghubs.broker_firm.enums.ROLE;

import java.util.List;
import java.util.UUID;

public class UserDTO {

    private UUID id;

    private String username;

    private List<OrderDTO> orders;

    private List<AssetDTO> assets;

    private ROLE role;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public List<AssetDTO> getAssets() {
        return assets;
    }

    public void setAssets(List<AssetDTO> assets) {
        this.assets = assets;
    }

    public List<OrderDTO> getOrders() {
        return orders;
    }

    public void setOrders(List<OrderDTO> orders) {
        this.orders = orders;
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
