package com.vintagecargarage.entities;

import java.util.List;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Storage {
    @ElementCollection
    private List<String> conditions;
    private int capacity;

    public Storage(List<String> conditions, int capacity) {
        this.conditions = conditions;
        this.capacity = capacity;
    }
}
