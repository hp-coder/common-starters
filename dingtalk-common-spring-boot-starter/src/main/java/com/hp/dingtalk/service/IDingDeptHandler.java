package com.hp.dingtalk.service;

import com.dingtalk.api.response.*;
import com.hp.dingtalk.constant.Language;
import lombok.NonNull;

import java.util.List;

/**
 * 钉钉部门
 *
 * @author hp
 */
public interface IDingDeptHandler {

    /**
     * 部门详情
     *
     * @param deptId   部门id
     * @param language 语言：zh_CN 或 en_US
     * @return 部门详情
     */
    OapiV2DepartmentGetResponse.DeptGetResponse getByDeptId(@NonNull Long deptId, @NonNull Language language);

    /**
     * 子部门id列表
     *
     * @param deptId 父部门id
     * @return 子部门id列表
     */
    OapiV2DepartmentListsubidResponse.DeptListSubIdResponse getDeptIdList(@NonNull Long deptId);

    /**
     * 获取所有部门
     *
     * @return 子部门集合
     */
    List<OapiV2DepartmentListsubResponse.DeptBaseResponse> getDeptList();

    /**
     * 获取所有部门
     *
     * @param deptId   指定父部门id
     * @param language 语言：zh_CN 或 en_US
     * @return 子部门集合
     */
    List<OapiV2DepartmentListsubResponse.DeptBaseResponse> getDeptList(Long deptId, @NonNull Language language);

    /**
     * 获取指定部门的所有父部门列表
     *
     * @param deptId 子部门id
     * @return 所有父部门列表
     */
    OapiV2DepartmentListparentbydeptResponse.DeptListParentByDeptIdResponse getParentDeptListByDeptId(@NonNull Long deptId);

    /**
     * 获取指定用户的所有父部门列表
     *
     * @param userId 用户id
     * @return 所有父部门列表
     */
    OapiV2DepartmentListparentbyuserResponse.DeptListParentByUserResponse getParentDeptListByUserId(@NonNull String userId);
}
