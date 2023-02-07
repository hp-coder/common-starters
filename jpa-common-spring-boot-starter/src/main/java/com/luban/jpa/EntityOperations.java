package com.hp.jpp;

import org.springframework.data.repository.CrudRepository;

/**
 * @author gim 2022/3/5 9:52 下午
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
