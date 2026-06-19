package com.vintagecargarage.dtos;

public record RequestDto(
                String id,
                String description,
                String carOwnerId,
                String storageOwnerId) {
}
