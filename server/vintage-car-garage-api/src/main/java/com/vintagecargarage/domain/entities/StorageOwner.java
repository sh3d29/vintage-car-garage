package com.vintagecargarage.domain.entities;

import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
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
    private Set<String> carManufacturers;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "storage_owner_id", nullable = false)
    private Set<Service> services = new HashSet<>();

    @OneToMany(mappedBy = "storageOwner", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Offer> offers = new HashSet<>();

    @OneToMany(mappedBy = "storageOwner")
    private Set<Request> incomingRequests = new HashSet<>();

    @OneToMany(mappedBy = "currentStorage")
    private Set<Car> storedCars = new HashSet<>();

    private StorageOwner(String email, String password, int passwordVersion, String companyName, float costs,
            LocalTime serviceTime, Location location, Storage storage, Set<String> carManufacturers) {
        super(email, password, passwordVersion);
        this.companyName = companyName;
        this.costs = costs;
        this.serviceTime = serviceTime;
        this.location = location;
        this.storage = storage;
        this.carManufacturers = carManufacturers;
    }

    public static StorageOwner createStorageOwner(String email, String password,
            String companyName, float costs,
            LocalTime serviceTime, Location location, Storage storage, Set<String> carManufacturers) {
        return new StorageOwner(email, password, 1, companyName, costs, serviceTime, location, storage,
                carManufacturers);
    }

    public void updateStorageOwner(String email, String password, String companyName, Float costs,
            LocalTime serviceTime, Location location, Storage storage, Set<String> carManufacturers) {
        if (email != null) {
            this.setEmail(email);
        }
        if (password != null) {
            this.setPassword(password);
        }
        if (companyName != null) {
            this.setCompanyName(companyName);
        }
        if (costs != null) {
            this.setCosts(costs);
        }
        if (serviceTime != null) {
            this.setServiceTime(serviceTime);
        }
        if (location != null) {
            this.setLocation(location);
        }
        if (storage != null) {
            this.setStorage(storage);
        }
        if (carManufacturers != null) {
            this.carManufacturers.clear();
            this.carManufacturers.addAll(carManufacturers);
        }
    }

    // --- Serviceportfolio (UC_L1 / UC_L6) ---

    public Service createService(String title, String description, float price, boolean isOptional) {
        Service service = new Service(title, description, price, isOptional);
        this.services.add(service);
        return service;
    }

    public Service getServiceById(String id) {
        return services.stream()
                .filter(service -> service.getId() != null && service.getId().equals(id))
                .findFirst().orElse(null);
    }

    public Set<Service> getAllServices() {
        return Set.copyOf(this.services);
    }

    public void updateService(String id, String title, String description, Float price, Boolean isOptional) {
        this.services.stream()
                .filter(currentService -> currentService.getId() != null
                        && currentService.getId().equals(id))
                .findFirst()
                .ifPresent(currentService -> {
                    if (title != null)
                        currentService.setTitle(title);
                    if (description != null)
                        currentService.setDescription(description);
                    if (price != null)
                        currentService.setPrice(price);
                    if (isOptional != null)
                        currentService.setOptional(isOptional);
                });
    }

    public boolean deleteService(Service service) {
        return this.services.remove(service);
    }

    // --- Stellplatzanfragen (UC_L7) ---

    public Set<Request> getIncomingRequests() {
        return Set.copyOf(this.incomingRequests);
    }

    public Request getIncomingRequestById(String requestId) {
        return this.incomingRequests.stream()
                .filter(request -> request.getId() != null && request.getId().equals(requestId))
                .findFirst().orElse(null);
    }

    // --- Angebote (UC_L7) ---

    public Offer createOffer(String requestId, String description, float costs) {
        Request request = getIncomingRequestById(requestId);
        if (request == null) {
            throw new IllegalArgumentException("Stellplatzanfrage nicht gefunden");
        }
        Offer offer = new Offer(this, request, description, costs);
        this.offers.add(offer);
        return offer;
    }

    public Offer getOfferById(String offerId) {
        return this.offers.stream()
                .filter(offer -> offer.getId() != null && offer.getId().equals(offerId))
                .findFirst().orElse(null);
    }

    public Set<Offer> getAllOffers() {
        return Set.copyOf(this.offers);
    }

    public boolean deleteOffer(Offer offer) {
        return this.offers.remove(offer);
    }

    // --- Fahrzeugstatus eingelagerter Fahrzeuge (UC_L3) ---

    public Set<Car> getStoredCars() {
        return Set.copyOf(this.storedCars);
    }

    public Car updateStoredCarStatus(String carId, boolean readyToDrive, boolean isMaintained) {
        Car car = this.storedCars.stream()
                .filter(c -> c.getId() != null && c.getId().equals(carId))
                .findFirst().orElse(null);
        if (car != null) {
            car.setReadyToDrive(readyToDrive);
            car.setMaintained(isMaintained);
        }
        return car;
    }
}