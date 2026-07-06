package com.vintagecargarage.dtos.service;

public record ServiceInputDto(
                String title,
                String description,
                Float price,
                Boolean isOptional) {
}
