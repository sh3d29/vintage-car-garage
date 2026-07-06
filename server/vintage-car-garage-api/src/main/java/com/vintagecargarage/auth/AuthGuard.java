package com.vintagecargarage.auth;

import org.eclipse.microprofile.jwt.JsonWebToken;

import io.quarkus.security.ForbiddenException;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class AuthGuard {

    @Inject
    JsonWebToken jwt;

    public void requireSelf(String ownerId) {
        if (ownerId == null || !ownerId.equals(jwt.getSubject())) {
            throw new ForbiddenException("Zugriff verweigert");
        }
    }
}
