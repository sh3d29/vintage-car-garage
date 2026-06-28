package com.vintagecargarage.application.controller;

import java.util.List;

import com.vintagecargarage.application.dtos.car.CarOutputDto;
import com.vintagecargarage.application.dtos.car.CarStatusDto;
import com.vintagecargarage.application.dtos.offer.OfferInputDto;
import com.vintagecargarage.application.dtos.offer.OfferOutputDto;
import com.vintagecargarage.application.dtos.request.RequestOutputDto;
import com.vintagecargarage.application.dtos.service.ServiceInputDto;
import com.vintagecargarage.application.dtos.service.ServiceOutputDto;
import com.vintagecargarage.application.dtos.user.StorageOwnerInputDto;
import com.vintagecargarage.application.dtos.user.StorageOwnerOutputDto;
import com.vintagecargarage.application.services.StorageOwnerService;
import com.vintagecargarage.infrastructure.auth.UserRole;

import io.quarkus.security.credential.TokenCredential;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/storage-owners")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class StorageOwnerController {

    @Inject
    StorageOwnerService storageOwnerService;

    // --- LAGERHALTER KONTO-VERWALTUNG ---

    @PermitAll
    @POST
    @Path("/sign-up")
    public Response createStorageOwner(StorageOwnerInputDto dto) {
        storageOwnerService.createStorageOwner(dto);
        return Response.status(Response.Status.CREATED).build();
    }

    @RolesAllowed(UserRole.STORAGE_OWNER)
    @GET
    @Path("/{id}")
    public Response getStorageOwner(@PathParam("id") String id) {
        StorageOwnerOutputDto storageOwner = storageOwnerService.getStorageOwner(id);
        if (storageOwner == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(storageOwner).build();
    }

    @RolesAllowed(UserRole.STORAGE_OWNER)
    @PATCH
    @Path("/{id}")
    public Response updateStorageOwner(@PathParam("id") String id, StorageOwnerInputDto dto) {
        TokenCredential token = storageOwnerService.updateStorageOwner(id, dto);
        if (token != null) {
            // Falls das Passwort geändert wurde, wird der neue JWT-Token zurückgegeben
            return Response.ok(token).build();
        }
        return Response.noContent().build();
    }

    // --- SERVICEPORTFOLIO (UC_L1 / UC_L6) ---

    @RolesAllowed(UserRole.STORAGE_OWNER)
    @GET
    @Path("/{storageOwnerId}/services")
    public Response getServices(@PathParam("storageOwnerId") String storageOwnerId) {
        List<ServiceOutputDto> services = storageOwnerService.getServices(storageOwnerId);
        return Response.ok(services).build();
    }

    @RolesAllowed(UserRole.CAR_OWNER)
    @GET
    @Path("/{storageOwnerId}/optional-services")
    public Response getOptionalServices(@PathParam("storageOwnerId") String storageOwnerId) {
        List<ServiceOutputDto> services = storageOwnerService.getOptionalServices(storageOwnerId);
        return Response.ok(services).build();
    }

    @RolesAllowed(UserRole.STORAGE_OWNER)
    @POST
    @Path("/{storageOwnerId}/services")
    public Response createService(@PathParam("storageOwnerId") String storageOwnerId, ServiceInputDto dto) {
        ServiceOutputDto createdService = storageOwnerService.createService(storageOwnerId, dto);
        return Response.status(Response.Status.CREATED).entity(createdService).build();
    }

    @RolesAllowed(UserRole.STORAGE_OWNER)
    @PUT
    @Path("/{storageOwnerId}/services/{serviceId}")
    public Response updateService(
            @PathParam("storageOwnerId") String storageOwnerId,
            @PathParam("serviceId") String serviceId,
            ServiceInputDto dto) {
        ServiceOutputDto updatedService = storageOwnerService.updateService(storageOwnerId, serviceId, dto);
        return Response.ok(updatedService).build();
    }

    @RolesAllowed(UserRole.STORAGE_OWNER)
    @DELETE
    @Path("/{storageOwnerId}/services/{serviceId}")
    public Response deleteService(
            @PathParam("storageOwnerId") String storageOwnerId,
            @PathParam("serviceId") String serviceId) {
        storageOwnerService.deleteService(storageOwnerId, serviceId);
        return Response.noContent().build();
    }

    // --- STELLPLATZANFRAGEN ---

    @RolesAllowed(UserRole.STORAGE_OWNER)
    @GET
    @Path("/{storageOwnerId}/incoming-requests")
    public Response getIncomingRequests(@PathParam("storageOwnerId") String storageOwnerId) {
        List<RequestOutputDto> requests = storageOwnerService.getIncomingRequests(storageOwnerId);
        return Response.ok(requests).build();
    }

    // --- ANGEBOTE ABGEBEN & VERWALTEN (UC_L7) ---

    @RolesAllowed(UserRole.STORAGE_OWNER)
    @GET
    @Path("/{storageOwnerId}/offers")
    public Response getAllOffers(@PathParam("storageOwnerId") String storageOwnerId) {
        List<OfferOutputDto> offers = storageOwnerService.getAllOffers(storageOwnerId);
        return Response.ok(offers).build();
    }

    @RolesAllowed(UserRole.STORAGE_OWNER)
    @POST
    @Path("/{storageOwnerId}/requests/{requestId}/offers")
    public Response createOffer(
            @PathParam("storageOwnerId") String storageOwnerId,
            @PathParam("requestId") String requestId,
            OfferInputDto dto) {
        OfferOutputDto offer = storageOwnerService.createOffer(storageOwnerId, requestId, dto);
        return Response.status(Response.Status.CREATED).entity(offer).build();
    }

    @RolesAllowed(UserRole.STORAGE_OWNER)
    @DELETE
    @Path("/{storageOwnerId}/offers/{offerId}")
    public Response deleteOffer(
            @PathParam("storageOwnerId") String storageOwnerId,
            @PathParam("offerId") String offerId) {
        storageOwnerService.deleteOffer(storageOwnerId, offerId);
        return Response.noContent().build();
    }

    // --- FAHRZEUGÜBERSICHT & FAHRZEUGSTATUS AKTUALISIEREN (UC_L3) ---

    @RolesAllowed(UserRole.STORAGE_OWNER)
    @GET
    @Path("/{storageOwnerId}/stored-cars")
    public Response getStoredCars(@PathParam("storageOwnerId") String storageOwnerId) {
        List<CarOutputDto> cars = storageOwnerService.getStoredCars(storageOwnerId);
        return Response.ok(cars).build();
    }

    @RolesAllowed(UserRole.STORAGE_OWNER)
    @PUT
    @Path("/{storageOwnerId}/stored-cars/{carId}/status")
    public Response updateCarStatus(
            @PathParam("storageOwnerId") String storageOwnerId,
            @PathParam("carId") String carId,
            CarStatusDto dto) {
        try {
            CarOutputDto updatedCar = storageOwnerService.updateCarStatus(storageOwnerId, carId, dto);
            return Response.ok(updatedCar).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }
}