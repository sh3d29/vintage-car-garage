package com.vintagecargarage.dtos.mappers;

import com.vintagecargarage.dtos.request.RequestOutputDto;
import com.vintagecargarage.entities.Request;

public final class RequestMapper {

    private RequestMapper() {
    }

    public static RequestOutputDto toRequestOutputDto(Request r) {
        return new RequestOutputDto(r.getId(), r.getType(), r.getDescription(),
                r.getStorageOwner().getId(),
                r.getCarOwner() != null ? r.getCarOwner().getId() : null);
    }
}
