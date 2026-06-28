package com.vintagecargarage.domain.entities;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@AttributeOverride(name = "email", column = @Column(unique = true))
public class CarOwner extends User {
    private String firstName;
    private String lastName;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "car_owner_id", nullable = false)
    private Set<Car> cars = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "car_owner_id", nullable = false)
    private Set<Request> requests = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "car_owner_id", nullable = false)
    private Set<Appointment> appointments = new HashSet<>();

    private CarOwner(String email, String password, int passwordVersion, String firstName, String lastName) {
        super(email, password, passwordVersion);
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public static CarOwner createCarOwner(String email, String password, String firstName, String lastName) {
        return new CarOwner(email, password, 1, firstName, lastName);
    }

    public void updateCarOwner(String email, String password, String firstName, String lastName) {
        if (email != null)
            this.setEmail(email);
        if (password != null)
            this.setPassword(password);
        if (firstName != null)
            this.setFirstName(firstName);
        if (lastName != null)
            this.setLastName(lastName);
    }

    // --- Fahrzeuge (UC_E7 / UC_E8) ---

    public Car createCar(String licensePlate) {
        Car car = new Car(licensePlate);
        this.cars.add(car);
        return car;
    }

    public Set<Car> getAllCars() {
        return Set.copyOf(this.cars);
    }

    public Car getCarById(String carId) {
        return this.cars.stream()
                .filter(car -> car.getId() != null && car.getId().equals(carId))
                .findFirst().orElse(null);
    }

    public Car getCarByLicensePlate(String licensePlate) {
        return this.cars.stream()
                .filter(car -> car.getLicensePlate() != null && car.getLicensePlate().equals(licensePlate))
                .findFirst().orElse(null);
    }

    public void updateCar(String carId, String licensePlate, Boolean readyToDrive, Boolean isMaintained) {
        Car car = this.getCarById(carId);
        if (car == null) {
            return;
        }
        if (licensePlate != null)
            car.setLicensePlate(licensePlate);
        if (readyToDrive != null)
            car.setReadyToDrive(readyToDrive);
        if (isMaintained != null)
            car.setMaintained(isMaintained);
    }

    public boolean removeCar(Car car) {
        return this.cars.remove(car);
    }

    // --- Stellplatzanfragen (UC_E2) ---

    public Request createRequest(StorageOwner storageOwner, String description) {
        Request request = new Request(storageOwner, RequestType.STELLPLATZ, description);
        this.requests.add(request);
        return request;
    }

    public Set<Request> getAllRequests() {
        return Set.copyOf(this.requests);
    }

    public Request getRequestById(String requestId) {
        return this.requests.stream()
                .filter(request -> request.getId() != null && request.getId().equals(requestId))
                .findFirst().orElse(null);
    }

    public boolean deleteRequest(Request request) {
        return this.requests.remove(request);
    }

    // --- Stellplatzangebote (UC_E3) ---

    public Set<Offer> getReceivedOffers() {
        return this.requests.stream()
                .map(Request::getOffers)
                .filter(Objects::nonNull) // Filtert leere Beziehungs-Referenzen heraus
                .collect(Collectors.toSet());
    }

    public void respondToOffer(String offerId, Boolean accepted) {
        if (accepted == null) {
            return;
        }
        this.requests.stream()
                .map(Request::getOffers)
                .filter(Objects::nonNull)
                .filter(offer -> offer.getId() != null && offer.getId().equals(offerId))
                .findFirst()
                .ifPresent(offer -> offer.setAccepted(accepted));
    }

    // --- Termine (UC_E5) ---

    public Appointment createAppointment(StorageOwner storageOwner, Car subject, LocalDateTime date, LocalTime time) {
        Appointment appointment = new Appointment(storageOwner, subject, date, time);
        this.appointments.add(appointment);
        return appointment;
    }

    public Appointment getAppointmentById(String appointmentId) {
        return this.appointments.stream()
                .filter(appointment -> appointment.getId() != null
                        && appointment.getId().equals(appointmentId))
                .findFirst().orElse(null);
    }

    public Set<Appointment> getAllAppointments() {
        return Set.copyOf(this.appointments);
    }

    public boolean deleteAppointment(Appointment appointment) {
        return this.appointments.remove(appointment);
    }
}