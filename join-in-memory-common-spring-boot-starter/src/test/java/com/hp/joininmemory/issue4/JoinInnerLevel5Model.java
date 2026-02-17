package com.hp.joininmemory.issue4;

import lombok.Data;

/**
 * @author hp
 */
@Data
public class JoinInnerLevel5Model {

    private volatile Long id;

    private Long innerId;

    private String name;

    public JoinInnerLevel5Model() {
    }

    public JoinInnerLevel5Model(Long innerId) {
        this.id = innerId;
        this.innerId = innerId;
    }
}
