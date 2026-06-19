package com.vintagecargarage.service;

import com.vintagecargarage.dtos.RequestDto;
import com.vintagecargarage.dtos.RequestInputDto;
import com.vintagecargarage.entities.CarOwner;
import com.vintagecargarage.entities.Request;
import com.vintagecargarage.entities.StorageOwner;
import com.vintagecargarage.mapper.RequestMapper;
import com.vintagecargarage.repository.CarOwnerRepository;
import com.vintagecargarage.repository.RequestRepository;
import com.vintagecargarage.repository.StorageOwnerRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class RequestService {

    @Inject
    CarOwnerRepository carOwnerRepository;

    @Inject
    StorageOwnerRepository storageOwnerRepository;

    @Inject
    RequestRepository requestRepository;

    @Inject
    RequestMapper mapper;

    @Transactional
    public RequestDto createRequest(String carOwnerId, RequestInputDto dto) {
        CarOwner carOwner = carOwnerRepository.findById(carOwnerId);
        StorageOwner storageOwner = storageOwnerRepository.findById(dto.storageOwnerId());
        Request request = carOwner.createRequest(storageOwner, dto.description());
        requestRepository.persist(request);
        return mapper.toDto(request);
    }
}
