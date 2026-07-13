package com.vintagecargarage.dtos.user;

import java.time.LocalTime;
import java.util.List;
import java.util.Set;

import com.vintagecargarage.dtos.service.ServiceOutputDto;

public record StorageOwnerSearchResDto(
        String id,
        String companyName,
        float costs, LocalTime serviceStartTime,
        LocalTime serviceEndTime,
        LocationDto location,
        StorageDto storage,
        Set<String> carManufacturers,
        List<ServiceOutputDto> services) {

}
