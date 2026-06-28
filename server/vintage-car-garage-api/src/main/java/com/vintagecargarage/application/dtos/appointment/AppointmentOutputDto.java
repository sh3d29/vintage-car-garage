package com.vintagecargarage.application.dtos.appointment;

import java.time.LocalDateTime;
import java.time.LocalTime;

public record AppointmentOutputDto(
        String id,
        LocalDateTime date,
        LocalTime time,
        String storageOwnerId,
        String carId) {
}
