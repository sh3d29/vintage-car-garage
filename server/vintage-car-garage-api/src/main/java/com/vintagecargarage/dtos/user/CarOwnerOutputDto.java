package com.vintagecargarage.dtos.user;

public record CarOwnerOutputDto(
        String id,
        String email,
        String firstName,
        String lastName) implements UserOutputDto {
}
