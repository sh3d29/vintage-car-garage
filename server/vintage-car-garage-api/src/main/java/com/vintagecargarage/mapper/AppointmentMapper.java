package com.vintagecargarage.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.vintagecargarage.dtos.AppointmentDto;
import com.vintagecargarage.entities.Appointment;

@Mapper(componentModel = "jakarta")
public interface AppointmentMapper {

    @Mapping(target = "carOwnerId", source = "carOwner.id")
    @Mapping(target = "storageOwnerId", source = "storageOwner.id")
    @Mapping(target = "subjectCarId", source = "subject.id")
    AppointmentDto toDto(Appointment entity);

    List<AppointmentDto> toDtoList(List<Appointment> entities);
}
