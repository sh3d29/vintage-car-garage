package com.vintagecargarage.application.dtos.auth;

public record SignInDto(
        String email,
        String password) {
}