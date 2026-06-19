package com.vintagecargarage.controller;

import com.vintagecargarage.dtos.CarOwnerDto;
import com.vintagecargarage.service.CarOwnerService;

import io.quarkus.security.credential.TokenCredential;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@Path("/car-owner")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CarOwnerController extends BaseController {

    @Inject
    CarOwnerService service;

    @POST
    @Path("/register")
    public Response register(CarOwnerDto dto) {
        service.createCarOwner(dto);
        return Response.status(Status.CREATED).build();
    }

    @RolesAllowed("car-owner")
    @GET
    @Path("/account/{id}")
    public Response get(@PathParam("id") String id) {
        assertSelf(id);
        return Response.ok(service.getCarOwner(id)).build();
    }

    @RolesAllowed("car-owner")
    @PATCH
    @Path("/account/{id}")
    public Response patch(@PathParam("id") String id, CarOwnerDto dto) {
        assertSelf(id);
        TokenCredential refreshedToken = service.updateCarOwner(id, dto);
        if (refreshedToken != null) {
            return Response.ok(refreshedToken).build();
        }
        return Response.ok().build();
    }
}
