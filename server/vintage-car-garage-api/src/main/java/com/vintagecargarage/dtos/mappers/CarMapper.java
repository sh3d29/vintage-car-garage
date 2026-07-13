package com.vintagecargarage.dtos.mappers;

import java.util.stream.Collectors;

import com.vintagecargarage.dtos.car.CarOutputDto;
import com.vintagecargarage.entities.Car;
import com.vintagecargarage.entities.Service;

public final class CarMapper {

    private CarMapper() {
    }

    public static CarOutputDto toCarOutputDto(Car car) {
        return new CarOutputDto(
                car.getId(), car.getLicensePlate(), car.getCategory(), car.getManufacturer(),
                car.isReadyToDrive(), car.isMaintained(),
                car.getBookedServices().stream().map(Service::getId).collect(Collectors.toSet()),
                car.getCurrentStorage() != null ? car.getCurrentStorage().getId() : null,
                car.getCurrentStorage() != null ? car.getCurrentStorage().getCompanyName() : null);
    }
}
