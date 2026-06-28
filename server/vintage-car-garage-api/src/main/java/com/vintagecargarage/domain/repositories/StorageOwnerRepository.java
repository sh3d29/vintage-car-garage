package com.vintagecargarage.domain.repositories;

import java.util.List;

import com.vintagecargarage.domain.entities.StorageOwner;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class StorageOwnerRepository extends BaseRepository<StorageOwner> {

    public List<StorageOwner> findByCity(String city) {
        return list("location.city", city);
    }
}
