package com.vintagecargarage.service;

import com.vintagecargarage.dtos.AppointmentDto;
import com.vintagecargarage.dtos.AppointmentInputDto;
import com.vintagecargarage.entities.Appointment;
import com.vintagecargarage.entities.Car;
import com.vintagecargarage.entities.CarOwner;
import com.vintagecargarage.entities.StorageOwner;
import com.vintagecargarage.mapper.AppointmentMapper;
import com.vintagecargarage.repository.AppointmentRepository;
import com.vintagecargarage.repository.CarOwnerRepository;
import com.vintagecargarage.repository.CarRepository;
import com.vintagecargarage.repository.StorageOwnerRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class AppointmentService {

    @Inject
    CarOwnerRepository carOwnerRepository;

    @Inject
    StorageOwnerRepository storageOwnerRepository;

    @Inject
    CarRepository carRepository;

    @Inject
    AppointmentRepository appointmentRepository;

    @Inject
    AppointmentMapper mapper;

    @Transactional
    public AppointmentDto createAppointment(String carOwnerId, AppointmentInputDto dto) {
        CarOwner carOwner = carOwnerRepository.findById(carOwnerId);
        StorageOwner storageOwner = storageOwnerRepository.findById(dto.storageOwnerId());
        Car car = carRepository.findById(dto.carId());
        Appointment appointment = carOwner.createAppointment(storageOwner, car, dto.date(), dto.time());
        appointmentRepository.persist(appointment);
        return mapper.toDto(appointment);
    }
}
