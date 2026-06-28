package com.vintagecargarage.application.dtos.user;

import java.util.List;

public record StorageDto(
        List<String> conditions,
        int capacity) {
}