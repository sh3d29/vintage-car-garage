package com.vintagecargarage.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.vintagecargarage.dtos.CarOwnerDto;
import com.vintagecargarage.entities.CarOwner;

@Mapper(componentModel = "jakarta")
public interface CarOwnerMapper extends BaseMapper<CarOwner, CarOwnerDto> {

    @Override
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "passwordVersion", ignore = true)
    @Mapping(target = "id", ignore = true)
    CarOwner toEntity(CarOwnerDto dto);

    @Override
    CarOwnerDto toDto(CarOwner entity);

    @Override
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "passwordVersion", ignore = true)
    @Mapping(target = "id", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(CarOwnerDto dto, @MappingTarget CarOwner entity);
}