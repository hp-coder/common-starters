package com.hp.joininmemory.feature_filter;

import lombok.Data;

/**
 * @author hp
 */
@Data
public class JoinFilteringDataTester {

    String createdBy;

    @JoinFilterDataUsernameOnUserId(value = "createdBy", sourceDataFilter = "T(java.lang.Long).parseLong(createdBy) > 2")
    String creator;

    String updatedBy;

    @JoinFilterDataUsernameOnUserId(value = "updatedBy", joinDataFilter = "T(java.lang.Long).parseLong(id) > 3")
    String updater;

    Long removedBy;

    @JoinFilterDataUsernameOnUserId(value = "removedBy")
    String remover;

    public JoinFilteringDataTester(String createdBy, String updatedBy, Long removedBy) {
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
        this.removedBy = removedBy;
    }
}
