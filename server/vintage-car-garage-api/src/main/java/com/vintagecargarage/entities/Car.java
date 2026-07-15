package com.vintagecargarage.entities;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Fahrzeug: gehört zu einem CarOwner, optional bei einem
 * StorageOwner eingelagert; Information Expert für den eigenen Zustand und die
 * Fahrzeugumlagerung. Kennzeichen je Halter eindeutig (UniqueConstraint).
 */
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "car_owner_id", "licensePlate" }))
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Car extends BaseEntity {
    private String licensePlate;
    private boolean readyToDrive;
    private boolean isMaintained;

    @Enumerated(EnumType.STRING)
    private CarCategory category;

    private String manufacturer;

    @ManyToMany
    @JoinTable(name = "car_booked_services", joinColumns = @JoinColumn(name = "car_id"), inverseJoinColumns = @JoinColumn(name = "service_id"))
    private Set<Service> bookedServices = new HashSet<>();

    @ManyToOne(optional = false)
    @JoinColumn(name = "car_owner_id", nullable = false)
    private CarOwner carOwner;

    @ManyToOne
    @JoinColumn(name = "current_storage_owner_id")
    private StorageOwner currentStorage;

    public Car(CarOwner carOwner, String licensePlate, String manufacturer, CarCategory category) {
        this.carOwner = carOwner;
        this.licensePlate = licensePlate;
        this.manufacturer = manufacturer;
        this.category = category;
        this.readyToDrive = true;
        this.isMaintained = false;
        this.currentStorage = null;
    }

    /** aktualisiert den vom Lagerhalter gepflegten Zustand (fahrbereit/gewartet) */
    public void updateStatus(boolean readyToDrive, boolean maintained) {
        this.readyToDrive = readyToDrive;
        this.isMaintained = maintained;
    }

    /**
     * Fahrzeugumlagerung: aus altem Lager entfernen, in neues aufnehmen (null =
     * auslagern)
     */
    public void relocateTo(StorageOwner newStorageOwner) {
        if (this.currentStorage != null)
            this.currentStorage.removeCarFromStorage(this);
        if (newStorageOwner != null)
            newStorageOwner.addCarToStorage(this);
    }

    /** ändert das Kennzeichen */
    public void changeLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    /** ändert die Kategorie (Youngtimer/Oldtimer) */
    public void changeCategory(CarCategory category) {
        this.category = category;
    }

    /** ändert den Hersteller */
    public void changeManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    /** interne Kopplung, genutzt von StorageOwner.add-/removeCarFromStorage */
    public void setCurrentStorage(StorageOwner currentStorage) {
        this.currentStorage = currentStorage;
    }

    /** bucht einen optionalen Service hinzu */
    public void addBookedOptionalService(Service service) {
        this.bookedServices.add(service);
    }

    /** entfernt einen gebuchten optionalen Service */
    public void removeBookedOptionalService(Service service) {
        this.bookedServices.remove(service);
    }
}
