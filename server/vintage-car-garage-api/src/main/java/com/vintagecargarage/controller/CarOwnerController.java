package com.vintagecargarage.controller;

import com.vintagecargarage.auth.Auth;
import com.vintagecargarage.dtos.CarOwnerDto;
import com.vintagecargarage.entities.UserRole;
import com.vintagecargarage.service.CarOwnerService;

import io.quarkus.security.credential.TokenCredential;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@Path("/car-owner")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CarOwnerController extends BaseController {

    @Inject
    Auth auth;

    @Inject
    CarOwnerService service;

    @POST
    @Path("/register")
    public Response register(CarOwnerDto dto) {
        service.createCarOwner(dto);
        return Response.status(Status.CREATED).build();
    }

    @RolesAllowed(UserRole.CAR_OWNER)
    @GET
    @Path("/account")
    public Response get() {
        return Response.ok(service.getCarOwner(super.getUserFromJWT())).build();
    }

    @RolesAllowed(UserRole.CAR_OWNER)
    @PATCH
    @Path("/account")
    public Response patch(CarOwnerDto dto) {
        TokenCredential refreshedToken = service.updateCarOwner(super.getUserFromJWT(), dto);
        if (refreshedToken != null) {
            return Response.ok(refreshedToken).build();
        }
        return Response.ok().build();
    }
}
