package com.hp.joininmemory.issue4.repository;

import com.google.common.collect.Lists;
import com.hp.joininmemory.issue4.JoinInnerLevel5Model;
import com.hp.joininmemory.issue4.JoinInnerLevel6Model;
import com.hp.joininmemory.issue4.JoinInnerModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author hp
 */
@Slf4j
@Component
public class JoinInnerRepository {

    public List<JoinInnerModel> findAllById(Collection<Long> ids) {
        return Lists.newArrayList(new JoinInnerModel(1L));
    }

    public List<JoinInnerLevel5Model> findAllLevel5ByInnerId(Collection<Long> ids) {
        log.warn("level5执行了. {}", ids);
        return ids.stream()
                .map(JoinInnerLevel5Model::new)
                .collect(Collectors.toList());
    }

    public List<JoinInnerLevel6Model> findAllLevel6ByLevel5Id(Collection<Long> ids) {
        log.warn("level6执行了. {}", ids);
        return ids.stream()
                .flatMap(i -> Lists.newArrayList(new JoinInnerLevel6Model(i, 1L), new JoinInnerLevel6Model(i, 2L)).stream())
                .collect(Collectors.toList());
    }
}
