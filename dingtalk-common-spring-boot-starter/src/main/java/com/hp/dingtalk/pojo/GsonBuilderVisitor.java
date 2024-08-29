package com.hp.dingtalk.pojo;

import com.google.gson.GsonBuilder;

/**
 * @author hp
 */
@FunctionalInterface
public interface GsonBuilderVisitor {

    /**
     * Customize gson builder accordingly.
     *
     * @param builder GsonBuilder
     */
    void customize(GsonBuilder builder);
}
