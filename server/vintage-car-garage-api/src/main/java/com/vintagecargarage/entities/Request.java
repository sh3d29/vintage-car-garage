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
public class Request extends BaseEntity {
    private String description;

    @ManyToOne
    private CarOwner carOwner;

    @ManyToOne
    private StorageOwner storageOwner;

    Request(CarOwner carOwner, StorageOwner storageOwner, String description) {
        this.carOwner = carOwner;
        this.storageOwner = storageOwner;
        this.description = description;
    }
}
