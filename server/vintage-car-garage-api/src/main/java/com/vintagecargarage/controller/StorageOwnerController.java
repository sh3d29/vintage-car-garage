package com.vintagecargarage.controller;

import java.util.List;

import com.vintagecargarage.dtos.appointment.AppointmentSlotInputDto;
import com.vintagecargarage.dtos.appointment.AppointmentOutputDto;
import com.vintagecargarage.dtos.car.CarOutputDto;
import com.vintagecargarage.dtos.car.CarStatusDto;
import com.vintagecargarage.dtos.offer.OfferInputDto;
import com.vintagecargarage.dtos.offer.OfferOutputDto;
import com.vintagecargarage.dtos.request.RequestOutputDto;
import com.vintagecargarage.dtos.service.ServiceInputDto;
import com.vintagecargarage.dtos.service.ServiceOutputDto;
import com.vintagecargarage.dtos.user.StorageOwnerInputDto;
import com.vintagecargarage.dtos.user.StorageOwnerOutputDto;
import com.vintagecargarage.services.StorageOwnerService;
import com.vintagecargarage.auth.AuthGuard;
import com.vintagecargarage.auth.UserRole;

import io.quarkus.security.credential.TokenCredential;
import jakarta.annotation.security.PermitAll;

import java.util.Optional;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
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

    @Inject
    AuthGuard authGuard;

    // --- Benutzer registrieren ---

    @PermitAll
    @POST
    @Path("/sign-up")
    public Response createStorageOwner(StorageOwnerInputDto dto) {
        storageOwnerService.createStorageOwner(dto);
        return Response.status(Response.Status.CREATED).build();
    }

    // --- Benutzer verwalten ---

    @RolesAllowed(UserRole.STORAGE_OWNER)
    @GET
    @Path("/{id}")
    public Response getStorageOwner(@PathParam("id") String id) {
        authGuard.requireSelf(id);
        StorageOwnerOutputDto storageOwner = storageOwnerService.getStorageOwner(id);
        return Response.ok(storageOwner).build();
    }

    @RolesAllowed(UserRole.STORAGE_OWNER)
    @PATCH
    @Path("/{id}")
    public Response updateStorageOwner(@PathParam("id") String id, StorageOwnerInputDto dto) {
        authGuard.requireSelf(id);
        Optional<TokenCredential> token = storageOwnerService.updateStorageOwner(id, dto);
        return token.map(t -> Response.ok(t).build())
                .orElse(Response.noContent().build());
    }

    // --- Serviceportfolio verwalten & Optionale Services verwalten ---

    @RolesAllowed(UserRole.STORAGE_OWNER)
    @POST
    @Path("/{storageOwnerId}/services")
    public Response createService(@PathParam("storageOwnerId") String storageOwnerId, ServiceInputDto dto) {
        authGuard.requireSelf(storageOwnerId);
        ServiceOutputDto service = storageOwnerService.createService(storageOwnerId, dto);
        return Response.status(Response.Status.CREATED).entity(service).build();
    }

    @RolesAllowed(UserRole.STORAGE_OWNER)
    @GET
    @Path("/{storageOwnerId}/services")
    public Response getAllServices(@PathParam("storageOwnerId") String storageOwnerId) {
        authGuard.requireSelf(storageOwnerId);
        List<ServiceOutputDto> services = storageOwnerService.getAllServices(storageOwnerId);
        return Response.ok(services).build();
    }

    @RolesAllowed(UserRole.STORAGE_OWNER)
    @PATCH
    @Path("/{storageOwnerId}/services/{serviceId}")
    public Response updateService(
            @PathParam("storageOwnerId") String storageOwnerId,
            @PathParam("serviceId") String serviceId,
            ServiceInputDto dto) {
        authGuard.requireSelf(storageOwnerId);
        ServiceOutputDto service = storageOwnerService.updateService(storageOwnerId, serviceId, dto);
        return Response.ok(service).build();
    }

    @RolesAllowed(UserRole.STORAGE_OWNER)
    @DELETE
    @Path("/{storageOwnerId}/services/{serviceId}")
    public Response deleteService(
            @PathParam("storageOwnerId") String storageOwnerId,
            @PathParam("serviceId") String serviceId) {
        authGuard.requireSelf(storageOwnerId);
        storageOwnerService.deleteService(storageOwnerId, serviceId);
        return Response.noContent().build();
    }

    // --- Fahrzeugstatus aktualisieren ---

    @RolesAllowed(UserRole.STORAGE_OWNER)
    @GET
    @Path("/{storageOwnerId}/stored-cars")
    public Response getStoredCars(@PathParam("storageOwnerId") String storageOwnerId) {
        authGuard.requireSelf(storageOwnerId);
        List<CarOutputDto> cars = storageOwnerService.getStoredCars(storageOwnerId);
        return Response.ok(cars).build();
    }

    @RolesAllowed(UserRole.STORAGE_OWNER)
    @PATCH
    @Path("/{storageOwnerId}/stored-cars/{carId}/status")
    public Response updateStoredCarStatus(
            @PathParam("storageOwnerId") String storageOwnerId,
            @PathParam("carId") String carId,
            CarStatusDto dto) {
        authGuard.requireSelf(storageOwnerId);
        CarOutputDto car = storageOwnerService.updateStoredCarStatus(storageOwnerId, carId, dto);
        return Response.ok(car).build();
    }

    // --- Stellplatzanfragen verwalten ---

    @RolesAllowed(UserRole.STORAGE_OWNER)
    @POST
    @Path("/{storageOwnerId}/requests/{requestId}/offers")
    public Response createOffer(
            @PathParam("storageOwnerId") String storageOwnerId,
            @PathParam("requestId") String requestId,
            OfferInputDto dto) {
        authGuard.requireSelf(storageOwnerId);
        OfferOutputDto offer = storageOwnerService.createOffer(storageOwnerId, requestId, dto);
        return Response.status(Response.Status.CREATED).entity(offer).build();
    }

    @RolesAllowed(UserRole.STORAGE_OWNER)
    @GET
    @Path("/{storageOwnerId}/incoming-requests")
    public Response getIncomingRequests(@PathParam("storageOwnerId") String storageOwnerId) {
        authGuard.requireSelf(storageOwnerId);
        List<RequestOutputDto> requests = storageOwnerService.getIncomingRequests(storageOwnerId);
        return Response.ok(requests).build();
    }

    @RolesAllowed(UserRole.STORAGE_OWNER)
    @GET
    @Path("/{storageOwnerId}/offers")
    public Response getAllOffers(@PathParam("storageOwnerId") String storageOwnerId) {
        authGuard.requireSelf(storageOwnerId);
        List<OfferOutputDto> offers = storageOwnerService.getAllOffers(storageOwnerId);
        return Response.ok(offers).build();
    }

    @RolesAllowed(UserRole.STORAGE_OWNER)
    @DELETE
    @Path("/{storageOwnerId}/offers/{offerId}")
    public Response deleteOffer(
            @PathParam("storageOwnerId") String storageOwnerId,
            @PathParam("offerId") String offerId) {
        authGuard.requireSelf(storageOwnerId);
        storageOwnerService.deleteOffer(storageOwnerId, offerId);
        return Response.noContent().build();
    }

    // --- Ein- & Auslagerungstermin koordinieren ---

    @RolesAllowed(UserRole.STORAGE_OWNER)
    @POST
    @Path("/{storageOwnerId}/appointments")
    public Response createAppointment(
            @PathParam("storageOwnerId") String storageOwnerId,
            AppointmentSlotInputDto dto) {
        authGuard.requireSelf(storageOwnerId);
        AppointmentOutputDto appointment = storageOwnerService.createAppointment(storageOwnerId, dto);
        return Response.status(Response.Status.CREATED).entity(appointment).build();
    }

    @RolesAllowed(UserRole.STORAGE_OWNER)
    @GET
    @Path("/{storageOwnerId}/appointments")
    public Response getAllAppointments(@PathParam("storageOwnerId") String storageOwnerId) {
        authGuard.requireSelf(storageOwnerId);
        List<AppointmentOutputDto> appointments = storageOwnerService.getAllAppointments(storageOwnerId);
        return Response.ok(appointments).build();
    }

    @RolesAllowed(UserRole.STORAGE_OWNER)
    @PATCH
    @Path("/{storageOwnerId}/appointments/{appointmentId}/complete")
    public Response completeAppointment(
            @PathParam("storageOwnerId") String storageOwnerId,
            @PathParam("appointmentId") String appointmentId) {
        authGuard.requireSelf(storageOwnerId);
        AppointmentOutputDto appointment = storageOwnerService.completeAppointment(storageOwnerId, appointmentId);
        return Response.ok(appointment).build();
    }

    @RolesAllowed(UserRole.STORAGE_OWNER)
    @DELETE
    @Path("/{storageOwnerId}/appointments/{appointmentId}")
    public Response deleteAppointment(
            @PathParam("storageOwnerId") String storageOwnerId,
            @PathParam("appointmentId") String appointmentId) {
        authGuard.requireSelf(storageOwnerId);
        storageOwnerService.deleteAppointment(storageOwnerId, appointmentId);
        return Response.noContent().build();
    }
}
