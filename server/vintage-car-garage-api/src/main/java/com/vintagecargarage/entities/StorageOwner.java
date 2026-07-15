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

/**
 * zentraler Creator/Information Expert für
 * Services, Requests, Offers, Appointments und die eingelagerten Cars; hält
 * Stammdaten (Location, Storage, Servicezeiten, akzeptierte Hersteller).
 */
@Entity
@Getter
@AttributeOverride(name = "email", column = @Column(unique = true))
public class StorageOwner extends User {

    // --- STAMMDATEN ---
    private String companyName;
    private float costs;
    private LocalTime serviceStartTime;
    private LocalTime serviceEndTime;
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
            LocalTime serviceTime, LocalTime serviceEndTime, Location location, Storage storage,
            Set<String> carManufacturers) {
        super(email, password, passwordVersion);
        this.companyName = companyName;
        this.costs = costs;
        this.serviceStartTime = serviceTime;
        this.serviceEndTime = serviceEndTime;
        this.location = location;
        this.storage = storage;
        this.carManufacturers = carManufacturers;
    }

    /**
     * aktualisiert die Stammdaten (null-Felder bleiben unverändert; Herstellerliste
     * wird ersetzt)
     */
    public void updateProfile(String email, String companyName, Float costs, LocalTime serviceTime,
            LocalTime serviceEndTime, Location location, Storage storage, Set<String> carManufacturers) {
        if (email != null)
            changeEmail(email);
        if (companyName != null)
            this.companyName = companyName;
        if (costs != null)
            this.costs = costs;
        if (serviceTime != null)
            this.serviceStartTime = serviceTime;
        if (serviceEndTime != null)
            this.serviceEndTime = serviceEndTime;
        if (location != null)
            this.location = location;
        if (storage != null)
            this.storage = storage;
        if (carManufacturers != null) {
            this.carManufacturers.clear();
            this.carManufacturers.addAll(carManufacturers);
        }
    }

    /** erzeugt einen Service und nimmt ihn ins Portfolio auf (Creator) */
    public Service openService(String title, String description, float price, boolean optional) {
        Service service = new Service(this, title, description, price, optional);
        this.services.add(service);
        return service;
    }

    /**
     * erzeugt ein Offer auf eine Request (Creator); Rückverknüpfung im
     * Offer-Konstruktor
     */
    public Offer makeOffer(Request request, String description, float costs) {
        Offer offer = new Offer(this, request, description, costs);
        this.offers.add(offer);
        return offer;
    }

    /** zieht ein Offer zurück und gibt die zugehörige Request wieder frei */
    public void withdrawOffer(Offer offer) {
        this.offers.remove(offer);
        offer.getRequest().reopen();
    }

    /**
     * erzeugt einen freien Termin-Slot und nimmt ihn in den Terminplan auf
     * (Creator)
     */
    public Appointment scheduleAppointment(AppointmentType type, LocalDateTime date, LocalTime time) {
        Appointment appointment = new Appointment(this, type, date, time);
        this.appointments.add(appointment);
        return appointment;
    }

    /** entfernt einen Service aus dem Portfolio (orphanRemoval löscht ihn) */
    public void removeService(Service service) {
        this.services.remove(service);
    }

    /** nimmt ein Car ins Lager auf (setzt beidseitig die Zuordnung) */
    public void addCarToStorage(Car car) {
        car.setCurrentStorage(this);
        this.storedCars.add(car);
    }

    /** lagert ein Car aus (löst beidseitig die Zuordnung) */
    public void removeCarFromStorage(Car car) {
        car.setCurrentStorage(null);
        this.storedCars.remove(car);
    }

    /** vermerkt eine eingegangene Request */
    public void addRequest(Request request) {
        this.incomingRequests.add(request);
    }

    /** löst die Zuordnung eines Appointments */
    public void removeAppointment(Appointment appointment) {
        this.appointments.remove(appointment);
    }
}