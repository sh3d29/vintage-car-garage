package com.vintagecargarage.dtos.user;

import java.time.LocalTime;
import java.util.Set;

public record StorageOwnerInputDto(
        String email,
        String password,
        String companyName,
        Float costs,
        LocalTime serviceTime,
        LocationDto location,
        StorageDto storage,
        Set<String> carManufacturers) {
}
