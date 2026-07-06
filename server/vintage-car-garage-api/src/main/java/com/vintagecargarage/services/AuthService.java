package com.vintagecargarage.services;

import com.vintagecargarage.dtos.auth.AuthUserDto;
import com.vintagecargarage.dtos.auth.SignInDto;
import com.vintagecargarage.entities.CarOwner;
import com.vintagecargarage.entities.StorageOwner;
import com.vintagecargarage.auth.Auth;
import com.vintagecargarage.auth.AuthResult;
import com.vintagecargarage.auth.UserRole;
import com.vintagecargarage.mappers.CarOwnerMapper;
import com.vintagecargarage.mappers.StorageOwnerMapper;
import com.vintagecargarage.repositories.CarOwnerRepository;
import com.vintagecargarage.repositories.StorageOwnerRepository;

import io.quarkus.security.credential.TokenCredential;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.Optional;

@ApplicationScoped
public class AuthService {

    @Inject
    Auth auth;

    @Inject
    StorageOwnerRepository storageOwnerRepository;

    @Inject
    CarOwnerRepository carOwnerRepository;

    public Optional<AuthResult> authenticate(SignInDto dto) {

        StorageOwner so = storageOwnerRepository.findByEmail(dto.email());
        if (so != null && auth.verifyPassword(dto.password(), so.getPassword())) {
            TokenCredential token = auth.generateAuthToken(so.getId(), UserRole.STORAGE_OWNER,
                    so.getPasswordVersion());
            AuthUserDto user = new AuthUserDto(UserRole.STORAGE_OWNER,
                    StorageOwnerMapper.toStorageOwnerOutputDto(so));
            return Optional.of(new AuthResult(token, user));
        }

        CarOwner co = carOwnerRepository.findByEmail(dto.email());
        if (co != null && auth.verifyPassword(dto.password(), co.getPassword())) {
            TokenCredential token = auth.generateAuthToken(co.getId(), UserRole.CAR_OWNER,
                    co.getPasswordVersion());
            AuthUserDto user = new AuthUserDto(UserRole.CAR_OWNER,
                    CarOwnerMapper.toCarOwnerOutputDto(co));
            return Optional.of(new AuthResult(token, user));
        }

        return Optional.empty();
    }

    public Optional<AuthUserDto> getAuthenticatedUser(String userId, String role) {
        if (UserRole.STORAGE_OWNER.equals(role)) {
            StorageOwner so = storageOwnerRepository.findById(userId);
            if (so != null) {
                return Optional.of(new AuthUserDto(role, StorageOwnerMapper.toStorageOwnerOutputDto(so)));
            }
        } else if (UserRole.CAR_OWNER.equals(role)) {
            CarOwner co = carOwnerRepository.findById(userId);
            if (co != null) {
                return Optional.of(new AuthUserDto(role, CarOwnerMapper.toCarOwnerOutputDto(co)));
            }
        }
        return Optional.empty();
    }
}
