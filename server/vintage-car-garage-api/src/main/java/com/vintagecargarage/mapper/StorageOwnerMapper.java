package com.vintagecargarage.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.vintagecargarage.dtos.StorageOwnerDto;
import com.vintagecargarage.entities.StorageOwner;

@Mapper(componentModel = "jakarta")
public interface StorageOwnerMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "passwordVersion", ignore = true)
    StorageOwner toEntity(StorageOwnerDto dto);

    StorageOwnerDto toDto(StorageOwner entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "passwordVersion", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(StorageOwnerDto dto, @MappingTarget StorageOwner entity);
}