package com.vintagecargarage.mappers;

import java.util.Set;

import com.vintagecargarage.dtos.service.ServiceOutputDto;
import com.vintagecargarage.dtos.user.LocationDto;
import com.vintagecargarage.dtos.user.StorageDto;
import com.vintagecargarage.dtos.user.StorageOwnerOutputDto;
import com.vintagecargarage.dtos.user.StorageOwnerSearchResDto;
import com.vintagecargarage.entities.Service;
import com.vintagecargarage.entities.StorageOwner;

public final class StorageOwnerMapper {

    private StorageOwnerMapper() {}

    public static StorageOwnerOutputDto toStorageOwnerOutputDto(StorageOwner so) {
        LocationDto locationDto = LocationStorageMapper.toLocationDto(so.getLocation());
        StorageDto storageDto = LocationStorageMapper.toStorageDto(so.getStorage());
        return new StorageOwnerOutputDto(so.getId(), so.getEmail(), so.getCompanyName(),
                so.getCosts(), so.getServiceTime(), locationDto, storageDto,
                so.getCarManufacturers() != null ? Set.copyOf(so.getCarManufacturers()) : Set.of());
    }

    public static StorageOwnerSearchResDto toStorageOwnerSearchResDto(StorageOwner so) {
        LocationDto locationDto = LocationStorageMapper.toLocationDto(so.getLocation());
        StorageDto storageDto = LocationStorageMapper.toStorageDto(so.getStorage());
        return new StorageOwnerSearchResDto(so.getId(), so.getCompanyName(), so.getCosts(),
                so.getServiceTime(), locationDto, storageDto,
                so.getCarManufacturers() != null ? Set.copyOf(so.getCarManufacturers()) : Set.of());
    }

    public static ServiceOutputDto toServiceOutputDto(Service s) {
        return new ServiceOutputDto(s.getId(), s.getTitle(), s.getDescription(), s.getPrice(), s.isOptional());
    }
}
