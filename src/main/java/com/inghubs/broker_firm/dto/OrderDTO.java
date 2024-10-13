package com.inghubs.broker_firm.dto;

import com.inghubs.broker_firm.enums.SIDE;
import com.inghubs.broker_firm.enums.STATUS;

import java.util.UUID;

public class OrderDTO {

    private UUID id;

    private String assetName;

    private SIDE orderSide;

    private STATUS status;

    private Long createdAt;

    private UUID customerId;

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

    public UUID getCustomerID() {
        return customerId;
    }

    public void setCustomer(UUID customerId) {
        this.customerId = customerId;
    }
}
