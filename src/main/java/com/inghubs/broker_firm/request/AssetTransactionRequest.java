package com.inghubs.broker_firm.request;

import jakarta.validation.constraints.NotNull;

public class AssetTransactionRequest {

    @NotNull
    private Double amount;

    @NotNull
    private String assetName;

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }
}
