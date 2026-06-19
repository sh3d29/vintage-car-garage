package com.vintagecargarage.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Offer extends BaseEntity {
    private String description;
    private float costs;
    private Boolean accepted;

    @ManyToOne
    private Request request;

    @ManyToOne
    private StorageOwner storageOwner;

    Offer(StorageOwner storageOwner, Request request, String description, float costs) {
        this.storageOwner = storageOwner;
        this.request = request;
        this.description = description;
        this.costs = costs;
        this.accepted = null;
    }

    /** CO_UC_E3_1: updateOfferStatus (Annahme) */
    public void accept() {
        this.accepted = true;
    }

    /** CO_UC_E3_1: updateOfferStatus (Ablehnung) */
    public void reject() {
        this.accepted = false;
    }
}
