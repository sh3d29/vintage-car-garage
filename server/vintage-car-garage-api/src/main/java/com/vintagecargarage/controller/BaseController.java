package com.vintagecargarage.controller;

import org.eclipse.microprofile.jwt.JsonWebToken;

import jakarta.inject.Inject;
import jakarta.ws.rs.ForbiddenException;

/**
 * Gemeinsame Basis für authentifizierte Controller. Stellt das Token bereit und
 * kapselt die IDOR-Prüfung. Bewusst nicht generisch: die Injection einer
 * Typvariablen ({@code @Inject T}) lässt sich von ArC nicht zuverlässig auflösen,
 * daher injiziert jeder Controller seinen konkreten Service selbst.
 */
public abstract class BaseController {

    @Inject
    JsonWebToken jwt;

    /**
     * Schützt vor IDOR: stellt sicher, dass die adressierte Ressourcen-Id mit dem
     * authentifizierten Benutzer (Token-Subject) übereinstimmt.
     */
    protected void assertSelf(String id) {
        if (id == null || !id.equals(jwt.getSubject())) {
            throw new ForbiddenException();
        }
    }
}
