package com.vintagecargarage.domain.entities;

import java.util.List;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class Storage {
    @ElementCollection
    private List<String> conditions;
    private int capacity;

    public Storage(List<String> conditions, int capacity) {
        this.conditions = conditions;
        this.capacity = capacity;
    }
}
