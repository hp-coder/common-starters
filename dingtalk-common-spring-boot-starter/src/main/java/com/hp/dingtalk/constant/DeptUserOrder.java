package com.hp.dingtalk.constant;

/**
 * @author hp 2023/4/23
 */
public enum DeptUserOrder {

    /**
     * 代表按照进入部门的时间升序。
     */
    entry_asc,
    /**
     * 代表按照进入部门的时间降序。
     */
    entry_desc,
    /**
     * 代表按照部门信息修改时间升序。
     */
    modify_asc,
    /**
     * 代表按照部门信息修改时间降序。
     */
    modify_desc,
    /**
     * 代表用户定义(未定义时按照拼音)排序。
     */
    custom;
}
