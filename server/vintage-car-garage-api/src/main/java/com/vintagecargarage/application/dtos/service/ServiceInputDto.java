package com.vintagecargarage.application.dtos.service;

public record ServiceInputDto(
                String title,
                String description,
                float price,
                boolean isOptional) {
}
