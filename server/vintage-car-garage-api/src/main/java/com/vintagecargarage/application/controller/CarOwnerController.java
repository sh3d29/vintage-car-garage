package com.vintagecargarage.application.controller;

import java.util.List;

import com.vintagecargarage.application.dtos.appointment.AppointmentInputDto;
import com.vintagecargarage.application.dtos.appointment.AppointmentOutputDto;
import com.vintagecargarage.application.dtos.car.BookServiceInputDto;
import com.vintagecargarage.application.dtos.car.CarInputDto;
import com.vintagecargarage.application.dtos.car.CarOutputDto;
import com.vintagecargarage.application.dtos.offer.OfferOutputDto;
import com.vintagecargarage.application.dtos.offer.OfferResponseDto;
import com.vintagecargarage.application.dtos.request.RequestInputDto;
import com.vintagecargarage.application.dtos.request.RequestOutputDto;
import com.vintagecargarage.application.dtos.user.CarOwnerInputDto;
import com.vintagecargarage.application.dtos.user.CarOwnerOutputDto;
import com.vintagecargarage.application.dtos.user.StorageOwnerSearchResDto;
import com.vintagecargarage.application.services.CarOwnerService;
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
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/car-owners")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CarOwnerController {

    @Inject
    CarOwnerService carOwnerService;

    @PermitAll
    @POST
    @Path("/sign-up")
    public Response createCarOwner(CarOwnerInputDto dto) {
        carOwnerService.createCarOwner(dto);
        return Response.status(Response.Status.CREATED).build();
    }

    @RolesAllowed(UserRole.CAR_OWNER)
    @GET
    @Path("/search")
    public Response searchByCity(@QueryParam("city") String city) {
        List<StorageOwnerSearchResDto> results = carOwnerService.searchByCity(city);
        return Response.ok(results).build();
    }

    @RolesAllowed(UserRole.CAR_OWNER)
    @GET
    @Path("/{id}")
    public Response getCarOwner(@PathParam("id") String id) {
        CarOwnerOutputDto dto = carOwnerService.getCarOwner(id);
        if (dto == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(dto).build();
    }

    @RolesAllowed(UserRole.CAR_OWNER)
    @PATCH
    @Path("/{id}")
    public Response updateCarOwner(@PathParam("id") String id, CarOwnerInputDto dto) {
        TokenCredential token = carOwnerService.updateCarOwner(id, dto);
        if (token != null) {
            return Response.ok(token).build();
        }
        return Response.noContent().build();
    }

    // --- FAHRZEUGE (UC_E7 / UC_E8) ---

    @RolesAllowed(UserRole.CAR_OWNER)
    @GET
    @Path("/{carOwnerId}/cars")
    public Response getCars(@PathParam("carOwnerId") String carOwnerId) {
        List<CarOutputDto> cars = carOwnerService.getCars(carOwnerId);
        return Response.ok(cars).build();
    }

    @RolesAllowed(UserRole.CAR_OWNER)
    @POST
    @Path("/{carOwnerId}/cars")
    public Response addCar(@PathParam("carOwnerId") String carOwnerId, CarInputDto dto) {
        CarOutputDto car = carOwnerService.createCar(carOwnerId, dto);
        return Response.status(Response.Status.CREATED).entity(car).build();
    }

    @RolesAllowed(UserRole.CAR_OWNER)
    @PATCH
    @Path("/{carOwnerId}/cars/{carId}")
    public Response updateLicensePlate(
            @PathParam("carOwnerId") String carOwnerId,
            @PathParam("carId") String carId,
            CarInputDto dto) {
        CarOutputDto car = carOwnerService.updateLicensePlate(carOwnerId, carId, dto);
        return Response.ok(car).build();
    }

    @RolesAllowed(UserRole.CAR_OWNER)
    @POST
    @Path("/{carOwnerId}/cars/{carId}/booked-services")
    public Response bookOptionalService(
            @PathParam("carOwnerId") String carOwnerId,
            @PathParam("carId") String carId,
            BookServiceInputDto dto) {
        try {
            CarOutputDto car = carOwnerService.bookOptionalService(carOwnerId, carId, dto.storageOwnerId(),
                    dto.serviceId());
            return Response.ok(car).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @RolesAllowed(UserRole.CAR_OWNER)
    @DELETE
    @Path("/{carOwnerId}/cars/{carId}")
    public Response deleteCar(@PathParam("carOwnerId") String carOwnerId, @PathParam("carId") String carId) {
        carOwnerService.deleteCar(carOwnerId, carId);
        return Response.noContent().build();
    }

    // --- STELLPLATZANFRAGEN (UC_E2) ---

    @RolesAllowed(UserRole.CAR_OWNER)
    @POST
    @Path("/{carOwnerId}/requests")
    public Response createRequest(@PathParam("carOwnerId") String carOwnerId, RequestInputDto dto) {
        RequestOutputDto request = carOwnerService.createRequest(carOwnerId, dto);
        return Response.status(Response.Status.CREATED).entity(request).build();
    }

    // --- STELLPLATZANGEBOTE (UC_E3) ---

    @RolesAllowed(UserRole.CAR_OWNER)
    @GET
    @Path("/{carOwnerId}/offers")
    public Response getReceivedOffers(@PathParam("carOwnerId") String carOwnerId) {
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
        try {
            OfferOutputDto offer = carOwnerService.respondToOffer(carOwnerId, offerId, dto.accepted());
            return Response.ok(offer).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }

    // --- TERMINE (UC_E5) ---

    @RolesAllowed(UserRole.CAR_OWNER)
    @GET
    @Path("/{carOwnerId}/appointments")
    public Response getAppointments(@PathParam("carOwnerId") String carOwnerId) {
        List<AppointmentOutputDto> appointments = carOwnerService.getAllAppointments(carOwnerId);
        return Response.ok(appointments).build();
    }

    @RolesAllowed(UserRole.CAR_OWNER)
    @POST
    @Path("/{carOwnerId}/appointments")
    public Response createAppointment(@PathParam("carOwnerId") String carOwnerId, AppointmentInputDto dto) {
        try {
            AppointmentOutputDto appointment = carOwnerService.createAppointment(carOwnerId, dto);
            return Response.status(Response.Status.CREATED).entity(appointment).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @RolesAllowed(UserRole.CAR_OWNER)
    @DELETE
    @Path("/{carOwnerId}/appointments/{appointmentId}")
    public Response deleteAppointment(
            @PathParam("carOwnerId") String carOwnerId,
            @PathParam("appointmentId") String appointmentId) {
        carOwnerService.deleteAppointment(carOwnerId, appointmentId);
        return Response.noContent().build();
    }
}