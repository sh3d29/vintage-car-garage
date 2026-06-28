package com.vintagecargarage.application.services;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.vintagecargarage.application.dtos.car.CarOutputDto;
import com.vintagecargarage.application.dtos.car.CarStatusDto;
import com.vintagecargarage.application.dtos.offer.OfferInputDto;
import com.vintagecargarage.application.dtos.offer.OfferOutputDto;
import com.vintagecargarage.application.dtos.request.RequestOutputDto;
import com.vintagecargarage.application.dtos.service.ServiceInputDto;
import com.vintagecargarage.application.dtos.service.ServiceOutputDto;
import com.vintagecargarage.application.dtos.user.LocationDto;
import com.vintagecargarage.application.dtos.user.StorageDto;
import com.vintagecargarage.application.dtos.user.StorageOwnerInputDto;
import com.vintagecargarage.application.dtos.user.StorageOwnerOutputDto;
import com.vintagecargarage.domain.entities.Car;
import com.vintagecargarage.domain.entities.Location;
import com.vintagecargarage.domain.entities.Offer;
import com.vintagecargarage.domain.entities.Service;
import com.vintagecargarage.domain.entities.Storage;
import com.vintagecargarage.domain.entities.StorageOwner;
import com.vintagecargarage.domain.repositories.StorageOwnerRepository;
import com.vintagecargarage.exceptions.ConflictException;
import com.vintagecargarage.infrastructure.auth.Auth;
import com.vintagecargarage.infrastructure.auth.UserRole;

