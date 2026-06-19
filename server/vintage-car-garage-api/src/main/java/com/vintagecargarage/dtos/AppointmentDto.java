package com.vintagecargarage.dtos;

public record AppointmentDto(
                String id,
                String date,
                String time,
                String carOwnerId,
                String storageOwnerId,
                String subjectCarId) {
}
