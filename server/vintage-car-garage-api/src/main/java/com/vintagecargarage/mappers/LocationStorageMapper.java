package com.vintagecargarage.mappers;

import java.util.List;

import com.vintagecargarage.dtos.user.LocationDto;
import com.vintagecargarage.dtos.user.StorageDto;
import com.vintagecargarage.entities.Location;
import com.vintagecargarage.entities.Storage;

public final class LocationStorageMapper {

    private LocationStorageMapper() {
    }

    public static LocationDto toLocationDto(Location location) {
        if (location == null) {
            return null;
        }
        return new LocationDto(location.getStreet(), location.getHouseNumber(),
                location.getPostalCode(), location.getCity());
    }

    public static StorageDto toStorageDto(Storage storage) {
        if (storage == null) {
            return null;
        }
        List<String> conditions = storage.getConditions() != null
                ? List.copyOf(storage.getConditions())
                : List.of();
        return new StorageDto(conditions, storage.getCapacity());
    }
}
