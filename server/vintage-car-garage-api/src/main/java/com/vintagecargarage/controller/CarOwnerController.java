package com.vintagecargarage.controller;

import java.util.List;
import java.util.Optional;

import com.vintagecargarage.auth.AuthGuard;
import com.vintagecargarage.auth.UserRole;
import com.vintagecargarage.dtos.appointment.AppointmentBookingInputDto;
import com.vintagecargarage.dtos.appointment.AppointmentOutputDto;
import com.vintagecargarage.dtos.car.CarInputDto;
import com.vintagecargarage.dtos.car.CarOutputDto;
import com.vintagecargarage.dtos.offer.OfferOutputDto;
import com.vintagecargarage.dtos.offer.OfferResponseDto;
import com.vintagecargarage.dtos.request.RequestInputDto;
import com.vintagecargarage.dtos.request.RequestOutputDto;
import com.vintagecargarage.dtos.service.ServiceOutputDto;
import com.vintagecargarage.dtos.user.CarOwnerInputDto;
import com.vintagecargarage.dtos.user.CarOwnerOutputDto;
import com.vintagecargarage.dtos.user.StorageOwnerSearchResDto;
import com.vintagecargarage.services.CarOwnerService;

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
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Systemgrenze für die Anwendungsfälle des CarOwner unter /car-owners:
 * nimmt HTTP-Anfragen entgegen, sichert den Zugriff (AuthGuard/RolesAllowed)
 * und
 * delegiert an den CarOwnerService.
 */
