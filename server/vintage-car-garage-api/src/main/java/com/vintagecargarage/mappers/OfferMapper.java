package com.vintagecargarage.mappers;

import com.vintagecargarage.dtos.offer.OfferOutputDto;
import com.vintagecargarage.entities.Offer;
import com.vintagecargarage.entities.OfferStatus;

public final class OfferMapper {

    private OfferMapper() {}

    public static OfferOutputDto toOfferOutputDto(Offer offer) {
        String carOwnerId = offer.getRequest().getCarOwner() != null
                ? offer.getRequest().getCarOwner().getId() : null;
        Boolean accepted = offer.getStatus() == OfferStatus.ACCEPTED ? Boolean.TRUE
                : offer.getStatus() == OfferStatus.REJECTED ? Boolean.FALSE : null;
        return new OfferOutputDto(offer.getId(), offer.getDescription(), offer.getCosts(),
                accepted, offer.getRequest().getId(), offer.getStorageOwner().getId(), carOwnerId);
    }
}
