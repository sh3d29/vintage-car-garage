package com.vintagecargarage.services;

import java.util.List;
import java.util.Optional;

import com.vintagecargarage.auth.Auth;
import com.vintagecargarage.auth.UserRole;
import com.vintagecargarage.dtos.appointment.AppointmentBookingInputDto;
import com.vintagecargarage.dtos.appointment.AppointmentOutputDto;
import com.vintagecargarage.dtos.car.CarInputDto;
import com.vintagecargarage.dtos.car.CarOutputDto;
import com.vintagecargarage.dtos.mappers.AppointmentMapper;
import com.vintagecargarage.dtos.mappers.CarMapper;
import com.vintagecargarage.dtos.mappers.CarOwnerMapper;
import com.vintagecargarage.dtos.mappers.OfferMapper;
import com.vintagecargarage.dtos.mappers.RequestMapper;
import com.vintagecargarage.dtos.mappers.StorageOwnerMapper;
import com.vintagecargarage.dtos.offer.OfferOutputDto;
import com.vintagecargarage.dtos.request.RequestInputDto;
import com.vintagecargarage.dtos.request.RequestOutputDto;
import com.vintagecargarage.dtos.service.ServiceOutputDto;
import com.vintagecargarage.dtos.user.CarOwnerInputDto;
import com.vintagecargarage.dtos.user.CarOwnerOutputDto;
import com.vintagecargarage.dtos.user.StorageOwnerSearchResDto;
import com.vintagecargarage.entities.Appointment;
import com.vintagecargarage.entities.AppointmentStatus;
import com.vintagecargarage.entities.AppointmentType;
import com.vintagecargarage.entities.Car;
import com.vintagecargarage.entities.CarOwner;
import com.vintagecargarage.entities.Offer;
import com.vintagecargarage.entities.OfferStatus;
import com.vintagecargarage.entities.Request;
import com.vintagecargarage.entities.Service;
import com.vintagecargarage.entities.StorageOwner;
import com.vintagecargarage.errors.exceptions.ConflictException;
import com.vintagecargarage.errors.exceptions.NotFoundException;
import com.vintagecargarage.errors.exceptions.ValidationException;
import com.vintagecargarage.repositories.CarOwnerRepository;
import com.vintagecargarage.repositories.StorageOwnerRepository;

import io.quarkus.security.credential.TokenCredential;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

/**
 * nimmt die
 * Systemoperationen des CarOwnerController entgegen, prüft die
 * Vorbedingungen und koordiniert die Domänenobjekte; die eigentliche
 * Zustandsänderung liegt in den Entitäten.
 */
@ApplicationScoped
public class CarOwnerService {

    @Inject
    Auth auth;

    @Inject
    CarOwnerRepository carOwnerRepository;

    @Inject
    StorageOwnerRepository storageOwnerRepository;

    /** lädt den CarOwner oder wirft NotFoundException */
    private CarOwner requireCarOwner(String id) {
        CarOwner co = carOwnerRepository.findById(id);
        if (co == null)
            throw new NotFoundException("Einlagerer nicht gefunden");
        return co;
    }

    /** lädt den StorageOwner oder wirft NotFoundException */
    private StorageOwner requireStorageOwner(String id) {
        StorageOwner so = storageOwnerRepository.findById(id);
        if (so == null)
            throw new NotFoundException("Lagerhalter nicht gefunden");
        return so;
    }

    // --- Benutzer registrieren ---

    /** registriert einen neuen CarOwner (E-Mail rollenübergreifend eindeutig) */
    @Transactional
    public void createCarOwner(CarOwnerInputDto dto) {
        if (carOwnerRepository.isEmailTaken(dto.email()) || storageOwnerRepository.isEmailTaken(dto.email()))
            throw new ConflictException("E-Mail-Adresse ist bereits registriert");
        carOwnerRepository.persist(
                new CarOwner(dto.email(), auth.hashPassword(dto.password()), 1, dto.firstName(), dto.lastName()));
    }

    // --- Benutzer verwalten ---

    /** Profildaten des CarOwner */
    @Transactional
    public CarOwnerOutputDto getCarOwner(String id) {
        return CarOwnerMapper.toCarOwnerOutputDto(requireCarOwner(id));
    }

    /**
     * aktualisiert das Profil; bei Passwortwechsel neues Token (alte Version
     * verfällt)
     */
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

    /** Stellplatzsuche: StorageOwner nach Standort-Ort */
    @Transactional
    public List<StorageOwnerSearchResDto> searchByCity(String city) {
        return storageOwnerRepository.findByCity(city).stream()
                .map(StorageOwnerMapper::toStorageOwnerSearchResDto).toList();
    }

    // --- Fahrzeug registrieren ---

