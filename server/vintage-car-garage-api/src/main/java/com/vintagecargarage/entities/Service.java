package com.vintagecargarage.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Service eines StorageOwner. isOptional -> vom CarOwner
 * je Car hinzubuchbar, sonst Teil des Standard-Lagerangebots.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Service extends BaseEntity {
    private String title;
    private String description;
    private float price;
    private boolean isOptional = false;

    @ManyToOne(optional = false)
    @JoinColumn(name = "storage_owner_id", nullable = false)
    private StorageOwner storageOwner;

    public Service(StorageOwner storageOwner, String title, String description, float price, boolean isOptional) {
        this.storageOwner = storageOwner;
        this.title = title;
        this.description = description;
        this.price = price;
        this.isOptional = isOptional;
    }

    /** aktualisiert die Servicedetails (null-Felder bleiben unverändert) */
    public void updateDetails(String title, String description, Float price, Boolean optional) {
        if (title != null)
            this.title = title;
        if (description != null)
            this.description = description;
        if (price != null)
            this.price = price;
        if (optional != null)
            this.isOptional = optional;
    }
}
