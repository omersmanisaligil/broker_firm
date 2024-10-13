package com.inghubs.broker_firm.entity;

import com.inghubs.broker_firm.enums.ROLE;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "username", updatable = false)
    @NotNull
    private String username;

    @Column(name = "password", updatable = false)
    @NotNull
    private String password;

    @OneToMany(
        fetch = FetchType.LAZY,
        orphanRemoval = false,
        mappedBy = "user",
        cascade = CascadeType.ALL
    )
    private List<Order> orders;

    @OneToMany(
        fetch = FetchType.LAZY,
        orphanRemoval = false,
        mappedBy = "user",
        cascade = CascadeType.ALL
    )
    private List<Asset> assets;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    @NotNull
    private ROLE role;

    public List<Asset> getAssets() {
        return assets;
    }

    public void setAssets(List<Asset> assets) {
        this.assets = assets;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

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
