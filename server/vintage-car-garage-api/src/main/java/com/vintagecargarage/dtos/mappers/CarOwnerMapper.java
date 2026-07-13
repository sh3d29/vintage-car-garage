package com.vintagecargarage.dtos.mappers;

import com.vintagecargarage.dtos.user.CarOwnerOutputDto;
import com.vintagecargarage.entities.CarOwner;

public final class CarOwnerMapper {

    private CarOwnerMapper() {
    }

    public static CarOwnerOutputDto toCarOwnerOutputDto(CarOwner co) {
        return new CarOwnerOutputDto(co.getId(), co.getEmail(), co.getFirstName(), co.getLastName());
    }
}
