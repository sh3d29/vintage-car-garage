package com.vintagecargarage.domain.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Offer extends BaseEntity {
    private String description;
    private float costs;
    private Boolean accepted;

    @ManyToOne(optional = false)
    @JoinColumn(name = "storage_owner_id", nullable = false)
    private StorageOwner storageOwner;

    @ManyToOne(optional = false)
    @JoinColumn(name = "request_id", nullable = false)
    private Request request;

    Offer(StorageOwner storageOwner, Request request, String description, float costs) {
        this.storageOwner = storageOwner;
        this.request = request;
        this.description = description;
        this.costs = costs;
        this.accepted = null;
    }
}