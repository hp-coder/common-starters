package com.hp.dingtalk.service.oa;

import com.dingtalk.api.response.OapiProcessTemplateManageGetResponse;
import com.dingtalk.api.response.OapiProcessinstanceGetResponse;
import com.dingtalk.api.response.OapiProcessinstanceListidsResponse;
import com.hp.dingtalk.pojo.oa.CreateProcessInstanceRequest;
import lombok.NonNull;

import java.time.LocalDate;
import java.util.List;

/**
 * @author hp
 */
public interface IDingOldOAHandler {
    String createProcessInstance(@NonNull CreateProcessInstanceRequest request);

    /**
     * 获取用户可见的所有模版编号信息
     *
     * @param userId 用户id
     * @return 薄膜编号
     */
    List<OapiProcessTemplateManageGetResponse.ProcessSimpleVO> getManageableProcessTemplatesInCorp(@NonNull String userId);

    /**
     * 根据审批模版名称获取审批模版编号
     *
     * @param name 模版名称
     * @return 模版编号
     */
    String getProcessTemplateCodeByName(@NonNull String name);

    /**
     * 根据审批模版编码查询实例id列表
     *
     * @param processCode 模版编号
     * @param start       开始时间
     * @return 模版实例id列表 及 下次分页指针
     */
    OapiProcessinstanceListidsResponse.PageResult getProcessInstanceIds(@NonNull String processCode, @NonNull LocalDate start);

    /**
     * 根据审批模版编码查询实例id列表
     *
     * @param processCode 模版编号
     * @param start       开始时间
     * @param size        分页大小，最大20
     * @param cursor      分页指针
     * @return 模版实例id列表 及 下次分页指针
     */
    OapiProcessinstanceListidsResponse.PageResult getProcessInstanceIds(@NonNull String processCode, @NonNull LocalDate start, @NonNull Long size, @NonNull Long cursor);

    /**
     * 根据审批模版编码查询实例id列表
     *
     * @param processCode 模版编号
     * @param start       开始时间
     * @param end         结束时间
     * @param size        分页大小，最大20
     * @param cursor      分页指针
     * @return 模版实例id列表 及 下次分页指针
     */
    OapiProcessinstanceListidsResponse.PageResult getProcessInstanceIds(@NonNull String processCode, @NonNull LocalDate start, LocalDate end, @NonNull Long size, @NonNull Long cursor);

    /**
     * 根据审批模版编码查询实例id列表
     *
     * @param processCode 模版编号
     * @param start       开始时间
     * @param end         结束时间
     * @param size        分页大小，最大20
     * @param cursor      分页指针
     * @param userIds     发起请求的用户id
     * @return 模版实例id列表 及 下次分页指针
     */
    OapiProcessinstanceListidsResponse.PageResult getProcessInstanceIds(@NonNull String processCode, @NonNull LocalDate start, LocalDate end, @NonNull Long size, @NonNull Long cursor, List<String> userIds);

    /**
     * 获取审批实例信息
     *
     * @param processInstanceId 审批实例id
     * @return 审批实例
     */
    OapiProcessinstanceGetResponse.ProcessInstanceTopVo getProcessInstance(@NonNull String processInstanceId);

}
