package com.vintagecargarage.application.dtos.car;

import java.util.Set;

public record CarOutputDto(
                String id,
                String licensePlate,
                boolean readyToDrive,
                boolean isMaintained,
                Set<String> bookedServiceIds,
                String currentStorageOwnerId) {
}