package com.vintagecargarage.dtos.user;

public record CarOwnerInputDto(
                String email,
                String password,
                String firstName,
                String lastName) {
}
