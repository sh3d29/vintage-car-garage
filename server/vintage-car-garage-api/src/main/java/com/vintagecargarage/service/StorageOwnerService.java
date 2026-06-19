package com.vintagecargarage.service;

import java.util.List;

import com.vintagecargarage.auth.Auth;
import com.vintagecargarage.dtos.StorageOwnerDto;
import com.vintagecargarage.entities.StorageOwner;
import com.vintagecargarage.entities.UserRole;
import com.vintagecargarage.mapper.StorageOwnerMapper;
import com.vintagecargarage.repository.StorageOwnerRepository;

import io.quarkus.security.credential.TokenCredential;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class StorageOwnerService {

    @Inject
    Auth auth;

    @Inject
    StorageOwnerMapper mapper;

    @Inject
    StorageOwnerRepository repository;

    @Transactional
    public StorageOwnerDto getStorageOwner(String id) {
        StorageOwner so = repository.findById(id);
        return mapper.toDto(so);
    }

    /** CO_UC_E1_1: searchLocation – Lagerhalter nach Stadt finden. */
    @Transactional
    public List<StorageOwnerDto> searchByCity(String city) {
        return repository.findByCity(city).stream().map(mapper::toDto).toList();
    }

    @Transactional
    public void createStorageOwner(StorageOwnerDto dto) {
        StorageOwner newCo = mapper.toEntity(dto);
        newCo.setPassword(auth.hashPassword(dto.password()));
        newCo.setPasswordVersion(1);
        repository.persist(newCo);
    }

    @Transactional
    public TokenCredential updateStorageOwner(String id, StorageOwnerDto dto) {
        StorageOwner so = repository.findById(id);
        mapper.updateFromDto(dto, so);

        if (dto.password() != null && !auth.verifyPassword(dto.password(), so.getPassword())) {
            so.changePassword(auth.hashPassword(dto.password()));
            return auth.generateAuthToken(so.getId(), UserRole.STORAGE_OWNER, so.getPasswordVersion());
        }

        return null;
    }

}
