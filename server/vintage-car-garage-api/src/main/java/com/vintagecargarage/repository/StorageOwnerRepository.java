package com.vintagecargarage.repository;

import java.util.List;

import com.vintagecargarage.entities.StorageOwner;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class StorageOwnerRepository extends BaseRepository<StorageOwner> {

    public StorageOwner findByEmail(String email) {
        return find("email", email).firstResult();
    }

    /** CO_UC_E1_1: searchLocation – alle Lagerhalter in einer Stadt. */
    public List<StorageOwner> findByCity(String city) {
        return list("location.city", city);
    }
}
