package com.inghubs.broker_firm.dto;

import java.util.UUID;

public class AssetDTO {

    private UUID id;

    private String name;

    private Double size;

    private Double usableSize;

    private UUID userId;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getSize() {
        return size;
    }

    public void setSize(Double size) {
        this.size = size;
    }

    public Double getUsableSize() {
        return usableSize;
    }

    public void setUsableSize(Double usableSize) {
        this.usableSize = usableSize;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }
}
