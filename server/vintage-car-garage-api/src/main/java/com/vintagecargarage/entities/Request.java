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

    public void addOffer(Offer offer) {
        this.offer = offer;
    }

    public void reopen() {
        this.offer = null;
    }
}