package com.vintagecargarage.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.vintagecargarage.dtos.RequestDto;
import com.vintagecargarage.entities.Request;

@Mapper(componentModel = "jakarta")
public interface RequestMapper {

    @Mapping(target = "carOwnerId", source = "carOwner.id")
    @Mapping(target = "storageOwnerId", source = "storageOwner.id")
    RequestDto toDto(Request entity);

    List<RequestDto> toDtoList(List<Request> entities);
}
