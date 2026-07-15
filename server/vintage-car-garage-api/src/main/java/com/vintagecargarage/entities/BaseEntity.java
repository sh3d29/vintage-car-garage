package com.vintagecargarage.entities;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

/** Gemeinsame Oberklasse aller Entwurfsklassen: liefert die Objektidentität (UUID). */
@Getter
@MappedSuperclass
public abstract class BaseEntity {
    /** Objektidentität (UUID), beim Persistieren automatisch erzeugt */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
}
