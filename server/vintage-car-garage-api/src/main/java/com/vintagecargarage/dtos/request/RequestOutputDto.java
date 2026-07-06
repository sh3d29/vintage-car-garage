package com.vintagecargarage.dtos.request;

import com.vintagecargarage.entities.RequestType;

public record RequestOutputDto(
        String id,
        RequestType type,
        String description,
        String storageOwnerId,
        String carOwnerId,
        boolean offerAccepted) {
}
