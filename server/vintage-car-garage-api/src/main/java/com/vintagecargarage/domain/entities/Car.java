package com.vintagecargarage.domain.entities;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Car extends BaseEntity {
    @Column(unique = true)
    private String licensePlate;
    private boolean readyToDrive;
    private boolean isMaintained;

    @ManyToMany
    @JoinTable(name = "car_booked_services", joinColumns = @JoinColumn(name = "car_id"), inverseJoinColumns = @JoinColumn(name = "service_id"))
    private Set<Service> bookedServices = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "current_storage_owner_id")
    private StorageOwner currentStorage;

    Car(String licensePlate) {
        this.licensePlate = licensePlate;
        this.readyToDrive = true;
        this.isMaintained = false;
        this.currentStorage = null;
    }

    public void bookOptionalService(Service service) {
        this.bookedServices.add(service);
    }

    public void assignCarToStorage(StorageOwner storageOwner) {
        this.currentStorage = storageOwner;
    }
}
