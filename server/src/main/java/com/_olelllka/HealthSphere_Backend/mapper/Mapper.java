package com._olelllka.HealthSphere_Backend.mapper;

public interface Mapper<T, B> {
    T toEntity(B b);
    B toDto(T t);
}
