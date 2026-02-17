package com.hp.joininmemory.issue_spel;

import java.util.Collection;
import java.util.List;

/**
 * @author hp
 */
public interface UserRepository {

    List<User> findAllByIdIn(Collection<Long> ids);
}
