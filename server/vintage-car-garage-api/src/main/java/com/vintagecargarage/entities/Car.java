package com.vintagecargarage.entities;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "car_owner_id", "licensePlate" }))
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Car extends BaseEntity {
    private String licensePlate;
    private boolean readyToDrive;
    private boolean isMaintained;

    @ManyToMany
    @JoinTable(name = "car_booked_services", joinColumns = @JoinColumn(name = "car_id"), inverseJoinColumns = @JoinColumn(name = "service_id"))
    private Set<Service> bookedServices = new HashSet<>();

    @ManyToOne(optional = false)
    @JoinColumn(name = "car_owner_id", nullable = false)
    private CarOwner carOwner;

    @ManyToOne
    @JoinColumn(name = "current_storage_owner_id")
    private StorageOwner currentStorage;

    public Car(CarOwner carOwner, String licensePlate) {
        this.carOwner = carOwner;
        this.licensePlate = licensePlate;
        this.readyToDrive = true;
        this.isMaintained = false;
        this.currentStorage = null;
    }

    public void updateStatus(boolean readyToDrive, boolean maintained) {
        this.readyToDrive = readyToDrive;
        this.isMaintained = maintained;
    }

    public void relocateTo(StorageOwner newStorageOwner) {
        if (this.currentStorage != null)
            this.currentStorage.removeCarFromStorage(this);
        if (newStorageOwner != null)
            newStorageOwner.addCarToStorage(this);
    }

    public void changeLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public void setCurrentStorage(StorageOwner currentStorage) {
        this.currentStorage = currentStorage;
    }

    public void addBookedOptionalService(Service service) {
        this.bookedServices.add(service);
    }

    public void removeBookedOptionalService(Service service) {
        this.bookedServices.remove(service);
    }
}
