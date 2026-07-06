package com.vintagecargarage.dtos.car;

public record CarStatusDto(
        boolean readyToDrive,
        boolean isMaintained) {
}
