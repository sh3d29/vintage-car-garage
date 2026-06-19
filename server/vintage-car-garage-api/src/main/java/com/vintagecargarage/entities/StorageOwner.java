package com.vintagecargarage.entities;

import java.time.LocalTime;
import java.util.List;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AttributeOverride(name = "email", column = @Column(unique = true))
public class StorageOwner extends User {
    private String companyName;
    private float costs;
    private LocalTime serviceTime;
    @Embedded
    private Location location;
    @Embedded
    private Storage storage;
    @ElementCollection
    private List<String> carManufacturers;

    public StorageOwner(String email, String password, int passwordVersion, String companyName, float costs,
            LocalTime serviceTime,
            Location location, Storage storage, List<String> carManufacturers) {
        super(email, password, passwordVersion);
        this.companyName = companyName;
        this.costs = costs;
        this.serviceTime = serviceTime;
        this.location = location;
        this.storage = storage;
        this.carManufacturers = carManufacturers;
    }

    /**
     * Creator (CO_UC_L1_1: createService): der Lagerhalter aggregiert seine
     * Services.
     */
    public Service createService(String title, String description, float price, boolean isOptional) {
        return new Service(this, title, description, price, isOptional);
    }

    /**
     * Creator (CO_UC_L7_1: createOffer): der Lagerhalter erstellt ein Angebot zu
     * einer Anfrage.
     */
    public Offer createOffer(Request request, String description, float costs) {
        return new Offer(this, request, description, costs);
    }
}
