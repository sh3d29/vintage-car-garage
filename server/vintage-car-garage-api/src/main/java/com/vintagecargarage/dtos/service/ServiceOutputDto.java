package com.vintagecargarage.dtos.service;

public record ServiceOutputDto(
        String id,
        String title,
        String description,
        float price,
        boolean isOptional) {
}
