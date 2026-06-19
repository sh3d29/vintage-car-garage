package com.vintagecargarage.controller;

import com.vintagecargarage.dtos.AppointmentInputDto;
import com.vintagecargarage.entities.UserRole;
import com.vintagecargarage.service.AppointmentService;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@Path("/appointments")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed(UserRole.CAR_OWNER)
public class AppointmentController extends BaseController {

    @Inject
    AppointmentService service;

    @POST
    public Response create(AppointmentInputDto dto) {
        return Response.status(Status.CREATED).entity(service.createAppointment(super.getUserFromJWT(), dto)).build();
    }
}
