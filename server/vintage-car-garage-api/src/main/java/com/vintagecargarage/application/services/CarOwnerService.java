package com.vintagecargarage.application.services;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.vintagecargarage.application.dtos.appointment.AppointmentInputDto;
import com.vintagecargarage.application.dtos.appointment.AppointmentOutputDto;
import com.vintagecargarage.application.dtos.car.CarInputDto;
import com.vintagecargarage.application.dtos.car.CarOutputDto;
import com.vintagecargarage.application.dtos.offer.OfferOutputDto;
import com.vintagecargarage.application.dtos.request.RequestInputDto;
import com.vintagecargarage.application.dtos.request.RequestOutputDto;
import com.vintagecargarage.application.dtos.user.CarOwnerInputDto;
import com.vintagecargarage.application.dtos.user.CarOwnerOutputDto;
import com.vintagecargarage.application.dtos.user.LocationDto;
import com.vintagecargarage.application.dtos.user.StorageDto;
import com.vintagecargarage.application.dtos.user.StorageOwnerSearchResDto;
import com.vintagecargarage.domain.entities.Appointment;
import com.vintagecargarage.domain.entities.Car;
import com.vintagecargarage.domain.entities.CarOwner;
import com.vintagecargarage.domain.entities.Offer;
import com.vintagecargarage.domain.entities.Request;
import com.vintagecargarage.domain.entities.Service;
import com.vintagecargarage.domain.entities.StorageOwner;
import com.vintagecargarage.domain.repositories.CarOwnerRepository;
import com.vintagecargarage.domain.repositories.StorageOwnerRepository;
import com.vintagecargarage.exceptions.ConflictException;
import com.vintagecargarage.infrastructure.auth.Auth;
import com.vintagecargarage.infrastructure.auth.UserRole;

