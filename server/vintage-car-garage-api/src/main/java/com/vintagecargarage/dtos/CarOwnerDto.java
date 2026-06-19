package com.vintagecargarage.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CarOwnerDto(
                String email,
                @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) String password,
                String firstName,
                String lastName) {
}
