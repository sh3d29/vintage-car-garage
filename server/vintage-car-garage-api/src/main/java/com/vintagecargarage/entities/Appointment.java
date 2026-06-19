package com.vintagecargarage.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Appointment extends BaseEntity {
    private String date;
    private String time;

    @ManyToOne
    private CarOwner carOwner;

    @ManyToOne
    private StorageOwner storageOwner;

    @ManyToOne
    private Car subject;

    Appointment(CarOwner carOwner, StorageOwner storageOwner, Car subject, String date, String time) {
        this.carOwner = carOwner;
        this.storageOwner = storageOwner;
        this.subject = subject;
        this.date = date;
        this.time = time;
    }
}
