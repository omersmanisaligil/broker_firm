package com.inghubs.broker_firm.dto;

import com.inghubs.broker_firm.enums.SIDE;
import com.inghubs.broker_firm.enums.STATUS;

import java.util.UUID;

public class OrderDTO {

    private UUID id;

    private String assetName;

    private SIDE orderSide;

    private Double size;

    private Double price;

    private STATUS status;

    private Long createdAt;

    private UUID userId;

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

    public void setUser(UUID userId) {
        this.userId = userId;
    }

    public Double getSize() {
        return size;
    }

    public void setSize(Double size) {
        this.size = size;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
