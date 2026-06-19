package com.vintagecargarage.controller;

import com.vintagecargarage.dtos.StorageOwnerDto;
import com.vintagecargarage.entities.UserRole;
import com.vintagecargarage.service.StorageOwnerService;

import io.quarkus.security.credential.TokenCredential;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.core.UriInfo;

@Path("/storage-owner")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class StorageOwnerController extends BaseController {

    @Inject
    StorageOwnerService service;

    @POST
    @Path("/register")
    public Response register(StorageOwnerDto dto, @Context UriInfo uriInfo) {
        service.createStorageOwner(dto);
        return Response.status(Status.CREATED).build();
    }

    /** UC_E1: Einlagerer suchen Lagerhalter nach Stadt. */
    @RolesAllowed(UserRole.CAR_OWNER)
    @GET
    @Path("/search")
    public Response search(@QueryParam("city") String city) {
        return Response.ok(service.searchByCity(city)).build();
    }

    @RolesAllowed(UserRole.STORAGE_OWNER)
    @GET
    @Path("/account")
    public Response get(String id) {
        return Response.ok(service.getStorageOwner(id)).build();
    }

    @RolesAllowed(UserRole.STORAGE_OWNER)
    @PATCH
    @Path("/account")
    public Response patch(StorageOwnerDto dto) {
        TokenCredential refreshedToken = service.updateStorageOwner(super.getUserFromJWT(), dto);
        if (refreshedToken != null) {
            return Response.ok(refreshedToken).build();
        }
        return Response.ok().build();
    }
}
