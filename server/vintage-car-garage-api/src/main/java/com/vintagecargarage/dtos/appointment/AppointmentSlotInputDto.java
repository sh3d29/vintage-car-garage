package com.vintagecargarage.dtos.appointment;

import java.time.LocalDateTime;
import java.time.LocalTime;

import com.vintagecargarage.entities.AppointmentType;

public record AppointmentSlotInputDto(
                AppointmentType type,
                LocalDateTime date,
                LocalTime time) {
}
