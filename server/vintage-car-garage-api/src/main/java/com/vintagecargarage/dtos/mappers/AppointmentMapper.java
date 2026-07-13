package com.vintagecargarage.dtos.mappers;

import com.vintagecargarage.dtos.appointment.AppointmentOutputDto;
import com.vintagecargarage.entities.Appointment;

public final class AppointmentMapper {

    private AppointmentMapper() {
    }

    public static AppointmentOutputDto toAppointmentOutputDto(Appointment a) {
        return new AppointmentOutputDto(a.getId(), a.getType(), a.getStatus(), a.getDate(), a.getTime(),
                a.getStorageOwner().getId(),
                a.getSubject() != null ? a.getSubject().getId() : null);
    }
}
