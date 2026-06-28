package com.vintagecargarage.domain.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Request extends BaseEntity {
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestType type;
    private String description;

    @ManyToOne(optional = false)
    @JoinColumn(name = "storage_owner_id", nullable = false)
    private StorageOwner storageOwner;

    @ManyToOne
    @JoinColumn(name = "car_owner_id", insertable = false, updatable = false)
    private CarOwner carOwner;

    @OneToOne(mappedBy = "request")
    private Offer offers;

    Request(StorageOwner storageOwner, RequestType type, String description) {
        this.storageOwner = storageOwner;
        this.type = type;
        this.description = description;
    }
}
