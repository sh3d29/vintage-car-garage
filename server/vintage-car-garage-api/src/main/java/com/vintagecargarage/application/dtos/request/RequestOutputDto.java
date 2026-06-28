package com.vintagecargarage.application.dtos.request;

public record RequestOutputDto(
        String id,
        String type,
        String description,
        String storageOwnerId,
        String carOwnerId) {
}
