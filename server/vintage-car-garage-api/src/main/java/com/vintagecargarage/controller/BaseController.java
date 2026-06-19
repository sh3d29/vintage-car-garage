package com.vintagecargarage.controller;

import org.eclipse.microprofile.jwt.JsonWebToken;

import jakarta.inject.Inject;
import lombok.experimental.PackagePrivate;

public abstract class BaseController {

    @PackagePrivate
    @Inject
    JsonWebToken jwt;

    protected String getUserFromJWT() {
        return jwt.getSubject();
    }
}
