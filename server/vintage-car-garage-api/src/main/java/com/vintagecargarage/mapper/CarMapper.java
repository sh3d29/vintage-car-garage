package com.vintagecargarage.mapper;

import java.util.List;
import java.util.Set;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.vintagecargarage.dtos.CarDto;
import com.vintagecargarage.entities.Car;
import com.vintagecargarage.entities.Service;

@Mapper(componentModel = "jakarta")
public interface CarMapper {

    @Mapping(target = "ownerId", source = "owner.id")
    @Mapping(target = "isMaintained", source = "maintained")
    CarDto toDto(Car entity);

    List<CarDto> toDtoList(List<Car> entities);

    default List<String> mapServices(Set<Service> services) {
        return services.stream().map(Service::getId).toList();
    }
}
