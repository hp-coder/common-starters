package com.hp.joininmemory.feature_join_at_return_dynamic_join_fields;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @developers <a href="mailto:max_verstrappon@outlook.com">Hu Peng</a>
 * @date 2026/1/22
 */
@Data
@NoArgsConstructor
public class Order {

    private Long  id;

    public Order(Long id, Long createdBy, Long updatedBy) {
        this.id = id;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
    }

    private Long createdBy;

    private Long updatedBy;

    @JoinUsernameOnUserId("createdBy")
    private String creator;

    @JoinUsernameOnUserId("updatedBy")
    private String updater;
}
