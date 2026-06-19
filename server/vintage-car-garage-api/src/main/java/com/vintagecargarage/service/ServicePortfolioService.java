package com.vintagecargarage.service;

import com.vintagecargarage.dtos.ServiceDto;
import com.vintagecargarage.dtos.ServiceInputDto;
import com.vintagecargarage.entities.Service;
import com.vintagecargarage.entities.StorageOwner;
import com.vintagecargarage.mapper.ServiceMapper;
import com.vintagecargarage.repository.ServiceRepository;
import com.vintagecargarage.repository.StorageOwnerRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class ServicePortfolioService {

    @Inject
    StorageOwnerRepository storageOwnerRepository;

    @Inject
    ServiceRepository serviceRepository;

    @Inject
    ServiceMapper mapper;

    @Transactional
    public ServiceDto createService(String storageOwnerId, ServiceInputDto dto) {
        StorageOwner owner = storageOwnerRepository.findById(storageOwnerId);
        Service service = owner.createService(dto.title(), dto.description(), dto.price(), dto.isOptional());
        serviceRepository.persist(service);
        return mapper.toDto(service);
    }

    @Transactional
    public ServiceDto updateService(String id, ServiceInputDto dto) {
        Service service = serviceRepository.findById(id);
        service.updateDetails(dto.title(), dto.description(), dto.price(), dto.isOptional());
        return mapper.toDto(service);
    }

    @Transactional
    public void deleteService(String id) {
        serviceRepository.delete(serviceRepository.findById(id));
    }
}
