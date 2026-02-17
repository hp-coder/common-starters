
package com.hp.joininmemory.issue_spel;

import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author hp
 */
@Component
public class UserRepositoryAdapter implements UserRepository {
    @Override
    public List<User> findAllByIdIn(Collection<Long> ids) {
        return ids.stream()
                .map(id -> {
                    final User user = new User();
                    user.setId(id);
                    user.setName(id.toString() + "Name");
                    return user;
                })
                .collect(Collectors.toList());
    }
}
