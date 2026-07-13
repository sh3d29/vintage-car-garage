package com.vintagecargarage.dtos.offer;

public record OfferOutputDto(
        String id,
        String description,
        float costs,
        Boolean accepted,
        String requestId,
        String storageOwnerId,
        String storageOwnerName,
        String carOwnerId) {
}
