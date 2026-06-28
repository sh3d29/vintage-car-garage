package com.vintagecargarage.application.dtos.offer;

public record OfferOutputDto(
        String id,
        String description,
        float costs,
        Boolean accepted,
        String requestId,
        String storageOwnerId) {
}