import io.quarkus.security.credential.TokenCredential;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class CarOwnerService {

    @Inject
    Auth auth;

    @Inject
    CarOwnerRepository carOwnerRepository;

    @Inject
    StorageOwnerRepository storageOwnerRepository;

    // Einlagerer registrieren
    @Transactional
    public void createCarOwner(CarOwnerInputDto dto) {
        if (carOwnerRepository.isEmailTaken(dto.email())) {
            throw new ConflictException("E-Mail-Adresse ist bereits registriert");
        }

        CarOwner newCo = CarOwner.createCarOwner(
                dto.email(),
                auth.hashPassword(dto.password()),
                dto.firstName(),
                dto.lastName());

        carOwnerRepository.persist(newCo);
    }

    // Einlagerer verwalten
    @Transactional
    public CarOwnerOutputDto getCarOwner(String id) {
        CarOwner co = carOwnerRepository.findById(id);
        if (co == null)
            return null;

        return new CarOwnerOutputDto(
                co.getId(),
                co.getEmail(),
                co.getFirstName(),
                co.getLastName());
    }

    @Transactional
    public TokenCredential updateCarOwner(String id, CarOwnerInputDto dto) {
        CarOwner co = carOwnerRepository.findById(id);

        if (dto.email() != null && !dto.email().equals(co.getEmail()) && carOwnerRepository.isEmailTaken(dto.email())) {
            throw new ConflictException("E-Mail-Adresse ist bereits registriert");
        }

        boolean passwordChanged = dto.password() != null && !auth.verifyPassword(dto.password(), co.getPassword());

        co.updateCarOwner(dto.email(), passwordChanged ? auth.hashPassword(dto.password()) : null, dto.firstName(),
                dto.lastName());

        if (passwordChanged) {
            return auth.generateAuthToken(co.getId(), UserRole.CAR_OWNER, co.getPasswordVersion());
        }

        return null;
    }

    // Stellplatz suchen
    @Transactional
    public List<StorageOwnerSearchResDto> searchByCity(String city) {
        return storageOwnerRepository.findByCity(city).stream()
                .map(so -> {
                    LocationDto locationDto = null;
                    if (so.getLocation() != null) {
                        locationDto = new LocationDto(
                                so.getLocation().getStreet(),
                                so.getLocation().getHouseNumber(),
                                so.getLocation().getPostalCode(),
                                so.getLocation().getCity());
                    }

                    StorageDto storageDto = null;
                    if (so.getStorage() != null) {
                        List<String> conditions = so.getStorage().getConditions() != null
                                ? List.copyOf(so.getStorage().getConditions())
                                : List.of();

                        storageDto = new StorageDto(
                                conditions,
                                so.getStorage().getCapacity());
                    }

                    return new StorageOwnerSearchResDto(
                            so.getId(),
                            so.getCompanyName(),
                            so.getCosts(),
                            so.getServiceTime(),
                            locationDto,
                            storageDto,
                            so.getCarManufacturers() != null ? Set.copyOf(so.getCarManufacturers()) : Set.of());
                })
                .toList();
    }

    // Stellplatzangebot anfordern
    @Transactional
    public RequestOutputDto createRequest(String carOwnerId, RequestInputDto dto) {
        CarOwner carOwner = carOwnerRepository.findById(carOwnerId);
        StorageOwner storageOwner = storageOwnerRepository.findById(dto.storageOwnerId());

        Request request = carOwner.createRequest(storageOwner, dto.description());
        carOwnerRepository.flush();

        return new RequestOutputDto(
                request.getId(),
                request.getType().name(),
                request.getDescription(),
                request.getStorageOwner().getId(),
                carOwnerId);
    }

    // Stellplatzangebote verwalten
    @Transactional
    public List<OfferOutputDto> getReceivedOffers(String carOwnerId) {
        CarOwner owner = carOwnerRepository.findById(carOwnerId);
        return owner.getReceivedOffers().stream()
                .map(offer -> new OfferOutputDto(
                        offer.getId(),
                        offer.getDescription(),
                        offer.getCosts(),
                        offer.getAccepted(),
                        offer.getRequest().getId(),
                        offer.getStorageOwner().getId()))
                .toList();
    }

    @Transactional
    public OfferOutputDto respondToOffer(String carOwnerId, String offerId, Boolean accepted) {
        CarOwner owner = carOwnerRepository.findById(carOwnerId);
        owner.respondToOffer(offerId, accepted);

        Offer offer = owner.getReceivedOffers().stream()
                .filter(o -> o.getId() != null && o.getId().equals(offerId))
                .findFirst().orElse(null);

        if (offer == null) {
            throw new IllegalArgumentException("Angebot nicht gefunden");
        }

        return new OfferOutputDto(
                offer.getId(),
                offer.getDescription(),
                offer.getCosts(),
                offer.getAccepted(),
                offer.getRequest().getId(),
                offer.getStorageOwner().getId());
    }

    // Ein- und Auslagerungstermin koordinieren
    @Transactional
    public AppointmentOutputDto createAppointment(String carOwnerId, AppointmentInputDto dto) {
        CarOwner carOwner = carOwnerRepository.findById(carOwnerId);
        StorageOwner storageOwner = storageOwnerRepository.findById(dto.storageOwnerId());

        Car car = carOwner.getCarById(dto.carId());
        if (car == null) {
            throw new IllegalArgumentException("Fahrzeug nicht gefunden");
        }

        car.assignCarToStorage(storageOwner);

        Appointment appointment = carOwner.createAppointment(storageOwner, car, dto.date(), dto.time());
        carOwnerRepository.flush();

        return new AppointmentOutputDto(
                appointment.getId(),
                appointment.getDate(),
                appointment.getTime(),
                appointment.getStorageOwner().getId(),
                appointment.getSubject().getId());
    }

    @Transactional
    public List<AppointmentOutputDto> getAllAppointments(String carOwnerId) {
        CarOwner owner = carOwnerRepository.findById(carOwnerId);
        return owner.getAllAppointments().stream()
                .map(appointment -> new AppointmentOutputDto(
                        appointment.getId(),
                        appointment.getDate(),
                        appointment.getTime(),
                        appointment.getStorageOwner().getId(),
                        appointment.getSubject().getId()))
                .toList();
    }

    @Transactional
    public void deleteAppointment(String carOwnerId, String appointmentId) {
        CarOwner owner = carOwnerRepository.findById(carOwnerId);
        Appointment appointment = owner.getAppointmentById(appointmentId);
        if (appointment != null) {
            owner.deleteAppointment(appointment);
        }
    }

    // Optionale Lagerservices buchen
    @Transactional
    public CarOutputDto bookOptionalService(String carOwnerId, String carId, String storageOwnerId, String serviceId) {
        CarOwner carOwner = carOwnerRepository.findById(carOwnerId);
        StorageOwner storageOwner = storageOwnerRepository.findById(storageOwnerId);

        Service service = storageOwner.getServiceById(serviceId);
        if (service == null) {
            throw new IllegalArgumentException("Service nicht gefunden");
        }

        Car car = carOwner.getCarById(carId);
        if (car == null) {
            throw new IllegalArgumentException("Fahrzeug nicht gefunden");
        }

        car.bookOptionalService(service);

        return new CarOutputDto(
                car.getId(),
                car.getLicensePlate(),
                car.isReadyToDrive(),
                car.isMaintained(),
                car.getBookedServices().stream().map(s -> s.getId()).collect(Collectors.toSet()),
                car.getCurrentStorage() != null ? car.getCurrentStorage().getId() : null);
    }

    // Fahrzeug registrieren
    @Transactional
    public CarOutputDto createCar(String carOwnerId, CarInputDto dto) {
        CarOwner owner = carOwnerRepository.findById(carOwnerId);
        if (owner.getCarByLicensePlate(dto.licensePlate()) != null) {
            throw new ConflictException("Kennzeichen ist bereits registriert");
        }

        Car car = owner.createCar(dto.licensePlate());
        carOwnerRepository.flush();

        return new CarOutputDto(
                car.getId(),
                car.getLicensePlate(),
                car.isReadyToDrive(),
                car.isMaintained(),
                car.getBookedServices().stream().map(s -> s.getId()).collect(Collectors.toSet()),
                car.getCurrentStorage() != null ? car.getCurrentStorage().getId() : null);
    }

    // Fahrzeuge verwalten
    @Transactional
    public List<CarOutputDto> getCars(String carOwnerId) {
        CarOwner owner = carOwnerRepository.findById(carOwnerId);
        return owner.getAllCars().stream()
                .map(car -> new CarOutputDto(
                        car.getId(),
                        car.getLicensePlate(),
                        car.isReadyToDrive(),
                        car.isMaintained(),
                        car.getBookedServices().stream().map(s -> s.getId()).collect(Collectors.toSet()),
                        car.getCurrentStorage() != null ? car.getCurrentStorage().getId() : null))
                .toList();
    }

    @Transactional
    public CarOutputDto updateLicensePlate(String carOwnerId, String carId, CarInputDto dto) {
        CarOwner owner = carOwnerRepository.findById(carOwnerId);
        if (owner.getCarByLicensePlate(dto.licensePlate()) != null) {
            throw new ConflictException("Kennzeichen ist bereits registriert");
        }

        owner.updateCar(carId, dto.licensePlate(), null, null);
        Car car = owner.getCarById(carId);

        return new CarOutputDto(
                car.getId(),
                car.getLicensePlate(),
                car.isReadyToDrive(),
                car.isMaintained(),
                car.getBookedServices().stream().map(s -> s.getId()).collect(Collectors.toSet()),
                car.getCurrentStorage() != null ? car.getCurrentStorage().getId() : null);
    }

    @Transactional
    public void deleteCar(String carOwnerId, String carId) {
        CarOwner owner = carOwnerRepository.findById(carOwnerId);

        Car car = owner.getCarById(carId);
        if (car != null) {
            owner.removeCar(car);
        }
    }
}
