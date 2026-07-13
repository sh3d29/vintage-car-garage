package com.vintagecargarage.dtos.car;

import java.util.Set;

import com.vintagecargarage.entities.CarCategory;

public record CarOutputDto(
                String id,
                String licensePlate,
                CarCategory category,
                String manufacturer,
                boolean readyToDrive,
                boolean isMaintained,
                Set<String> bookedServiceIds,
                String currentStorageOwnerId,
                String currentStorageOwnerName) {
}
