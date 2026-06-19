package com.vintagecargarage.service;

import com.vintagecargarage.dtos.CarDto;
import com.vintagecargarage.dtos.CarInputDto;
import com.vintagecargarage.dtos.CarStatusDto;
import com.vintagecargarage.entities.Car;
import com.vintagecargarage.entities.CarOwner;
import com.vintagecargarage.entities.Service;
import com.vintagecargarage.mapper.CarMapper;
import com.vintagecargarage.repository.CarOwnerRepository;
import com.vintagecargarage.repository.CarRepository;
import com.vintagecargarage.repository.ServiceRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class CarService {

    @Inject
    CarOwnerRepository carOwnerRepository;

    @Inject
    CarRepository carRepository;

    @Inject
    ServiceRepository serviceRepository;

    @Inject
    CarMapper mapper;

    @Transactional
    public CarDto createCar(String carOwnerId, CarInputDto dto) {
        CarOwner owner = carOwnerRepository.findById(carOwnerId);
        Car car = owner.createCar(dto.licensePlate());
        carRepository.persist(car);
        return mapper.toDto(car);
    }

    @Transactional
    public CarDto updateLicensePlate(String id, CarInputDto dto) {
        Car car = carRepository.findById(id);
        car.updateLicensePlate(dto.licensePlate());
        return mapper.toDto(car);
    }

    @Transactional
    public CarDto updateStatus(String id, CarStatusDto dto) {
        Car car = carRepository.findById(id);
        car.updateStatus(dto.readyToDrive(), dto.isMaintained());
        return mapper.toDto(car);
    }

    @Transactional
    public CarDto bookService(String carId, String serviceId) {
        Car car = carRepository.findById(carId);
        Service service = serviceRepository.findById(serviceId);
        car.bookService(service);
        return mapper.toDto(car);
    }

    @Transactional
    public void deleteCar(String id) {
        carRepository.delete(carRepository.findById(id));
    }
}
