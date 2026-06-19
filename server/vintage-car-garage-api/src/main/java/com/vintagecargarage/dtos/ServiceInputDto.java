package com.vintagecargarage.dtos;

public record ServiceInputDto(
        String title,
        String description,
        float price,
        boolean isOptional) {
}
