package com.vintagecargarage.controller;

import org.eclipse.microprofile.jwt.JsonWebToken;

import com.vintagecargarage.dtos.ServiceInputDto;
import com.vintagecargarage.entities.UserRole;
import com.vintagecargarage.service.ServicePortfolioService;

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

@Path("/services")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed(UserRole.STORAGE_OWNER)
public class ServiceController {

    @Inject
    ServicePortfolioService service;

    @Inject
    JsonWebToken jwt;

    @POST
    public Response create(ServiceInputDto dto) {
        return Response.status(Status.CREATED).entity(service.createService(jwt.getSubject(), dto)).build();
    }

    @PATCH
    @Path("/{id}")
    public Response update(@PathParam("id") String id, ServiceInputDto dto) {
        return Response.ok(service.updateService(id, dto)).build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") String id) {
        service.deleteService(id);
        return Response.noContent().build();
    }
}