    /**
     * registriert ein Car (Kennzeichen/Kategorie/Hersteller Pflicht, Kennzeichen je
     * Halter eindeutig)
     */
    @Transactional
    public CarOutputDto createCar(String carOwnerId, CarInputDto dto) {
        CarOwner owner = requireCarOwner(carOwnerId);
        if (dto.licensePlate() == null || dto.licensePlate().isBlank())
            throw new ValidationException("Kennzeichen erforderlich");
        if (dto.category() == null)
            throw new ValidationException("Fahrzeugkategorie erforderlich");
        if (dto.manufacturer() == null || dto.manufacturer().isBlank())
            throw new ValidationException("Hersteller erforderlich");
        boolean duplicatePlate = owner.getCars().stream()
                .anyMatch(c -> dto.licensePlate().equals(c.getLicensePlate()));
        if (duplicatePlate)
            throw new ConflictException("Kennzeichen ist bereits registriert");
        Car car = owner.registerCar(dto.licensePlate(), dto.manufacturer(), dto.category());
        carOwnerRepository.flush();
        return CarMapper.toCarOutputDto(car);
    }

    // --- Fahrzeuge verwalten ---

    /** alle Cars des CarOwner */
    @Transactional
    public List<CarOutputDto> getAllCars(String carOwnerId) {
        CarOwner owner = requireCarOwner(carOwnerId);
        return owner.getCars().stream().map(CarMapper::toCarOutputDto).toList();
    }

