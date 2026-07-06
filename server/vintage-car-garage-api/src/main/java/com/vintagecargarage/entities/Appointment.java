package com.vintagecargarage.entities;

import java.time.LocalDateTime;
import java.time.LocalTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Appointment extends BaseEntity {
    @Enumerated(EnumType.STRING)
    private AppointmentType type;

    @Enumerated(EnumType.STRING)
    private AppointmentStatus status;

    private LocalDateTime date;
    private LocalTime time;

    @ManyToOne(optional = false)
    @JoinColumn(name = "storage_owner_id", nullable = false)
    private StorageOwner storageOwner;

    @ManyToOne(optional = true)
    @JoinColumn(name = "car_owner_id", nullable = true)
    private CarOwner carOwner;

    @ManyToOne(optional = true)
    @JoinColumn(name = "car_id", nullable = true)
    private Car subject;

    public Appointment(StorageOwner storageOwner, AppointmentType type, LocalDateTime date, LocalTime time) {
        this.storageOwner = storageOwner;
        this.type = type;
        this.date = date;
        this.time = time;
        this.subject = null;
        this.status = AppointmentStatus.AVAILABLE;
    }

    public void book(CarOwner carOwner, Car car) {
        this.carOwner = carOwner;
        this.subject = car;
        this.status = AppointmentStatus.BOOKED;
    }

    /**
     * Schließt den Termin ab und führt die eigentliche Umlagerung durch:
     * Bei EINLAGERUNG wird das Fahrzeug dem Lagerhalter zugeordnet, bei
     * AUSLAGERUNG aus dem Lager entfernt.
     */
    public void complete() {
        this.subject.relocateTo(this.type == AppointmentType.EINLAGERUNG ? this.storageOwner : null);
        this.status = AppointmentStatus.COMPLETED;
    }

    /**
     * Gibt einen gebuchten Termin wieder frei: der Slot wird erneut buchbar und
     * die Zuordnung zu Einlagerer und Fahrzeug wird gelöst.
     */
    public void cancel() {
        this.carOwner = null;
        this.subject = null;
        this.status = AppointmentStatus.AVAILABLE;
    }
}