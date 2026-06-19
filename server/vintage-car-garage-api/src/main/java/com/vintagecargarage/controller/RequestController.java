package com.vintagecargarage.controller;

import com.vintagecargarage.dtos.RequestInputDto;
import com.vintagecargarage.entities.UserRole;
import com.vintagecargarage.service.RequestService;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@Path("/requests")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed(UserRole.CAR_OWNER)
public class RequestController extends BaseController {

    @Inject
    RequestService service;

    @POST
    public Response create(RequestInputDto dto) {
        return Response.status(Status.CREATED).entity(service.createRequest(super.getUserFromJWT(), dto)).build();
    }
}
