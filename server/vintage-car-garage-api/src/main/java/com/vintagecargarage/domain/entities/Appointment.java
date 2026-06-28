package com.vintagecargarage.domain.entities;

import java.time.LocalDateTime;
import java.time.LocalTime;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Appointment extends BaseEntity {
    private LocalDateTime date;
    private LocalTime time;

    @ManyToOne(optional = false)
    @JoinColumn(name = "storage_owner_id", nullable = false)
    private StorageOwner storageOwner;

    @ManyToOne(optional = false)
    @JoinColumn(name = "car_id", nullable = false)
    private Car subject;

    Appointment(StorageOwner storageOwner, Car subject, LocalDateTime date, LocalTime time) {
        this.storageOwner = storageOwner;
        this.subject = subject;
        this.date = date;
        this.time = time;
    }
}
