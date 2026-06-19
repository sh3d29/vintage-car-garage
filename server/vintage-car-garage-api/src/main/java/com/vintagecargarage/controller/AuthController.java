package com.vintagecargarage.controller;

import com.vintagecargarage.dtos.LoginDto;
import com.vintagecargarage.service.AuthService;

import io.quarkus.security.credential.TokenCredential;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AuthController extends BaseController {

    @Inject
    AuthService service;

    @POST
    @Path("/login/{role}")
    public Response login(@PathParam("role") String role, LoginDto dto) {
        TokenCredential token = service.authenticate(role, dto);

        if ((token == null)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        return Response.ok(token).build();
    }
}