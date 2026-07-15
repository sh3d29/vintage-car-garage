package com.vintagecargarage.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
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
 * Stellplatzanfrage von einem CarOwner an einen
 * StorageOwner. Höchstens ein Offer; gilt als "offen", solange offer == null
 * (kein eigenes Status-Feld). Offer gehört lebenszyklisch zur Request (cascade
 * = ALL).
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Request extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestType type;

    private String description;

    @ManyToOne(optional = false)
    @JoinColumn(name = "storage_owner_id", nullable = false)
    private StorageOwner storageOwner;

    @ManyToOne(optional = false)
    @JoinColumn(name = "car_owner_id", nullable = false)
    private CarOwner carOwner;

    @OneToOne(mappedBy = "request", cascade = CascadeType.ALL)
    private Offer offer;

    public Request(CarOwner carOwner, StorageOwner storageOwner, String description) {
        this.carOwner = carOwner;
        this.storageOwner = storageOwner;
        this.type = RequestType.STELLPLATZ;
        this.description = description;
    }

    /** verknüpft das Offer mit dieser Request */
    public void addOffer(Offer offer) {
        this.offer = offer;
    }

    /** gibt die Request wieder frei (offer = null) -> gilt danach als offen */
    public void reopen() {
        this.offer = null;
    }
}