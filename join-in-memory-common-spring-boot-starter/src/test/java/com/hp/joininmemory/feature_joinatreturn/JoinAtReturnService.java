package com.hp.joininmemory.feature_joinatreturn;

import com.google.common.collect.Lists;
import com.hp.common.base.model.PageResponse;
import com.hp.joininmemory.annotation.JoinAtReturn;
import com.hp.joininmemory.feature_groupedjoin.JoinTester;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author hp
 */

@Service
public class JoinAtReturnService {

    @JoinAtReturn
    public List<JoinTester> joinAtReturn() {
        final JoinTester joinTester = new JoinTester("1", "2");
        joinTester.setRemovedBy(3L);
        final JoinTester joinTester2 = new JoinTester("1", "2");
        joinTester.setRemovedBy(3L);
        return Lists.newArrayList(joinTester, joinTester2);
    }

    @JoinAtReturn
    public Optional<JoinTester> joinAtReturnOptional() {
        final JoinTester joinTester = new JoinTester("1", "2");
        joinTester.setRemovedBy(3L);
        return Optional.of(joinTester);
    }

    @JoinAtReturn("#this.list")
    public PageResponse<JoinTester> joinPageAtReturn() {
        final JoinTester joinTester = new JoinTester("1", "2");
        joinTester.setRemovedBy(3L);
        final JoinTester joinTester2 = new JoinTester("1", "2");
        joinTester.setRemovedBy(3L);
        final List<JoinTester> joinTesters = Lists.newArrayList(joinTester, joinTester2);
        return PageResponse.of(joinTesters, 2L, 1, 2);
    }

    @JoinAtReturn
    public PageResponse<JoinTester> joinPageAtReturnV2() {
        final JoinTester joinTester = new JoinTester("1", "2");
        joinTester.setRemovedBy(3L);
        final JoinTester joinTester2 = new JoinTester("1", "2");
        joinTester.setRemovedBy(3L);
        final List<JoinTester> joinTesters = Lists.newArrayList(joinTester, joinTester2);
        return PageResponse.of(joinTesters, 2L, 1, 2);
    }
}
