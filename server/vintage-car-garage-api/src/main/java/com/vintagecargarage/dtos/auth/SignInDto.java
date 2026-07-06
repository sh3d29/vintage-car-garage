package com.vintagecargarage.dtos.auth;

public record SignInDto(
        String email,
        String password) {
}