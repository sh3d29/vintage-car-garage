package com.vintagecargarage.dtos;

import java.time.LocalTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.vintagecargarage.entities.Location;
import com.vintagecargarage.entities.Storage;

public record StorageOwnerDto(
        String email,
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) String password, String companyName,
        float costs, LocalTime serviceTime,
        Location location,
        Storage storage,
        List<String> carManufacturers) {
}