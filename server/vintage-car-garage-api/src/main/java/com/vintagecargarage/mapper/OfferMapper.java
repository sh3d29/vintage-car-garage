package com.vintagecargarage.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.vintagecargarage.dtos.OfferDto;
import com.vintagecargarage.entities.Offer;

@Mapper(componentModel = "jakarta")
public interface OfferMapper {

    @Mapping(target = "requestId", source = "request.id")
    @Mapping(target = "storageOwnerId", source = "storageOwner.id")
    OfferDto toDto(Offer entity);

    List<OfferDto> toDtoList(List<Offer> entities);
}
