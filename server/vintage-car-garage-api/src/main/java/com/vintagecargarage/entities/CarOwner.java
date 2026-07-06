package com.vintagecargarage.entities;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Getter;

@Entity
@Getter
@AttributeOverride(name = "email", column = @Column(unique = true))
public class CarOwner extends User {

    private String firstName;
    private String lastName;

    @OneToMany(mappedBy = "carOwner", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Car> cars = new HashSet<>();

    @OneToMany(mappedBy = "carOwner", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Request> requests = new HashSet<>();

    // Der Lebenszyklus eines Termins gehört dem StorageOwner (dessen
    // appointments-Sammlung kaskadiert). Hier nur die Sicht über die
    // carOwner-FK – ohne Cascade/orphanRemoval, damit das Freigeben eines
    // gebuchten Termins den Slot nicht löscht.
    @OneToMany(mappedBy = "carOwner")
    private Set<Appointment> bookedAppointments = new HashSet<>();

    public CarOwner(String email, String password, int passwordVersion, String firstName, String lastName) {
        super(email, password, passwordVersion);
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public void updateProfile(String email, String firstName, String lastName) {
        if (email != null)
            changeEmail(email);
        if (firstName != null)
            this.firstName = firstName;
        if (lastName != null)
            this.lastName = lastName;
    }

    public Car registerCar(String licensePlate) {
        Car car = new Car(this, licensePlate);
        this.cars.add(car);
        return car;
    }

    public Request submitRequest(StorageOwner storageOwner, String description) {
        Request request = new Request(this, storageOwner, description);
        this.requests.add(request);
        storageOwner.addRequest(request);
        return request;
    }

    public void removeCar(Car car) {
        this.cars.remove(car);
    }

    public void removeRequest(Request request) {
        this.requests.remove(request);
    }

    public void addAppointment(Appointment appointment) {
        this.bookedAppointments.add(appointment);
    }

    public void removeAppointment(Appointment appointment) {
        this.bookedAppointments.remove(appointment);
    }
}