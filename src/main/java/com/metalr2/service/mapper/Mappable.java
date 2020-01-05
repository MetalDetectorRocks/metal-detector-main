package com.metalr2.service.mapper;

/**
 * Transforms an entity into a DTO and back.
 * The interface should be used for individual transformation requirements.
 * @param <E> The Entity class
 * @param <D> The DTO Class
 */
public interface Mappable<E, D> {

  E mapToEntity(D dto);

  D mapToDto(E entity);

}
