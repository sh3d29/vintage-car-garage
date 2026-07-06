package com.vintagecargarage.auth;

import com.vintagecargarage.dtos.auth.AuthUserDto;

import io.quarkus.security.credential.TokenCredential;

public record AuthResult(TokenCredential token, AuthUserDto user) {

}
