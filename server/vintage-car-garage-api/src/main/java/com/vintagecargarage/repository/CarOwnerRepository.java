package com.vintagecargarage.repository;

import com.vintagecargarage.entities.CarOwner;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CarOwnerRepository extends BaseRepository<CarOwner> {

    public CarOwner findByEmail(String email) {
        return find("email", email).firstResult();
    }
}
