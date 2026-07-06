package com.vintagecargarage.dtos.user;

import java.time.LocalTime;
import java.util.Set;

public record StorageOwnerSearchResDto(
                String id,
                String companyName,
                float costs, LocalTime serviceTime,
                LocationDto location,
                StorageDto storage,
                Set<String> carManufacturers) {

}
