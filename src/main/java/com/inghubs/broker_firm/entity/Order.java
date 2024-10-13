package com.inghubs.broker_firm.entity;

import com.inghubs.broker_firm.enums.SIDE;
import com.inghubs.broker_firm.enums.STATUS;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "asset_name")
    @NotNull
    private String assetName;

    @Column(name = "order_side")
    @Enumerated(EnumType.STRING)
    @NotNull
    private SIDE orderSide;

    @Column(name = "size")
    @NotNull
    private Double size;

    @Column(name = "price")
    @NotNull
    private Double price;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    @NotNull
    private STATUS status;

    @Column(name = "created_at")
    private Long createdAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @NotNull
    private User user;

    @PrePersist
    public void preSave(){
        status = STATUS.PENDING;
        createdAt = System.currentTimeMillis();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public SIDE getOrderSide() {
        return orderSide;
    }

    public void setOrderSide(SIDE orderSide) {
        this.orderSide = orderSide;
    }

    public Double getSize() {
        return size;
    }

    public void setSize(Double size) {
        this.size = size;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public STATUS getStatus() {
        return status;
    }

    public void setStatus(STATUS status) {
        this.status = status;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