import io.quarkus.security.credential.TokenCredential;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class StorageOwnerService {

        @Inject
        Auth auth;

        @Inject
        StorageOwnerRepository storageOwnerRepository;

        // Lagerhalter registrieren
        @Transactional
        public void createStorageOwner(StorageOwnerInputDto dto) {
                if (storageOwnerRepository.isEmailTaken(dto.email())) {
                        throw new ConflictException("E-Mail-Adresse ist bereits registriert");
                }
                Location location = dto.location() != null
                                ? new Location(dto.location().street(), dto.location().houseNumber(),
                                                dto.location().postalCode(), dto.location().city())
                                : null;
                Storage storage = dto.storage() != null
                                ? new Storage(dto.storage().conditions(), dto.storage().capacity())
                                : null;

                StorageOwner newSo = StorageOwner.createStorageOwner(
                                dto.email(),
                                auth.hashPassword(dto.password()),
                                dto.companyName(),
                                dto.costs(),
                                dto.serviceTime(),
                                location,
                                storage,
                                dto.carManufacturers());
                storageOwnerRepository.persist(newSo);
        }

        // Lagerhalter verwalten & Stellplatzkapazität verwalten
        @Transactional
        public StorageOwnerOutputDto getStorageOwner(String id) {
                StorageOwner so = storageOwnerRepository.findById(id);
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

                return new StorageOwnerOutputDto(
                                so.getId(), so.getEmail(), so.getCompanyName(),
                                so.getCosts(), so.getServiceTime(),
                                locationDto,
                                storageDto,
                                so.getCarManufacturers() != null ? Set.copyOf(so.getCarManufacturers()) : Set.of());
        }

        @Transactional
        public TokenCredential updateStorageOwner(String id, StorageOwnerInputDto dto) {
                StorageOwner so = storageOwnerRepository.findById(id);

                if (dto.email() != null && !dto.email().equals(so.getEmail())
                                && storageOwnerRepository.isEmailTaken(dto.email())) {
                        throw new ConflictException("E-Mail-Adresse ist bereits registriert");
                }

                boolean passwordChanged = dto.password() != null
                                && !auth.verifyPassword(dto.password(), so.getPassword());

                Location location = dto.location() != null
                                ? new Location(dto.location().street(), dto.location().houseNumber(),
                                                dto.location().postalCode(), dto.location().city())
                                : null;
                Storage storage = dto.storage() != null
                                ? new Storage(dto.storage().conditions(), dto.storage().capacity())
                                : null;

                so.updateStorageOwner(
                                dto.email(), passwordChanged ? auth.hashPassword(dto.password()) : null,
                                dto.companyName(), dto.costs(),
                                dto.serviceTime(), location, storage, dto.carManufacturers());

                if (passwordChanged) {
                        return auth.generateAuthToken(so.getId(), UserRole.STORAGE_OWNER, so.getPasswordVersion());
                }

                return null;
        }

        // Serviceportfolio verwalten & Optionale Services verwalten
        @Transactional
        public List<ServiceOutputDto> getServices(String storageOwnerId) {
                StorageOwner owner = storageOwnerRepository.findById(storageOwnerId);
                return owner.getAllServices().stream()
                                .map(service -> new ServiceOutputDto(service.getId(), service.getTitle(),
                                                service.getDescription(), service.getPrice(),
                                                service.isOptional()))
                                .toList();
        }

        @Transactional
        public List<ServiceOutputDto> getOptionalServices(String storageOwnerId) {
                StorageOwner owner = storageOwnerRepository.findById(storageOwnerId);
                return owner.getAllServices().stream()
                                .filter(service -> service.isOptional())
                                .map(service -> new ServiceOutputDto(service.getId(), service.getTitle(),
                                                service.getDescription(), service.getPrice(), service.isOptional()))
                                .toList();
        }

        @Transactional
        public ServiceOutputDto createService(String storageOwnerId, ServiceInputDto dto) {
                StorageOwner owner = storageOwnerRepository.findById(storageOwnerId);
                Service service = owner.createService(dto.title(), dto.description(), dto.price(), dto.isOptional());
                storageOwnerRepository.flush();
                return new ServiceOutputDto(service.getId(), service.getTitle(), service.getDescription(),
                                service.getPrice(),
                                service.isOptional());
        }

        @Transactional
        public ServiceOutputDto updateService(String storageOwnerId, String serviceId, ServiceInputDto dto) {
                StorageOwner owner = storageOwnerRepository.findById(storageOwnerId);
                owner.updateService(serviceId, dto.title(), dto.description(), dto.price(), dto.isOptional());
                Service service = owner.getServiceById(serviceId);
                return new ServiceOutputDto(service.getId(), service.getTitle(), service.getDescription(),
                                service.getPrice(),
                                service.isOptional());
        }

        @Transactional
        public void deleteService(String storageOwnerId, String serviceId) {
                StorageOwner owner = storageOwnerRepository.findById(storageOwnerId);
                Service service = owner.getServiceById(serviceId);
                if (service != null) {
                        owner.deleteService(service);
                }
        }

        // Fahrzeugübersicht (UC_L3)
        @Transactional
        public List<CarOutputDto> getStoredCars(String storageOwnerId) {
                StorageOwner owner = storageOwnerRepository.findById(storageOwnerId);
                return owner.getStoredCars().stream()
                                .map(car -> new CarOutputDto(
                                                car.getId(), car.getLicensePlate(), car.isReadyToDrive(),
                                                car.isMaintained(),
                                                car.getBookedServices().stream().map(s -> s.getId())
                                                                .collect(Collectors.toSet()),
                                                car.getCurrentStorage() != null ? car.getCurrentStorage().getId()
                                                                : null))
                                .toList();
        }

        // Fahrzeugstatus aktualisieren (UC_L3)
        @Transactional
        public CarOutputDto updateCarStatus(String storageOwnerId, String carId, CarStatusDto dto) {
                StorageOwner owner = storageOwnerRepository.findById(storageOwnerId);
                Car car = owner.updateStoredCarStatus(carId, dto.readyToDrive(), dto.isMaintained());
                if (car == null) {
                        throw new IllegalArgumentException("Fahrzeug ist diesem Lagerhalter nicht zugewiesen");
                }
                return new CarOutputDto(
                                car.getId(), car.getLicensePlate(), car.isReadyToDrive(), car.isMaintained(),
                                car.getBookedServices().stream().map(s -> s.getId()).collect(Collectors.toSet()),
                                car.getCurrentStorage() != null ? car.getCurrentStorage().getId() : null);
        }

        // Stellplatzanfragen verwalten
        @Transactional
        public List<RequestOutputDto> getIncomingRequests(String storageOwnerId) {
                StorageOwner owner = storageOwnerRepository.findById(storageOwnerId);
                return owner.getIncomingRequests().stream()
                                .map(r -> new RequestOutputDto(r.getId(), r.getType().name(), r.getDescription(),
                                                r.getStorageOwner().getId(),
                                                r.getCarOwner() != null ? r.getCarOwner().getId() : null))
                                .toList();
        }

        // Angebote abgeben (UC_L7)
        @Transactional
        public OfferOutputDto createOffer(String storageOwnerId, String requestId, OfferInputDto dto) {
                StorageOwner owner = storageOwnerRepository.findById(storageOwnerId);
                Offer o = owner.createOffer(requestId, dto.description(), dto.costs());
                storageOwnerRepository.flush();
                return new OfferOutputDto(o.getId(), o.getDescription(), o.getCosts(), o.getAccepted(),
                                o.getRequest().getId(), o.getStorageOwner().getId());
        }

        // Angebote verwalten
        @Transactional
        public List<OfferOutputDto> getAllOffers(String storageOwnerId) {
                StorageOwner owner = storageOwnerRepository.findById(storageOwnerId);
                return owner.getAllOffers().stream()
                                .map(o -> new OfferOutputDto(
                                                o.getId(),
                                                o.getDescription(),
                                                o.getCosts(),
                                                o.getAccepted(),
                                                o.getRequest().getId(),
                                                o.getStorageOwner().getId()))
                                .toList();
        }

        @Transactional
        public void deleteOffer(String storageOwnerId, String offerId) {
                StorageOwner owner = storageOwnerRepository.findById(storageOwnerId);

                Offer offer = owner.getOfferById(offerId);

                if (offer != null) {
                        owner.deleteOffer(offer);
                }
        }
}
