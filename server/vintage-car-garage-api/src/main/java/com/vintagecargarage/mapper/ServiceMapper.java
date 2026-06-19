package com.vintagecargarage.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.vintagecargarage.dtos.ServiceDto;
import com.vintagecargarage.entities.Service;

@Mapper(componentModel = "jakarta")
public interface ServiceMapper {

    @Mapping(target = "storageOwnerId", source = "storageOwner.id")
    @Mapping(target = "isOptional", source = "optional")
    ServiceDto toDto(Service entity);

    List<ServiceDto> toDtoList(List<Service> entities);
}
