package com.vintagecargarage.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

public abstract class BaseRepository<T> implements PanacheRepository<T> {

    public T findById(String id) {
        return find("id", id).firstResult();
    }
}
