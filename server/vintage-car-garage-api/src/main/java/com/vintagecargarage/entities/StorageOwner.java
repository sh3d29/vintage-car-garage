package com.vintagecargarage.entities;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Getter;

@Entity
@Getter
@AttributeOverride(name = "email", column = @Column(unique = true))
public class StorageOwner extends User {

    // --- STAMMDATEN ---
    private String companyName;
    private float costs;
    private LocalTime serviceTime;
    @Embedded
    private Location location;
    @Embedded
    private Storage storage;
    @ElementCollection
    private Set<String> carManufacturers = new HashSet<>();

    @OneToMany(mappedBy = "storageOwner", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Service> services = new HashSet<>();

    @OneToMany(mappedBy = "currentStorage")
    private Set<Car> storedCars = new HashSet<>();

    @OneToMany(mappedBy = "storageOwner")
    private Set<Request> incomingRequests = new HashSet<>();

    @OneToMany(mappedBy = "storageOwner", cascade = CascadeType.ALL)
    private Set<Offer> offers = new HashSet<>();

    @OneToMany(mappedBy = "storageOwner", cascade = CascadeType.ALL)
    private Set<Appointment> appointments = new HashSet<>();

    public StorageOwner(String email, String password, int passwordVersion, String companyName, float costs,
            LocalTime serviceTime, Location location, Storage storage, Set<String> carManufacturers) {
        super(email, password, passwordVersion);
        this.companyName = companyName;
        this.costs = costs;
        this.serviceTime = serviceTime;
        this.location = location;
        this.storage = storage;
        this.carManufacturers = carManufacturers;
    }

    public void updateProfile(String email, String companyName, Float costs, LocalTime serviceTime,
            Location location, Storage storage, Set<String> carManufacturers) {
        if (email != null)
            changeEmail(email);
        if (companyName != null)
            this.companyName = companyName;
        if (costs != null)
            this.costs = costs;
        if (serviceTime != null)
            this.serviceTime = serviceTime;
        if (location != null)
            this.location = location;
        if (storage != null)
            this.storage = storage;
        if (carManufacturers != null) {
            this.carManufacturers.clear();
            this.carManufacturers.addAll(carManufacturers);
        }
    }

    public Service openService(String title, String description, float price, boolean optional) {
        Service service = new Service(this, title, description, price, optional);
        this.services.add(service);
        return service;
    }

    public Offer makeOffer(Request request, String description, float costs) {
        Offer offer = new Offer(this, request, description, costs);
        this.offers.add(offer);
        return offer;
    }

    public Appointment scheduleAppointment(AppointmentType type, LocalDateTime date, LocalTime time) {
        Appointment appointment = new Appointment(this, type, date, time);
        this.appointments.add(appointment);
        return appointment;
    }

    public void removeService(Service service) {
        this.services.remove(service);
    }

    public void addCarToStorage(Car car) {
        car.setCurrentStorage(this);
        this.storedCars.add(car);
    }

    public void removeCarFromStorage(Car car) {
        car.setCurrentStorage(null);
        this.storedCars.remove(car);
    }

    public void addRequest(Request request) {
        this.incomingRequests.add(request);
    }

    public void removeAppointment(Appointment appointment) {
        this.appointments.remove(appointment);
    }
}