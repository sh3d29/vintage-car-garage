package com.vintagecargarage.dtos;

import java.util.List;

public record CarDto(
        String id,
        String licensePlate,
        boolean readyToDrive,
        boolean isMaintained,
        String ownerId,
        List<String> bookedServices) {
}
