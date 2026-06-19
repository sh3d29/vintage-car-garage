package com.vintagecargarage.service;

import com.vintagecargarage.auth.Auth;
import com.vintagecargarage.dtos.CarOwnerDto;
import com.vintagecargarage.entities.CarOwner;
import com.vintagecargarage.entities.UserRole;
import com.vintagecargarage.mapper.CarOwnerMapper;
import com.vintagecargarage.repository.CarOwnerRepository;

import io.quarkus.security.credential.TokenCredential;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class CarOwnerService {

    @Inject
    Auth auth;

    @Inject
    CarOwnerMapper mapper;

    @Inject
    CarOwnerRepository repository;

    public CarOwnerDto getCarOwner(String id) {
        CarOwner co = repository.findById(id);
        return mapper.toDto(co);
    }

    @Transactional
    public void createCarOwner(CarOwnerDto dto) {
        CarOwner newCo = mapper.toEntity(dto);
        newCo.setPassword(auth.hashPassword(dto.password()));
        newCo.setPasswordVersion(1);
        repository.persist(newCo);
    }

    @Transactional
    public TokenCredential updateCarOwner(String id, CarOwnerDto dto) {
        CarOwner co = repository.findById(id);
        mapper.updateFromDto(dto, co);

        if (dto.password() != null && !auth.verifyPassword(dto.password(), co.getPassword())) {
            co.changePassword(auth.hashPassword(dto.password()));
            return auth.generateAuthToken(co.getId(), UserRole.CAR_OWNER, co.getPasswordVersion());
        }

        return null;
    }
}
