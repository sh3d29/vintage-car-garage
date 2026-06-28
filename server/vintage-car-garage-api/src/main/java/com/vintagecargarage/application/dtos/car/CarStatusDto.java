package com.vintagecargarage.application.dtos.car;

public record CarStatusDto(
        boolean readyToDrive,
        boolean isMaintained) {
}
