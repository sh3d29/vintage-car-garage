package com.vintagecargarage.application.controller;

import com.vintagecargarage.application.dtos.auth.SignInDto;
import com.vintagecargarage.application.services.AuthService;

import io.quarkus.security.credential.TokenCredential;
import jakarta.annotation.security.PermitAll;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/auth")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthController {

    @Inject
    AuthService authService;

    @PermitAll
    @POST
    @Path("/sign-in")
    public Response auth(SignInDto signInDto) {
        TokenCredential token = authService.authenticate(signInDto);
        if (token == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        return Response.ok(token).build();
    }
}