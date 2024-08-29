package com.hp.dingtalk.pojo.oa;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.dingtalk.api.request.OapiProcessinstanceCreateRequest;
import com.google.common.base.Preconditions;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Objects;

/**
 * @author hp
 */
@Data
@AllArgsConstructor
public class CreateProcessInstanceRequest {

    private String processCode;
    private String userId;
    private Long deptId;
    private List<String> approvers;
    private List<OapiProcessinstanceCreateRequest.ProcessInstanceApproverVo> approversV2;
    private List<String> ccList;
    private CCPosition ccPosition;
    private List<OapiProcessinstanceCreateRequest.FormComponentValueVo> formComponentValueVos;

    private CreateProcessInstanceRequest() {
    }

    @Data
    public static class Builder {
        private String processCode;
        private String userId;
        private Long deptId;
        private List<String> approvers;
        private List<OapiProcessinstanceCreateRequest.ProcessInstanceApproverVo> approversV2;
        private List<String> ccList;
        private CCPosition ccPosition;
        private List<OapiProcessinstanceCreateRequest.FormComponentValueVo> formComponentValueVos;

        public Builder(
                String processCode,
                String userId,
                Long deptId,
                List<OapiProcessinstanceCreateRequest.FormComponentValueVo> formComponentValueVos
        ) {
            Preconditions.checkArgument(StrUtil.isNotEmpty(processCode), "审批模版编号不能为空");
            Preconditions.checkArgument(StrUtil.isNotEmpty(userId), "发起人id不能为空");
            Preconditions.checkArgument(Objects.nonNull(deptId), "发起人部门id不能为空");
            Preconditions.checkArgument(CollUtil.isNotEmpty(formComponentValueVos), "表单项不能为空");
            this.processCode = processCode;
            this.userId = userId;
            this.deptId = deptId;
            this.formComponentValueVos = formComponentValueVos;
        }

        public Builder approvers(List<String> approvers) {
            return approvers(approvers, null, null);
        }

        public Builder approversV2(List<OapiProcessinstanceCreateRequest.ProcessInstanceApproverVo> approversV2) {
            return approversV2(approversV2, null, null);
        }

        public Builder approvers(
                List<String> approvers,
                List<String> ccList
        ) {
            this.approvers = approvers;
            this.ccList = ccList;
            return this;
        }

        public Builder approvers(
                List<String> approvers,
                List<String> ccList,
                CCPosition ccPosition
        ) {
            this.approvers = approvers;
            this.ccList = ccList;
            this.ccPosition = ccPosition;
            return this;
        }

        public Builder approversV2(
                List<OapiProcessinstanceCreateRequest.ProcessInstanceApproverVo> approversV2,
                List<String> ccList
        ) {
            this.approversV2 = approversV2;
            this.ccList = ccList;
            return this;
        }

        public Builder approversV2(
                List<OapiProcessinstanceCreateRequest.ProcessInstanceApproverVo> approversV2,
                List<String> ccList,
                CCPosition ccPosition
        ) {
            this.approversV2 = approversV2;
            this.ccList = ccList;
            this.ccPosition = ccPosition;
            return this;
        }

        public CreateProcessInstanceRequest build() {
            if (CollUtil.isNotEmpty(approvers)) {
                Preconditions.checkArgument(approvers.size() <= 20,
                        "审批人userid列表，最大列表长度20。多个审批人用逗号分隔，按传入的顺序依次审批。");
            }
            if (CollUtil.isNotEmpty(ccList)) {
                Preconditions.checkArgument(ccList.size() <= 20,
                        "抄送人userid列表，最大列表长度20。");
            }
            if (CollUtil.isNotEmpty(approversV2)) {
                Preconditions.checkArgument(approversV2.size() <= 20,
                        "审批人列表，最大列表长度20。支持会签/或签，优先级高于approvers变量。");
                approversV2.forEach(approver -> {
                    final List<String> userIds = approver.getUserIds();
                    Preconditions.checkArgument(CollUtil.isNotEmpty(userIds) && userIds.size() <= 20,
                            "审批人userid列表：\n" +
                                    "\n" +
                                    "会签/或签列表长度必须大于1\n" +
                                    "\n" +
                                    "非会签/或签列表长度只能为1\n" +
                                    "\n" +
                                    "最大列表长度20。");
                });
            }
            return new CreateProcessInstanceRequest(
                    processCode,
                    userId,
                    deptId,
                    approvers,
                    approversV2,
                    ccList,
                    ccPosition,
                    formComponentValueVos
            );
        }
    }

    public OapiProcessinstanceCreateRequest request() {
        OapiProcessinstanceCreateRequest req = new OapiProcessinstanceCreateRequest();
        req.setProcessCode(processCode);
        req.setOriginatorUserId(userId);
        req.setDeptId(deptId);
        req.setFormComponentValues(formComponentValueVos);
        if (CollUtil.isNotEmpty(approvers)) {
            req.setApprovers(String.join(",", approvers));
        }
        if (CollUtil.isNotEmpty(approversV2)) {
            req.setApproversV2(approversV2);
        }
        if (CollUtil.isNotEmpty(ccList)) {
            req.setCcList(String.join(",", ccList));
        }
        if (Objects.nonNull(ccPosition)) {
            req.setCcPosition(ccPosition.name());
        }
        return req;
    }

    public enum CCPosition {
        START,
        FINISH,
        START_FINISH
    }
}
