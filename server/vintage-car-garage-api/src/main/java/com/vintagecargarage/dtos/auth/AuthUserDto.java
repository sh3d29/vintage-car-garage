package com.vintagecargarage.dtos.auth;

import com.vintagecargarage.dtos.user.UserOutputDto;

public record AuthUserDto(
        String role,
        UserOutputDto user) {
}
