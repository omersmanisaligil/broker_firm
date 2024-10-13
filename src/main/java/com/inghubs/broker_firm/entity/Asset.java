package com.inghubs.broker_firm.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Entity
@Table(name = "asset")
public class Asset {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "name")
    @NotNull
    private String name;

    @Column(name = "size")
    @NotNull
    private Double size;

    @Column(name = "usable_size")
    @NotNull
    private Double usableSize;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @NotNull
    private User user;

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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
