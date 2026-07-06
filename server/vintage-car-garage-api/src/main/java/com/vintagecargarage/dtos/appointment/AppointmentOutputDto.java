package com.vintagecargarage.dtos.appointment;

import java.time.LocalDateTime;
import java.time.LocalTime;

import com.vintagecargarage.entities.AppointmentStatus;
import com.vintagecargarage.entities.AppointmentType;

public record AppointmentOutputDto(
                String id,
                AppointmentType type,
                AppointmentStatus status,
                LocalDateTime date,
                LocalTime time,
                String storageOwnerId,
                String carId) {
}
