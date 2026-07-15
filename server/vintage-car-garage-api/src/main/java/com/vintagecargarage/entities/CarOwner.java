package com.vintagecargarage.entities;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Getter;

/**
 * Einlagerer: Creator/Information Expert für die
 * eigenen Cars, Requests und Appointments; fragt Stellplätze beim StorageOwner
 * an.
 */
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

    @OneToMany(mappedBy = "carOwner")
    private Set<Appointment> bookedAppointments = new HashSet<>();

    public CarOwner(String email, String password, int passwordVersion, String firstName, String lastName) {
        super(email, password, passwordVersion);
        this.firstName = firstName;
        this.lastName = lastName;
    }

    /** aktualisiert die Profildaten (null-Felder bleiben unverändert) */
    public void updateProfile(String email, String firstName, String lastName) {
        if (email != null)
            changeEmail(email);
        if (firstName != null)
            this.firstName = firstName;
        if (lastName != null)
            this.lastName = lastName;
    }

    /** erzeugt ein Car und nimmt es in den Bestand auf (Creator) */
    public Car registerCar(String licensePlate, String manufacturer, CarCategory category) {
        Car car = new Car(this, licensePlate, manufacturer, category);
        this.cars.add(car);
        return car;
    }

    /**
     * erzeugt eine Request an den StorageOwner (Creator) und verknüpft sie
     * beidseitig
     */
    public Request submitRequest(StorageOwner storageOwner, String description) {
        Request request = new Request(this, storageOwner, description);
        this.requests.add(request);
        storageOwner.addRequest(request);
        return request;
    }

    /** entfernt ein Car aus dem Bestand (orphanRemoval löscht es) */
    public void removeCar(Car car) {
        this.cars.remove(car);
    }

    /** entfernt eine Request aus dem Bestand (orphanRemoval löscht sie) */
    public void removeRequest(Request request) {
        this.requests.remove(request);
    }

    /** vermerkt einen gebuchten Appointment */
    public void addAppointment(Appointment appointment) {
        this.bookedAppointments.add(appointment);
    }

    /** löst die Zuordnung eines Appointments */
    public void removeAppointment(Appointment appointment) {
        this.bookedAppointments.remove(appointment);
    }
}