package com.vintagecargarage.services;

import java.util.HashSet;
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
import com.vintagecargarage.mappers.AppointmentMapper;
import com.vintagecargarage.mappers.CarMapper;
import com.vintagecargarage.mappers.OfferMapper;
import com.vintagecargarage.mappers.RequestMapper;
import com.vintagecargarage.mappers.StorageOwnerMapper;
import com.vintagecargarage.entities.Appointment;
import com.vintagecargarage.entities.AppointmentStatus;
import com.vintagecargarage.entities.Car;
import com.vintagecargarage.entities.Location;
import com.vintagecargarage.entities.Offer;
import com.vintagecargarage.entities.OfferStatus;
import com.vintagecargarage.entities.Request;
import com.vintagecargarage.entities.Service;
import com.vintagecargarage.entities.Storage;
import com.vintagecargarage.entities.StorageOwner;
import com.vintagecargarage.errors.exceptions.ConflictException;
import com.vintagecargarage.errors.exceptions.NotFoundException;
import com.vintagecargarage.auth.Auth;
import com.vintagecargarage.auth.UserRole;
import com.vintagecargarage.repositories.CarOwnerRepository;
import com.vintagecargarage.repositories.StorageOwnerRepository;

import io.quarkus.security.credential.TokenCredential;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.Optional;

@ApplicationScoped
public class StorageOwnerService {

    @Inject
    Auth auth;

    @Inject
    StorageOwnerRepository storageOwnerRepository;

    @Inject
    CarOwnerRepository carOwnerRepository;

    private StorageOwner requireStorageOwner(String id) {
        StorageOwner so = storageOwnerRepository.findById(id);
        if (so == null)
            throw new NotFoundException("Lagerhalter nicht gefunden");
        return so;
    }

    // --- Benutzer registrieren ---

    @Transactional
    public void createStorageOwner(StorageOwnerInputDto dto) {
        if (storageOwnerRepository.isEmailTaken(dto.email()) || carOwnerRepository.isEmailTaken(dto.email()))
            throw new ConflictException("E-Mail-Adresse ist bereits registriert");
        Location location = dto.location() != null
                ? new Location(dto.location().street(), dto.location().houseNumber(),
                        dto.location().postalCode(), dto.location().city())
                : null;
        Storage storage = dto.storage() != null
                ? new Storage(dto.storage().conditions(), dto.storage().capacity())
                : null;
        storageOwnerRepository.persist(new StorageOwner(dto.email(), auth.hashPassword(dto.password()), 1,
                dto.companyName(), dto.costs() != null ? dto.costs() : 0f,
                dto.serviceTime(), location, storage,
                dto.carManufacturers() != null ? dto.carManufacturers() : new HashSet<>()));
    }

    // --- Benutzer verwalten ---

    @Transactional
    public StorageOwnerOutputDto getStorageOwner(String id) {
        return StorageOwnerMapper.toStorageOwnerOutputDto(requireStorageOwner(id));
    }

    @Transactional
    public Optional<TokenCredential> updateStorageOwner(String id, StorageOwnerInputDto dto) {
        StorageOwner so = requireStorageOwner(id);
        if (dto.email() != null && !dto.email().equals(so.getEmail())
                && storageOwnerRepository.isEmailTaken(dto.email()))
            throw new ConflictException("E-Mail-Adresse ist bereits registriert");
        Location location = dto.location() != null
                ? new Location(dto.location().street(), dto.location().houseNumber(),
                        dto.location().postalCode(), dto.location().city())
                : null;
        Storage storage = dto.storage() != null
                ? new Storage(dto.storage().conditions(), dto.storage().capacity())
                : null;
        so.updateProfile(dto.email(), dto.companyName(), dto.costs(), dto.serviceTime(),
                location, storage, dto.carManufacturers());
        if (dto.password() != null && !auth.verifyPassword(dto.password(), so.getPassword())) {
            so.changePassword(auth.hashPassword(dto.password()));
            return Optional
                    .of(auth.generateAuthToken(so.getId(), UserRole.STORAGE_OWNER, so.getPasswordVersion()));
        }
        return Optional.empty();
    }

    // --- Serviceportfolio verwalten & Optionale Services verwalten ---

    @Transactional
    public ServiceOutputDto createService(String storageOwnerId, ServiceInputDto dto) {
        StorageOwner owner = requireStorageOwner(storageOwnerId);
        Service service = owner.openService(dto.title(), dto.description(),
                dto.price() != null ? dto.price() : 0f,
                dto.isOptional() != null ? dto.isOptional() : false);
        storageOwnerRepository.flush();
        return StorageOwnerMapper.toServiceOutputDto(service);
    }

    @Transactional
    public List<ServiceOutputDto> getAllServices(String storageOwnerId) {
        StorageOwner owner = requireStorageOwner(storageOwnerId);
        return owner.getServices().stream().map(StorageOwnerMapper::toServiceOutputDto).toList();
    }

    @Transactional
    public List<ServiceOutputDto> getOptionalServices(String storageOwnerId) {
        StorageOwner owner = requireStorageOwner(storageOwnerId);
        return owner.getServices().stream()
                .filter(Service::isOptional)
                .map(StorageOwnerMapper::toServiceOutputDto).toList();
    }

