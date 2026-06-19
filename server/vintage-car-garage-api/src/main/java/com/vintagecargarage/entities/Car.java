package com.vintagecargarage.entities;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Car extends BaseEntity {
    private String licensePlate;
    private boolean readyToDrive;
    private boolean isMaintained;

    @ManyToOne
    private CarOwner owner;

    @ManyToMany
    private Set<Service> bookedServices = new HashSet<>();

    Car(CarOwner owner, String licensePlate) {
        this.owner = owner;
        this.licensePlate = licensePlate;
        this.readyToDrive = false;
        this.isMaintained = false;
    }

    /** CO_UC_L3_1: updateCarStatus */
    public void updateStatus(boolean readyToDrive, boolean isMaintained) {
        this.readyToDrive = readyToDrive;
        this.isMaintained = isMaintained;
    }

    /** CO_UC_E8_1: updateCar */
    public void updateLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    /** CO_UC_E6_1: bookOptionalService */
    public void bookService(Service service) {
        this.bookedServices.add(service);
    }
}
