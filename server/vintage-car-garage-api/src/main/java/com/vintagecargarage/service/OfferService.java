package com.vintagecargarage.service;

import com.vintagecargarage.dtos.OfferDto;
import com.vintagecargarage.dtos.OfferInputDto;
import com.vintagecargarage.entities.Offer;
import com.vintagecargarage.entities.Request;
import com.vintagecargarage.entities.StorageOwner;
import com.vintagecargarage.mapper.OfferMapper;
import com.vintagecargarage.repository.OfferRepository;
import com.vintagecargarage.repository.RequestRepository;
import com.vintagecargarage.repository.StorageOwnerRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class OfferService {

    @Inject
    StorageOwnerRepository storageOwnerRepository;

    @Inject
    RequestRepository requestRepository;

    @Inject
    OfferRepository offerRepository;

    @Inject
    OfferMapper mapper;

    @Transactional
    public OfferDto createOffer(String storageOwnerId, OfferInputDto dto) {
        StorageOwner storageOwner = storageOwnerRepository.findById(storageOwnerId);
        Request request = requestRepository.findById(dto.requestId());
        Offer offer = storageOwner.createOffer(request, dto.description(), dto.costs());
        offerRepository.persist(offer);
        return mapper.toDto(offer);
    }

    @Transactional
    public OfferDto respondToOffer(String offerId, boolean accepted) {
        Offer offer = offerRepository.findById(offerId);
        if (accepted) {
            offer.accept();
        } else {
            offer.reject();
        }
        return mapper.toDto(offer);
    }
}
