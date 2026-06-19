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
public class Service extends BaseEntity {
    private String title;
    private String description;
    private float price;
    private boolean isOptional;

    @ManyToOne
    private StorageOwner storageOwner;

    Service(StorageOwner storageOwner, String title, String description, float price, boolean isOptional) {
        this.storageOwner = storageOwner;
        this.title = title;
        this.description = description;
        this.price = price;
        this.isOptional = isOptional;
    }

    /** CO_UC_L1_2: updateService */
    public void updateDetails(String title, String description, float price, boolean isOptional) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.isOptional = isOptional;
    }
}
