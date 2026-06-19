package com.vintagecargarage.entities;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
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
public class CarOwner extends User {
    private String firstName;
    private String lastName;

    public CarOwner(String email, String password, int passwordVersion, String firstName, String lastName) {
        super(email, password, passwordVersion);
        this.firstName = firstName;
        this.lastName = lastName;
    }

    /** Creator (CO_UC_E7_1: createCar): der Einlagerer registriert ein eigenes Fahrzeug. */
    public Car createCar(String licensePlate) {
        return new Car(this, licensePlate);
    }

    /** Creator (CO_UC_E2_1: createStorageRequest): der Einlagerer stellt eine Anfrage. */
    public Request createRequest(StorageOwner storageOwner, String description) {
        return new Request(this, storageOwner, description);
    }

    /** Creator (CO_UC_E5_1: createAppointment): der Einlagerer koordiniert einen Termin. */
    public Appointment createAppointment(StorageOwner storageOwner, Car subject, String date, String time) {
        return new Appointment(this, storageOwner, subject, date, time);
    }
}
