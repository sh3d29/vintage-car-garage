package com.vintagecargarage.services;

import java.util.List;
import java.util.Optional;

import com.vintagecargarage.auth.Auth;
import com.vintagecargarage.auth.UserRole;
import com.vintagecargarage.dtos.appointment.AppointmentBookingInputDto;
import com.vintagecargarage.dtos.appointment.AppointmentOutputDto;
import com.vintagecargarage.dtos.car.CarInputDto;
import com.vintagecargarage.dtos.car.CarOutputDto;
import com.vintagecargarage.dtos.offer.OfferOutputDto;
import com.vintagecargarage.dtos.request.RequestInputDto;
import com.vintagecargarage.dtos.request.RequestOutputDto;
import com.vintagecargarage.dtos.user.CarOwnerInputDto;
import com.vintagecargarage.dtos.user.CarOwnerOutputDto;
import com.vintagecargarage.dtos.user.StorageOwnerSearchResDto;
import com.vintagecargarage.entities.Appointment;
import com.vintagecargarage.entities.AppointmentStatus;
import com.vintagecargarage.entities.Car;
import com.vintagecargarage.entities.CarOwner;
import com.vintagecargarage.entities.Offer;
import com.vintagecargarage.entities.OfferStatus;
import com.vintagecargarage.entities.Request;
import com.vintagecargarage.entities.Service;
import com.vintagecargarage.entities.StorageOwner;
import com.vintagecargarage.errors.exceptions.ConflictException;
import com.vintagecargarage.errors.exceptions.NotFoundException;
import com.vintagecargarage.mappers.AppointmentMapper;
import com.vintagecargarage.mappers.CarMapper;
import com.vintagecargarage.mappers.CarOwnerMapper;
import com.vintagecargarage.mappers.OfferMapper;
import com.vintagecargarage.mappers.RequestMapper;
import com.vintagecargarage.mappers.StorageOwnerMapper;
import com.vintagecargarage.repositories.CarOwnerRepository;
import com.vintagecargarage.repositories.StorageOwnerRepository;

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

    private CarOwner requireCarOwner(String id) {
        CarOwner co = carOwnerRepository.findById(id);
        if (co == null)
            throw new NotFoundException("Einlagerer nicht gefunden");
        return co;
    }

    private StorageOwner requireStorageOwner(String id) {
        StorageOwner so = storageOwnerRepository.findById(id);
        if (so == null)
            throw new NotFoundException("Lagerhalter nicht gefunden");
        return so;
    }

    // --- Benutzer registrieren ---

    @Transactional
    public void createCarOwner(CarOwnerInputDto dto) {
        if (carOwnerRepository.isEmailTaken(dto.email()) || storageOwnerRepository.isEmailTaken(dto.email()))
            throw new ConflictException("E-Mail-Adresse ist bereits registriert");
        carOwnerRepository.persist(
                new CarOwner(dto.email(), auth.hashPassword(dto.password()), 1, dto.firstName(), dto.lastName()));
    }

    // --- Benutzer verwalten ---

    @Transactional
    public CarOwnerOutputDto getCarOwner(String id) {
        return CarOwnerMapper.toCarOwnerOutputDto(requireCarOwner(id));
    }

    @Transactional
    public Optional<TokenCredential> updateCarOwner(String id, CarOwnerInputDto dto) {
        CarOwner co = requireCarOwner(id);
        if (dto.email() != null && !dto.email().equals(co.getEmail())
                && carOwnerRepository.isEmailTaken(dto.email()))
            throw new ConflictException("E-Mail-Adresse ist bereits registriert");
        co.updateProfile(dto.email(), dto.firstName(), dto.lastName());
        if (dto.password() != null && !auth.verifyPassword(dto.password(), co.getPassword())) {
            co.changePassword(auth.hashPassword(dto.password()));
            return Optional.of(auth.generateAuthToken(co.getId(), UserRole.CAR_OWNER, co.getPasswordVersion()));
        }
        return Optional.empty();
    }

    // --- Stellplatz suchen ---

    @Transactional
    public List<StorageOwnerSearchResDto> searchByCity(String city) {
        return storageOwnerRepository.findByCity(city).stream()
                .map(StorageOwnerMapper::toStorageOwnerSearchResDto).toList();
    }

    // --- Fahrzeug registrieren ---

    @Transactional
    public CarOutputDto createCar(String carOwnerId, CarInputDto dto) {
        CarOwner owner = requireCarOwner(carOwnerId);
        boolean duplicatePlate = owner.getCars().stream()
                .anyMatch(c -> dto.licensePlate().equals(c.getLicensePlate()));
        if (duplicatePlate)
            throw new ConflictException("Kennzeichen ist bereits registriert");
        Car car = owner.registerCar(dto.licensePlate());
        carOwnerRepository.flush();
        return CarMapper.toCarOutputDto(car);
    }

    // --- Fahrzeuge verwalten ---

    @Transactional
    public List<CarOutputDto> getAllCars(String carOwnerId) {
        CarOwner owner = requireCarOwner(carOwnerId);
        return owner.getCars().stream().map(CarMapper::toCarOutputDto).toList();
    }

    @Transactional
    public CarOutputDto updateCar(String carOwnerId, String carId, CarInputDto dto) {
        CarOwner owner = requireCarOwner(carOwnerId);
        boolean duplicatePlate = owner.getCars().stream()
                .anyMatch(c -> !c.getId().equals(carId) && dto.licensePlate().equals(c.getLicensePlate()));
        if (duplicatePlate)
            throw new ConflictException("Kennzeichen ist bereits registriert");
        Car car = owner.getCars().stream()
                .filter(c -> c.getId().equals(carId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Fahrzeug nicht gefunden"));
        if (dto.licensePlate() != null)
            car.changeLicensePlate(dto.licensePlate());
        return CarMapper.toCarOutputDto(car);
    }

    @Transactional
    public void removeCar(String carOwnerId, String carId) {
        CarOwner owner = requireCarOwner(carOwnerId);
        owner.getCars().stream()
                .filter(c -> c.getId().equals(carId))
                .findFirst()
                .ifPresent(owner::removeCar);
    }

    // --- Optionale Lagerservices buchen ---

    @Transactional
    public CarOutputDto bookOptionalService(String carOwnerId, String carId, String serviceId) {
        CarOwner carOwner = requireCarOwner(carOwnerId);
        Car car = carOwner.getCars().stream()
                .filter(c -> c.getId().equals(carId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Fahrzeug nicht gefunden"));
        StorageOwner storageOwner = car.getCurrentStorage();
        if (storageOwner == null)
            throw new ConflictException("Fahrzeug ist nicht eingelagert");
        Service service = storageOwner.getServices().stream()
                .filter(s -> s.getId().equals(serviceId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Service nicht gefunden"));
        car.addBookedOptionalService(service);
        return CarMapper.toCarOutputDto(car);
    }

    @Transactional
    public void removeBookedService(String carOwnerId, String carId, String serviceId) {
        CarOwner owner = requireCarOwner(carOwnerId);
        Car car = owner.getCars().stream()
                .filter(c -> c.getId().equals(carId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Fahrzeug nicht gefunden"));
        Service service = car.getBookedServices().stream()
                .filter(s -> s.getId().equals(serviceId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Gebuchter Service nicht gefunden"));
        car.removeBookedOptionalService(service);
    }

    // --- Stellplatzangebot anfordern ---

    @Transactional
    public RequestOutputDto createRequest(String carOwnerId, RequestInputDto dto) {
        CarOwner carOwner = requireCarOwner(carOwnerId);
        StorageOwner storageOwner = requireStorageOwner(dto.storageOwnerId());
        Request request = carOwner.submitRequest(storageOwner, dto.description());
        carOwnerRepository.flush();
        return RequestMapper.toRequestOutputDto(request);
    }

    @Transactional
    public List<RequestOutputDto> getAllRequests(String carOwnerId) {
        CarOwner carOwner = requireCarOwner(carOwnerId);
        return carOwner.getRequests().stream()
                .map(RequestMapper::toRequestOutputDto).toList();
    }

    @Transactional
    public void deleteRequest(String carOwnerId, String requestId) {
        CarOwner carOwner = requireCarOwner(carOwnerId);
        Request request = carOwner.getRequests().stream()
                .filter(r -> r.getId().equals(requestId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Anfrage nicht gefunden"));
        if (request.getOffer() != null && request.getOffer().getStatus() == OfferStatus.ACCEPTED)
            throw new ConflictException("Anfrage mit angenommenem Angebot kann nicht gelöscht werden");
        carOwner.removeRequest(request);
    }

    // --- Stellplatzangebote verwalten ---

    @Transactional
    public List<OfferOutputDto> getReceivedOffers(String carOwnerId) {
        CarOwner owner = requireCarOwner(carOwnerId);
        return owner.getRequests().stream()
                .filter(r -> r.getOffer() != null)
                .map(r -> OfferMapper.toOfferOutputDto(r.getOffer()))
                .toList();
    }

    @Transactional
    public OfferOutputDto respondToOffer(String carOwnerId, String offerId, Boolean accepted) {
        CarOwner owner = requireCarOwner(carOwnerId);
        Offer offer = owner.getRequests().stream()
                .filter(r -> r.getOffer() != null && r.getOffer().getId().equals(offerId))
                .map(Request::getOffer)
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Angebot nicht gefunden"));
        if (Boolean.TRUE.equals(accepted))
            offer.accept();
        else if (Boolean.FALSE.equals(accepted))
            offer.reject();
        return OfferMapper.toOfferOutputDto(offer);
    }

    // --- Ein- & Auslagerungstermin koordinieren ---

    @Transactional
    public List<StorageOwnerSearchResDto> getBookableStorageOwners(String carOwnerId) {
        CarOwner owner = requireCarOwner(carOwnerId);
        return owner.getRequests().stream()
                .filter(r -> r.getOffer() != null && r.getOffer().getStatus() == OfferStatus.ACCEPTED)
                .map(Request::getStorageOwner)
                .distinct()
                .map(StorageOwnerMapper::toStorageOwnerSearchResDto)
                .toList();
    }

    @Transactional
    public AppointmentOutputDto bookAppointment(String carOwnerId, String storageOwnerId,
            AppointmentBookingInputDto dto) {
        CarOwner carOwner = requireCarOwner(carOwnerId);
        StorageOwner storageOwner = requireStorageOwner(storageOwnerId);
        boolean hasAcceptedOffer = carOwner.getRequests().stream()
                .anyMatch(r -> r.getStorageOwner().equals(storageOwner)
                        && r.getOffer() != null
                        && r.getOffer().getStatus() == OfferStatus.ACCEPTED);
        if (!hasAcceptedOffer)
            throw new ConflictException("Keine akzeptierte Buchung bei diesem Lagerhalter");
        Car car = carOwner.getCars().stream()
                .filter(c -> c.getId().equals(dto.carId()))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Fahrzeug nicht gefunden"));
        Appointment appointment = storageOwner.getAppointments().stream()
                .filter(a -> a.getStatus() == AppointmentStatus.AVAILABLE
                        && a.getType() == dto.type()
                        && a.getDate().equals(dto.date())
                        && a.getTime().equals(dto.time()))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Kein verfügbarer Termin gefunden"));
        appointment.book(carOwner, car);
        carOwner.addAppointment(appointment);
        return AppointmentMapper.toAppointmentOutputDto(appointment);
    }

    @Transactional
    public List<AppointmentOutputDto> getAvailableAppointments(String carOwnerId, String storageOwnerId) {
        requireCarOwner(carOwnerId);
        StorageOwner storageOwner = requireStorageOwner(storageOwnerId);
        return storageOwner.getAppointments().stream()
                .filter(a -> a.getStatus() == AppointmentStatus.AVAILABLE)
                .map(AppointmentMapper::toAppointmentOutputDto)
                .toList();
    }

    @Transactional
    public List<AppointmentOutputDto> getAllBookedAppointments(String carOwnerId) {
        CarOwner owner = requireCarOwner(carOwnerId);
        return owner.getBookedAppointments().stream()
                .filter(a -> a.getStatus() != AppointmentStatus.COMPLETED)
                .map(AppointmentMapper::toAppointmentOutputDto).toList();
    }

    @Transactional
    public void cancelAppointment(String carOwnerId, String appointmentId) {
        CarOwner owner = requireCarOwner(carOwnerId);
        Appointment appointment = owner.getBookedAppointments().stream()
                .filter(a -> a.getId().equals(appointmentId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Termin nicht gefunden"));
        if (appointment.getStatus() == AppointmentStatus.COMPLETED)
            throw new ConflictException("Abgeschlossene Termine können nicht storniert werden");
        appointment.cancel();
        owner.removeAppointment(appointment);
    }
}
