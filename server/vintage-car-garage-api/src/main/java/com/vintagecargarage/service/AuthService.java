package com.vintagecargarage.service;

import com.vintagecargarage.dtos.LoginDto;
import com.vintagecargarage.entities.CarOwner;
import com.vintagecargarage.entities.StorageOwner;
import com.vintagecargarage.entities.UserRole;
import com.vintagecargarage.repository.CarOwnerRepository;
import com.vintagecargarage.repository.StorageOwnerRepository;
import com.vintagecargarage.utils.Auth;

import io.quarkus.security.credential.TokenCredential;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class AuthService {

    @Inject
    Auth auth;

    @Inject
    CarOwnerRepository coRepo;

    @Inject
    StorageOwnerRepository soRepo;

    public TokenCredential authenticate(String role, LoginDto dto) {
        if (role.equals(UserRole.CAR_OWNER)) {
            CarOwner co = coRepo.findByEmail(dto.email());
            if (auth.verifyPassword(dto.password(), co.getPassword()))
                return auth.generateAuthToken(co.getId(), UserRole.CAR_OWNER, co.getPasswordVersion());
        }

        if (role.equals(UserRole.STORAGE_OWNER)) {
            StorageOwner so = soRepo.findByEmail(dto.email());
            if (auth.verifyPassword(dto.password(), so.getPassword()))
                return auth.generateAuthToken(so.getId(), UserRole.STORAGE_OWNER, so.getPasswordVersion());
        }

        return null;
    }
}
