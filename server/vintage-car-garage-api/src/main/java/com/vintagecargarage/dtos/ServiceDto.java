package com.vintagecargarage.dtos;

public record ServiceDto(
        String id,
        String title,
        String description,
        float price,
        boolean isOptional,
        String storageOwnerId) {
}
