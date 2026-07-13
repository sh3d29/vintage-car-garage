package com.vintagecargarage.dtos.car;

import com.vintagecargarage.entities.CarCategory;

public record CarInputDto(
        String licensePlate,
        String manufacturer,
        CarCategory category) {
}
