package com.hp.joininmemory.issue4;

import lombok.Data;

/**
 * @author hp
 */
@Data
public class JoinInnerLevel6Model {

    private Long id;

    private Long level5Id;

    private String name;

    public JoinInnerLevel6Model(Long level5Id, Long id) {
        this.id = id;
        this.level5Id = level5Id;
    }

    public JoinInnerLevel6Model() {

    }
}
