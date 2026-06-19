package com.vintagecargarage.mapper;

import org.mapstruct.MappingTarget;

public interface BaseMapper<T, D> {
    T toEntity(D dto);

    D toDto(T entity);

    void updateFromDto(D dto, @MappingTarget T entity);
}