package com.hp.joininmemory.feature_dynamicparam;

import lombok.Data;

/**
 * @author hp
 */
@Data
public class JoinDynamicParamTester {

    String createdBy;

    @JoinWithDynamicParamUsernameOnUserId(value = "createdBy", param1 = "T(com.hp.joininmemory.feature_dynamicparam.JoinDynamicParamTester).creatorParam")
    String creator;

    String updatedBy;

    @JoinWithDynamicParamUsernameOnUserId(value = "updatedBy", param1 = "T(com.hp.joininmemory.feature_dynamicparam.JoinDynamicParamTester).updaterParam")
    String updater;

    Long removedBy;

    @JoinWithDynamicParamUsernameOnUserId(value = "removedBy", param1 = "T(com.hp.joininmemory.feature_dynamicparam.JoinDynamicParamTester).removerParam")
    String remover;

    public static String creatorParam = "A";
    public static String updaterParam = "B";
    public static String removerParam = "C";

    public JoinDynamicParamTester(String createdBy, String updatedBy, Long removedBy) {
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
        this.removedBy = removedBy;
    }
}
