package com.vintagecargarage.controller;

import org.eclipse.microprofile.jwt.JsonWebToken;

import com.vintagecargarage.dtos.CarInputDto;
import com.vintagecargarage.dtos.CarStatusDto;
import com.vintagecargarage.entities.UserRole;
import com.vintagecargarage.service.CarService;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@Path("/cars")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed(UserRole.CAR_OWNER)
public class CarController {

    @Inject
    CarService service;

    @Inject
    JsonWebToken jwt;

    @POST
    public Response create(CarInputDto dto) {
        return Response.status(Status.CREATED).entity(service.createCar(jwt.getSubject(), dto)).build();
    }

    @PATCH
    @Path("/{id}")
    public Response updateLicensePlate(@PathParam("id") String id, CarInputDto dto) {
        return Response.ok(service.updateLicensePlate(id, dto)).build();
    }

    @PATCH
    @Path("/{id}/status")
    public Response updateStatus(@PathParam("id") String id, CarStatusDto dto) {
        return Response.ok(service.updateStatus(id, dto)).build();
    }

    @POST
    @Path("/{id}/services/{serviceId}")
    public Response bookService(@PathParam("id") String id, @PathParam("serviceId") String serviceId) {
        return Response.ok(service.bookService(id, serviceId)).build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") String id) {
        service.deleteCar(id);
        return Response.noContent().build();
    }
}
