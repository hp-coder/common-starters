package com.hp.dingtalk.service.dept;

import com.dingtalk.api.request.*;
import com.dingtalk.api.response.*;
import com.hp.dingtalk.constant.DingUrlConstant;
import com.hp.dingtalk.constant.Language;
import com.hp.dingtalk.service.AbstractDingApiHandler;
import com.hp.dingtalk.service.IDingDeptHandler;
import com.hp.dingtalk.component.application.IDingApp;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 钉钉部门
 *
 * @author hp
 */
@Slf4j
public class DingDeptHandler extends AbstractDingApiHandler implements IDingDeptHandler {

    public DingDeptHandler(@NonNull IDingApp app) {
        super(app);
    }

    @Override
    public OapiV2DepartmentGetResponse.DeptGetResponse getByDeptId(@NonNull Long deptId, @NonNull Language language) {
        OapiV2DepartmentGetRequest request = new OapiV2DepartmentGetRequest();
        request.setDeptId(deptId);
        request.setLanguage(language.name());
        final OapiV2DepartmentGetResponse response = execute(
                DingUrlConstant.Department.GET_DEPT_DETAIL,
                request,
                () -> "获取部门详情"
        );
        return response.getResult();
    }

    @Override
    public OapiV2DepartmentListsubidResponse.DeptListSubIdResponse getDeptIdList(@NonNull Long deptId) {
        OapiV2DepartmentListsubidRequest request = new OapiV2DepartmentListsubidRequest();
        request.setDeptId(deptId);
        OapiV2DepartmentListsubidResponse response = execute(
                DingUrlConstant.Department.GET_SUB_DEPT_ID_LIST,
                request,
                () -> "获取子部门ID列表"
        );
        return response.getResult();
    }

    @Override
    public List<OapiV2DepartmentListsubResponse.DeptBaseResponse> getDeptList() {
        return this.getDeptList(null, Language.zh_CN);
    }

    @Override
    public List<OapiV2DepartmentListsubResponse.DeptBaseResponse> getDeptList(Long deptId, @NotNull Language language) {
        OapiV2DepartmentListsubRequest request = new OapiV2DepartmentListsubRequest();
        request.setDeptId(deptId);
        request.setLanguage(language.name());
        final OapiV2DepartmentListsubResponse response = execute(
                DingUrlConstant.Department.GET_SUB_DEPT_LIST,
                request,
                () -> "获取部门列表"
        );
        return response.getResult();
    }

    @Override
    public OapiV2DepartmentListparentbydeptResponse.DeptListParentByDeptIdResponse getParentDeptListByDeptId(@NonNull Long deptId) {
        OapiV2DepartmentListparentbydeptRequest request = new OapiV2DepartmentListparentbydeptRequest();
        request.setDeptId(deptId);
        OapiV2DepartmentListparentbydeptResponse response = execute(
                DingUrlConstant.Department.GET_PARENT_DEPT_LIST_BY_DEPT_ID,
                request,
                () -> "获取指定部门的所有父部门列表"
        );
        return response.getResult();
    }

    @Override
    public OapiV2DepartmentListparentbyuserResponse.DeptListParentByUserResponse getParentDeptListByUserId(@NonNull String userId) {
        OapiV2DepartmentListparentbyuserRequest request = new OapiV2DepartmentListparentbyuserRequest();
        request.setUserid(userId);
        OapiV2DepartmentListparentbyuserResponse response = execute(
                DingUrlConstant.Department.GET_PARENT_DEPT_LIST_BY_USER_ID,
                request,
                () -> "获取指定用户的所有父部门列表");
        return response.getResult();
    }
}
