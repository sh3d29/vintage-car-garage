package com.vintagecargarage.application.dtos.user;

public record LocationDto(
        String street,
        String houseNumber,
        String postalCode,
        String city) {
}