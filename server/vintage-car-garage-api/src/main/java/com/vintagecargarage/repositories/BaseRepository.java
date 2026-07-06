package com.vintagecargarage.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

public class BaseRepository<T> implements PanacheRepository<T> {

    public T findById(String id) {
        return find("id", id).firstResult();
    }

    public T findByEmail(String email) {
        return find("email", email).firstResult();
    }

    public boolean isEmailTaken(String email) {
        return this.findByEmail(email) != null;
    }
}
