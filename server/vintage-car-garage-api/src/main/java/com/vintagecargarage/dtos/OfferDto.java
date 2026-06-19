package com.vintagecargarage.dtos;

public record OfferDto(
        String id,
        String description,
        float costs,
        Boolean accepted,
        String requestId,
        String storageOwnerId) {
}
