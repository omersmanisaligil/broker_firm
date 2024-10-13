package com.inghubs.broker_firm.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "customer")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "customer_name")
    @NotNull
    private String customerName;

    @OneToMany(
        fetch = FetchType.LAZY,
        orphanRemoval = false,
        mappedBy = "customer",
        cascade = CascadeType.ALL
    )
    private List<Order> orders;

    @OneToMany(
        fetch = FetchType.LAZY,
        orphanRemoval = false,
        mappedBy = "customer",
        cascade = CascadeType.ALL
    )
    private List<Asset> assets;

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

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
}
