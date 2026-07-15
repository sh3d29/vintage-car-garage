package com.vintagecargarage.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Angebot eines StorageOwner auf eine Request.
 * Trägt Konditionen + OfferStatus (PENDING -> ACCEPTED/REJECTED); strikte
 * 1:1-Beziehung zur Request, die der Konstruktor beidseitig herstellt.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Offer extends BaseEntity {

    private String description;

    private float costs;

    @Enumerated(EnumType.STRING)
    private OfferStatus status;

    @ManyToOne(optional = false)
    @JoinColumn(name = "storage_owner_id", nullable = false)
    private StorageOwner storageOwner;

    @OneToOne(optional = false)
    @JoinColumn(name = "request_id", nullable = false, unique = true)
    private Request request;

    public Offer(StorageOwner storageOwner, Request request, String description, float costs) {
        this.storageOwner = storageOwner;
        this.request = request;
        this.description = description;
        this.status = OfferStatus.PENDING;
        this.costs = costs;
        request.addOffer(this);
    }

    /** Angebot angenommen */
    public void accept() {
        this.status = OfferStatus.ACCEPTED;
    }

    /** Angebot abgelehnt */
    public void reject() {
        this.status = OfferStatus.REJECTED;
    }
}