package com.hp.jpa;

import org.springframework.data.repository.CrudRepository;

/**
 * @author hp
 */
@SuppressWarnings("unchecked")
public abstract class EntityOperations {

  public static <T, ID> EntityUpdater<T, ID> doUpdate(CrudRepository<T, ID> repository) {
    return new EntityUpdater<>(repository);
  }

  public static <T, ID> EntityCreator<T, ID> doCreate(CrudRepository<T, ID> repository) {
    return new EntityCreator(repository);
  }


}
