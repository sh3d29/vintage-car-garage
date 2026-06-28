package com.vintagecargarage.application.dtos.user;

import java.time.LocalTime;
import java.util.Set;

public record StorageOwnerOutputDto(
                String id,
                String email,
                String companyName,
                float costs,
                LocalTime serviceTime,
                LocationDto location,
                StorageDto storage,
                Set<String> carManufacturers) {
}