    /**
     * aktualisiert ein Car (Felder einzeln; Kennzeichen bleibt je Halter eindeutig)
     */
    @Transactional
    public CarOutputDto updateCar(String carOwnerId, String carId, CarInputDto dto) {
        CarOwner owner = requireCarOwner(carOwnerId);
        Car car = owner.getCars().stream()
                .filter(c -> c.getId().equals(carId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Fahrzeug nicht gefunden"));
        if (dto.licensePlate() != null) {
            if (dto.licensePlate().isBlank())
                throw new ValidationException("Kennzeichen erforderlich");
            boolean duplicatePlate = owner.getCars().stream()
                    .anyMatch(c -> !c.getId().equals(carId) && dto.licensePlate().equals(c.getLicensePlate()));
            if (duplicatePlate)
                throw new ConflictException("Kennzeichen ist bereits registriert");
            car.changeLicensePlate(dto.licensePlate());
        }
        if (dto.category() != null)
            car.changeCategory(dto.category());
        if (dto.manufacturer() != null) {
            if (dto.manufacturer().isBlank())
                throw new ValidationException("Hersteller erforderlich");
            car.changeManufacturer(dto.manufacturer());
        }
        return CarMapper.toCarOutputDto(car);
    }

    /** löscht ein Car (nicht möglich, solange es eingelagert ist) */
    @Transactional
    public void removeCar(String carOwnerId, String carId) {
        CarOwner owner = requireCarOwner(carOwnerId);
        Car car = owner.getCars().stream()
                .filter(c -> c.getId().equals(carId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Fahrzeug nicht gefunden"));
        if (car.getCurrentStorage() != null)
            throw new ConflictException("Eingelagertes Fahrzeug kann nicht gelöscht werden");
        owner.removeCar(car);
    }

    // --- Optionale Lagerservices buchen ---

    /** optional buchbare Services des StorageOwner */
    @Transactional
    public List<ServiceOutputDto> getOptionalServices(String carOwnerId, String storageOwnerId) {
        requireCarOwner(carOwnerId);
        StorageOwner owner = requireStorageOwner(storageOwnerId);
        return owner.getServices().stream()
                .filter(Service::isOptional)
                .map(StorageOwnerMapper::toServiceOutputDto).toList();
    }

    /**
     * bucht einen optionalen Service für ein Car (nur beim aktuellen
     * Lager-StorageOwner)
     */
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

    /** entfernt einen gebuchten optionalen Service von einem Car */
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

    /** stellt eine Request (Stellplatzanfrage) an einen StorageOwner */
    @Transactional
    public RequestOutputDto createRequest(String carOwnerId, RequestInputDto dto) {
        CarOwner carOwner = requireCarOwner(carOwnerId);
        StorageOwner storageOwner = requireStorageOwner(dto.storageOwnerId());
        Request request = carOwner.submitRequest(storageOwner, dto.description());
        carOwnerRepository.flush();
        return RequestMapper.toRequestOutputDto(request);
    }

    /** noch relevante Requests (offen oder mit noch nicht angenommenem Offer) */
    @Transactional
    public List<RequestOutputDto> getAllRequests(String carOwnerId) {
        CarOwner carOwner = requireCarOwner(carOwnerId);
        return carOwner.getRequests().stream()
                .filter(r -> r.getOffer() == null
                        || r.getOffer().getStatus() != OfferStatus.ACCEPTED)
                .map(RequestMapper::toRequestOutputDto).toList();
    }

    /** löscht eine Request (nicht möglich bei bereits angenommenem Offer) */
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

    /** alle auf die eigenen Requests erhaltenen Offers */
    @Transactional
    public List<OfferOutputDto> getReceivedOffers(String carOwnerId) {
        CarOwner owner = requireCarOwner(carOwnerId);
        return owner.getRequests().stream()
                .filter(r -> r.getOffer() != null)
                .map(r -> OfferMapper.toOfferOutputDto(r.getOffer()))
                .toList();
    }

    /**
     * Antwort auf ein Offer: annehmen (ACCEPTED) oder ablehnen (Offer löschen +
     * Request wieder freigeben, damit ein neues Offer möglich ist)
     */
    @Transactional
    public OfferOutputDto respondToOffer(String carOwnerId, String offerId, Boolean accepted) {
        CarOwner owner = requireCarOwner(carOwnerId);
        Offer offer = owner.getRequests().stream()
                .filter(r -> r.getOffer() != null && r.getOffer().getId().equals(offerId))
                .map(Request::getOffer)
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Angebot nicht gefunden"));
        if (Boolean.TRUE.equals(accepted)) {
            offer.accept();
            return OfferMapper.toOfferOutputDto(offer);
        }
        // Ablehnung: Angebot löschen und Anfrage wieder freigeben, damit der
        // Lagerhalter ein neues Angebot erstellen kann.
        offer.reject();
        OfferOutputDto dto = OfferMapper.toOfferOutputDto(offer);
        offer.getStorageOwner().withdrawOffer(offer);
        storageOwnerRepository.getEntityManager().remove(offer);
        return dto;
    }

    // --- Ein- & Auslagerungstermin koordinieren ---

    /**
     * bucht für ein Car einen freien Termin: EINLAGERUNG setzt angenommenes Offer
     * + noch nicht eingelagert voraus, AUSLAGERUNG setzt aktuelle Einlagerung
     * voraus;
     * je Car nur ein offener Termin gleicher Art. Umlagerung erst beim Abschluss.
     */
    @Transactional
    public AppointmentOutputDto bookAppointment(String carOwnerId, String storageOwnerId,
            AppointmentBookingInputDto dto) {
        CarOwner carOwner = requireCarOwner(carOwnerId);
        StorageOwner storageOwner = requireStorageOwner(storageOwnerId);
        Car car = carOwner.getCars().stream()
                .filter(c -> c.getId().equals(dto.carId()))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Fahrzeug nicht gefunden"));
        if (dto.type() == AppointmentType.EINLAGERUNG) {
            // Eine Einlagerung setzt ein angenommenes Angebot des Lagerhalters voraus.
            boolean hasAcceptedOffer = carOwner.getRequests().stream()
                    .anyMatch(r -> r.getStorageOwner().equals(storageOwner)
                            && r.getOffer() != null
                            && r.getOffer().getStatus() == OfferStatus.ACCEPTED);
            if (!hasAcceptedOffer)
                throw new ConflictException("Keine akzeptierte Buchung bei diesem Lagerhalter");
            if (car.getCurrentStorage() != null)
                throw new ConflictException("Fahrzeug ist bereits eingelagert");
            boolean pendingEinlagerung = carOwner.getBookedAppointments().stream()
                    .anyMatch(a -> a.getType() == AppointmentType.EINLAGERUNG
                            && a.getStatus() == AppointmentStatus.BOOKED
                            && car.equals(a.getSubject()));
            if (pendingEinlagerung)
                throw new ConflictException("Für dieses Fahrzeug besteht bereits eine Einlagerung");
        } else {
            // Eine Auslagerung hängt nur davon ab, dass das Fahrzeug aktuell bei
            // diesem Lagerhalter eingelagert ist – nicht (mehr) von einem Angebot.
            if (!storageOwner.equals(car.getCurrentStorage()))
                throw new ConflictException("Fahrzeug ist bei diesem Lagerhalter nicht eingelagert");
            boolean pendingAuslagerung = carOwner.getBookedAppointments().stream()
                    .anyMatch(a -> a.getType() == AppointmentType.AUSLAGERUNG
                            && a.getStatus() == AppointmentStatus.BOOKED
                            && car.equals(a.getSubject()));
            if (pendingAuslagerung)
                throw new ConflictException("Für dieses Fahrzeug besteht bereits eine Auslagerung");
        }
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

    /** aktuell freie (buchbare) Termine des StorageOwner */
    @Transactional
    public List<AppointmentOutputDto> getAvailableAppointments(String carOwnerId, String storageOwnerId) {
        requireCarOwner(carOwnerId);
        StorageOwner storageOwner = requireStorageOwner(storageOwnerId);
        return storageOwner.getAppointments().stream()
                .filter(a -> a.getStatus() == AppointmentStatus.AVAILABLE)
                .map(AppointmentMapper::toAppointmentOutputDto)
                .toList();
    }

    /** noch nicht abgeschlossene, vom CarOwner gebuchte Termine */
    @Transactional
    public List<AppointmentOutputDto> getAllBookedAppointments(String carOwnerId) {
        CarOwner owner = requireCarOwner(carOwnerId);
        return owner.getBookedAppointments().stream()
                .filter(a -> a.getStatus() != AppointmentStatus.COMPLETED)
                .map(AppointmentMapper::toAppointmentOutputDto).toList();
    }

    /**
     * storniert einen gebuchten Termin (Slot wird wieder frei; nicht bei COMPLETED)
     */
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
