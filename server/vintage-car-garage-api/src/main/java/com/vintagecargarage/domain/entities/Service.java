package com.vintagecargarage.domain.entities;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Service extends BaseEntity {
    private String title;
    private String description;
    private float price;
    private boolean isOptional = false;

    Service(String title, String description, float price, boolean isOptional) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.isOptional = isOptional;
    }
}