@Path("/car-owners")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CarOwnerController {

    @Inject
    CarOwnerService carOwnerService;

    @Inject
    AuthGuard authGuard;

    // --- Benutzer registrieren ---

    @PermitAll
    @POST
    @Path("/sign-up")
    public Response createCarOwner(CarOwnerInputDto dto) {
        carOwnerService.createCarOwner(dto);
        return Response.status(Response.Status.CREATED).build();
    }

    // --- Benutzer verwalten ---

    @RolesAllowed(UserRole.CAR_OWNER)
    @GET
    @Path("/{id}")
    public Response getCarOwner(@PathParam("id") String id) {
        authGuard.requireSelf(id);
        CarOwnerOutputDto dto = carOwnerService.getCarOwner(id);
        return Response.ok(dto).build();
    }

    @RolesAllowed(UserRole.CAR_OWNER)
    @PATCH
    @Path("/{id}")
    public Response updateCarOwner(@PathParam("id") String id, CarOwnerInputDto dto) {
        authGuard.requireSelf(id);
        Optional<TokenCredential> token = carOwnerService.updateCarOwner(id, dto);
        return token.map(t -> Response.ok(t).build())
                .orElse(Response.noContent().build());
    }

    // --- Stellplatz suchen ---

    @RolesAllowed(UserRole.CAR_OWNER)
    @GET
    @Path("/search")
    public Response searchByCity(@QueryParam("city") String city) {
        List<StorageOwnerSearchResDto> results = carOwnerService.searchByCity(city);
        return Response.ok(results).build();
    }

    // Fahrzeug registrieren

    @RolesAllowed(UserRole.CAR_OWNER)
    @POST
    @Path("/{carOwnerId}/cars")
    public Response createCar(@PathParam("carOwnerId") String carOwnerId, CarInputDto dto) {
        authGuard.requireSelf(carOwnerId);
        CarOutputDto car = carOwnerService.createCar(carOwnerId, dto);
        return Response.status(Response.Status.CREATED).entity(car).build();
    }

    // --- Fahrzeuge verwalten ---

    @RolesAllowed(UserRole.CAR_OWNER)
    @GET
    @Path("/{carOwnerId}/cars")
    public Response getAllCars(@PathParam("carOwnerId") String carOwnerId) {
        authGuard.requireSelf(carOwnerId);
        List<CarOutputDto> cars = carOwnerService.getAllCars(carOwnerId);
        return Response.ok(cars).build();
    }

    @RolesAllowed(UserRole.CAR_OWNER)
    @PATCH
    @Path("/{carOwnerId}/cars/{carId}")
    public Response updateCar(
            @PathParam("carOwnerId") String carOwnerId,
            @PathParam("carId") String carId,
            CarInputDto dto) {
        authGuard.requireSelf(carOwnerId);
        CarOutputDto car = carOwnerService.updateCar(carOwnerId, carId, dto);
        return Response.ok(car).build();
    }

    @RolesAllowed(UserRole.CAR_OWNER)
    @DELETE
    @Path("/{carOwnerId}/cars/{carId}")
    public Response removeCar(@PathParam("carOwnerId") String carOwnerId, @PathParam("carId") String carId) {
        authGuard.requireSelf(carOwnerId);
        carOwnerService.removeCar(carOwnerId, carId);
        return Response.noContent().build();
    }

    // --- Optionale Lagerservices buchen ---

    @RolesAllowed(UserRole.CAR_OWNER)
    @POST
    @Path("/{carOwnerId}/cars/{carId}/booked-services/{serviceId}")
    public Response bookOptionalService(
            @PathParam("carOwnerId") String carOwnerId,
            @PathParam("carId") String carId,
            @PathParam("serviceId") String serviceId) {
        authGuard.requireSelf(carOwnerId);
        CarOutputDto car = carOwnerService.bookOptionalService(carOwnerId, carId, serviceId);
        return Response.ok(car).build();
    }

    @RolesAllowed(UserRole.CAR_OWNER)
    @DELETE
    @Path("/{carOwnerId}/cars/{carId}/booked-services/{serviceId}")
    public Response removeBookedService(
            @PathParam("carOwnerId") String carOwnerId,
            @PathParam("carId") String carId,
            @PathParam("serviceId") String serviceId) {
        authGuard.requireSelf(carOwnerId);
        carOwnerService.removeBookedService(carOwnerId, carId, serviceId);
        return Response.noContent().build();
    }

    @RolesAllowed(UserRole.CAR_OWNER)
    @GET
    @Path("/{carOwnerId}/storage-owners/{storageOwnerId}/optional-services")
    public Response getOptionalServices(
            @PathParam("carOwnerId") String carOwnerId,
            @PathParam("storageOwnerId") String storageOwnerId) {
        authGuard.requireSelf(carOwnerId);
        List<ServiceOutputDto> services = carOwnerService.getOptionalServices(carOwnerId, storageOwnerId);
        return Response.ok(services).build();
    }

    // --- Stellplatzangebot anfordern ---

    @RolesAllowed(UserRole.CAR_OWNER)
    @POST
    @Path("/{carOwnerId}/requests")
    public Response createRequest(@PathParam("carOwnerId") String carOwnerId, RequestInputDto dto) {
        authGuard.requireSelf(carOwnerId);
        RequestOutputDto request = carOwnerService.createRequest(carOwnerId, dto);
        return Response.status(Response.Status.CREATED).entity(request).build();
    }

    @RolesAllowed(UserRole.CAR_OWNER)
    @GET
    @Path("/{carOwnerId}/requests")
    public Response getAllRequests(@PathParam("carOwnerId") String carOwnerId) {
        authGuard.requireSelf(carOwnerId);
        List<RequestOutputDto> requests = carOwnerService.getAllRequests(carOwnerId);
        return Response.ok(requests).build();
    }

    @RolesAllowed(UserRole.CAR_OWNER)
    @DELETE
    @Path("/{carOwnerId}/requests/{requestId}")
    public Response deleteRequest(
            @PathParam("carOwnerId") String carOwnerId,
            @PathParam("requestId") String requestId) {
        authGuard.requireSelf(carOwnerId);
        carOwnerService.deleteRequest(carOwnerId, requestId);
        return Response.noContent().build();
    }

    // --- Stellplatzangebote verwalten ---

    @RolesAllowed(UserRole.CAR_OWNER)
    @GET
    @Path("/{carOwnerId}/offers")
    public Response getReceivedOffers(@PathParam("carOwnerId") String carOwnerId) {
        authGuard.requireSelf(carOwnerId);
        List<OfferOutputDto> offers = carOwnerService.getReceivedOffers(carOwnerId);
        return Response.ok(offers).build();
    }

    @RolesAllowed(UserRole.CAR_OWNER)
    @PATCH
    @Path("/{carOwnerId}/offers/{offerId}")
    public Response respondToOffer(
            @PathParam("carOwnerId") String carOwnerId,
            @PathParam("offerId") String offerId,
            OfferResponseDto dto) {
        authGuard.requireSelf(carOwnerId);
        OfferOutputDto offer = carOwnerService.respondToOffer(carOwnerId, offerId, dto.accepted());
        return Response.ok(offer).build();
    }

    // --- Ein- & Auslagerungstermin koordinieren ---

    @RolesAllowed(UserRole.CAR_OWNER)
    @POST
    @Path("/{carOwnerId}/storage-owners/{storageOwnerId}/appointments")
    public Response bookAppointment(
            @PathParam("carOwnerId") String carOwnerId,
            @PathParam("storageOwnerId") String storageOwnerId,
            AppointmentBookingInputDto dto) {
        authGuard.requireSelf(carOwnerId);
        AppointmentOutputDto appointment = carOwnerService.bookAppointment(carOwnerId, storageOwnerId, dto);
        return Response.status(Response.Status.CREATED).entity(appointment).build();
    }

    @RolesAllowed(UserRole.CAR_OWNER)
    @GET
    @Path("/{carOwnerId}/storage-owners/{storageOwnerId}/available-appointments")
    public Response getAvailableAppointments(
            @PathParam("carOwnerId") String carOwnerId,
            @PathParam("storageOwnerId") String storageOwnerId) {
        authGuard.requireSelf(carOwnerId);
        List<AppointmentOutputDto> appointments = carOwnerService.getAvailableAppointments(carOwnerId, storageOwnerId);
        return Response.ok(appointments).build();
    }

    @RolesAllowed(UserRole.CAR_OWNER)
    @GET
    @Path("/{carOwnerId}/appointments")
    public Response getAllBookedAppointments(@PathParam("carOwnerId") String carOwnerId) {
        authGuard.requireSelf(carOwnerId);
        List<AppointmentOutputDto> appointments = carOwnerService.getAllBookedAppointments(carOwnerId);
        return Response.ok(appointments).build();
    }

    @RolesAllowed(UserRole.CAR_OWNER)
    @DELETE
    @Path("/{carOwnerId}/appointments/{appointmentId}")
    public Response cancelAppointment(
            @PathParam("carOwnerId") String carOwnerId,
            @PathParam("appointmentId") String appointmentId) {
        authGuard.requireSelf(carOwnerId);
        carOwnerService.cancelAppointment(carOwnerId, appointmentId);
        return Response.noContent().build();
    }
}