    @Transactional
    public ServiceOutputDto updateService(String storageOwnerId, String serviceId, ServiceInputDto dto) {
        StorageOwner owner = requireStorageOwner(storageOwnerId);
        Service service = owner.getServices().stream()
                .filter(s -> s.getId().equals(serviceId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Service nicht gefunden"));
        service.updateDetails(dto.title(), dto.description(), dto.price(), dto.isOptional());
        return StorageOwnerMapper.toServiceOutputDto(service);
    }

    @Transactional
    public void deleteService(String storageOwnerId, String serviceId) {
        StorageOwner owner = requireStorageOwner(storageOwnerId);
        owner.getServices().stream()
                .filter(s -> s.getId().equals(serviceId))
                .findFirst()
                .ifPresent(owner::removeService);
    }

    // --- Fahrzeugstatus aktualisieren ---

    @Transactional
    public List<CarOutputDto> getStoredCars(String storageOwnerId) {
        StorageOwner owner = requireStorageOwner(storageOwnerId);
        return owner.getStoredCars().stream().map(CarMapper::toCarOutputDto).toList();
    }

    @Transactional
    public CarOutputDto updateStoredCarStatus(String storageOwnerId, String carId, CarStatusDto dto) {
        StorageOwner owner = requireStorageOwner(storageOwnerId);
        Car car = owner.getStoredCars().stream()
                .filter(c -> c.getId().equals(carId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Fahrzeug ist diesem Lagerhalter nicht zugewiesen"));
        car.updateStatus(dto.readyToDrive(), dto.isMaintained());
        return CarMapper.toCarOutputDto(car);
    }

    // --- Stellplatzanfragen verwalten ---

    @Transactional
    public OfferOutputDto createOffer(String storageOwnerId, String requestId, OfferInputDto dto) {
        StorageOwner owner = requireStorageOwner(storageOwnerId);
        Request request = owner.getIncomingRequests().stream()
                .filter(r -> r.getId().equals(requestId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Stellplatzanfrage nicht gefunden"));
        if (request.getOffer() != null)
            throw new ConflictException("Für diese Anfrage besteht bereits ein Angebot");
        Offer offer = owner.makeOffer(request, dto.description(), dto.costs());
        storageOwnerRepository.flush();
        return OfferMapper.toOfferOutputDto(offer);
    }

    @Transactional
    public List<RequestOutputDto> getIncomingRequests(String storageOwnerId) {
        StorageOwner owner = requireStorageOwner(storageOwnerId);
        return owner.getIncomingRequests().stream()
                .map(RequestMapper::toRequestOutputDto).toList();
    }

    @Transactional
    public List<OfferOutputDto> getAllOffers(String storageOwnerId) {
        StorageOwner owner = requireStorageOwner(storageOwnerId);
        return owner.getOffers().stream().map(OfferMapper::toOfferOutputDto).toList();
    }

    @Transactional
    public void deleteOffer(String storageOwnerId, String offerId) {
        StorageOwner owner = requireStorageOwner(storageOwnerId);
        Offer offer = owner.getOffers().stream()
                .filter(o -> o.getId().equals(offerId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Angebot nicht gefunden"));
        if (offer.getStatus() == OfferStatus.ACCEPTED)
            throw new ConflictException("Angenommene Angebote können nicht gelöscht werden");
        storageOwnerRepository.getEntityManager().remove(offer);
    }

    // --- Ein- & Auslagerungstermin koordinieren ---

    @Transactional
    public AppointmentOutputDto createAppointment(String storageOwnerId, AppointmentSlotInputDto dto) {
        StorageOwner owner = requireStorageOwner(storageOwnerId);
        Appointment appointment = owner.scheduleAppointment(dto.type(), dto.date(), dto.time());
        storageOwnerRepository.flush();
        return AppointmentMapper.toAppointmentOutputDto(appointment);
    }

    @Transactional
    public List<AppointmentOutputDto> getAllAppointments(String storageOwnerId) {
        StorageOwner owner = requireStorageOwner(storageOwnerId);
        return owner.getAppointments().stream()
                .filter(a -> a.getStatus() != AppointmentStatus.COMPLETED)
                .map(AppointmentMapper::toAppointmentOutputDto).toList();
    }

    @Transactional
    public AppointmentOutputDto completeAppointment(String storageOwnerId, String appointmentId) {
        StorageOwner owner = requireStorageOwner(storageOwnerId);
        Appointment appointment = owner.getAppointments().stream()
                .filter(a -> a.getId().equals(appointmentId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Termin nicht gefunden"));
        if (appointment.getStatus() != AppointmentStatus.BOOKED)
            throw new ConflictException("Nur gebuchte Termine können abgeschlossen werden");
        appointment.complete();
        return AppointmentMapper.toAppointmentOutputDto(appointment);
    }

    @Transactional
    public void deleteAppointment(String storageOwnerId, String appointmentId) {
        StorageOwner owner = requireStorageOwner(storageOwnerId);
        owner.getAppointments().stream()
                .filter(a -> a.getId().equals(appointmentId))
                .findFirst()
                .ifPresent(a -> storageOwnerRepository.getEntityManager().remove(a));
    }
}
