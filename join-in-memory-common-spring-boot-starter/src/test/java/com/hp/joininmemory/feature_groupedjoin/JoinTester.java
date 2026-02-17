package com.hp.joininmemory.feature_groupedjoin;

import lombok.Data;

/**
 * @author hp
 */
@Data
public class JoinTester {


    String createdBy;

    @JoinUsernameOnUserId(value = "createdBy")
    String creator;

    String updatedBy;

    @JoinUsernameOnUserId(value = "updatedBy")
    String updater;

    Long removedBy;

    @JoinUsernameOnUserId(value = "removedBy")
    String remover;

    public JoinTester(String createdBy, String updatedBy) {
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
    }
}
