package com.vintagecargarage.application.dtos.appointment;

import java.time.LocalDateTime;
import java.time.LocalTime;

public record AppointmentInputDto(
                String storageOwnerId,
                String carId,
                LocalDateTime date,
                LocalTime time) {
}