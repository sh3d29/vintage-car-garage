package com.vintagecargarage.entities;

import java.util.List;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Storage {
    @ElementCollection
    private List<String> conditions;
    private int capacity;

    public Storage(List<String> conditions, int capacity) {
        this.conditions = conditions;
        this.capacity = capacity;
    }
}
