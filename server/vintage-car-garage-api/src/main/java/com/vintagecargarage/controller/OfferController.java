package com.vintagecargarage.controller;

import com.vintagecargarage.dtos.OfferInputDto;
import com.vintagecargarage.dtos.OfferStatusDto;
import com.vintagecargarage.entities.UserRole;
import com.vintagecargarage.service.OfferService;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@Path("/offers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OfferController extends BaseController {

    @Inject
    OfferService service;

    /** UC_L7: der Lagerhalter erstellt ein Angebot. */
    @POST
    @RolesAllowed(UserRole.STORAGE_OWNER)
    public Response create(OfferInputDto dto) {
        return Response.status(Status.CREATED).entity(service.createOffer(super.getUserFromJWT(), dto)).build();
    }

    /** UC_E3: der Einlagerer nimmt das Angebot an oder lehnt es ab. */
    @PATCH
    @Path("/{id}")
    @RolesAllowed(UserRole.CAR_OWNER)
    public Response respond(@PathParam("id") String id, OfferStatusDto dto) {
        return Response.ok(service.respondToOffer(id, dto.accepted())).build();
    }
}
