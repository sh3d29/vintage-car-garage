package com.vintagecargarage.application.services;

import com.vintagecargarage.application.dtos.auth.SignInDto;
import com.vintagecargarage.domain.entities.CarOwner;
import com.vintagecargarage.domain.entities.StorageOwner;
import com.vintagecargarage.domain.repositories.CarOwnerRepository;
import com.vintagecargarage.domain.repositories.StorageOwnerRepository;
import com.vintagecargarage.infrastructure.auth.Auth;
import com.vintagecargarage.infrastructure.auth.UserRole;

import io.quarkus.security.credential.TokenCredential;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class AuthService {

    @Inject
    Auth auth;

    @Inject
    StorageOwnerRepository storageOwnerRepository;

    @Inject
    CarOwnerRepository carOwnerRepository;

    public TokenCredential authenticate(SignInDto dto) {

        StorageOwner so = storageOwnerRepository.findByEmail(dto.email());
        CarOwner co = carOwnerRepository.findByEmail(dto.email());

        if (so != null && auth.verifyPassword(dto.password(), so.getPassword())) {
            return auth.generateAuthToken(so.getId(), UserRole.STORAGE_OWNER, so.getPasswordVersion());
        } else if (co != null && auth.verifyPassword(dto.password(), co.getPassword())) {
            return auth.generateAuthToken(co.getId(), UserRole.CAR_OWNER, co.getPasswordVersion());
        }

        return null;
    }
}
