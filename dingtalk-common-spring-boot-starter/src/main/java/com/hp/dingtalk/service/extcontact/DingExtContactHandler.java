package com.hp.dingtalk.service.extcontact;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.dingtalk.api.request.*;
import com.dingtalk.api.response.OapiExtcontactCreateResponse;
import com.dingtalk.api.response.OapiExtcontactGetResponse;
import com.dingtalk.api.response.OapiExtcontactListResponse;
import com.dingtalk.api.response.OapiExtcontactListlabelgroupsResponse;
import com.google.common.base.Preconditions;
import com.hp.dingtalk.service.AbstractDingApiHandler;
import com.hp.dingtalk.component.application.IDingMiniH5;
import com.hp.dingtalk.service.IDingExtContactHandler;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.hp.dingtalk.constant.DingUrlConstant.ExternalContact.*;

/**
 * 钉钉企业外部联系人接口
 *
 * @author hp
 */
@Slf4j
public class DingExtContactHandler extends AbstractDingApiHandler implements IDingExtContactHandler {
    public DingExtContactHandler(@NonNull IDingMiniH5 app) {
        super(app);
    }

    @Override
    public OapiExtcontactGetResponse.OpenExtContact getExtContactByDingUserId(@NonNull String userId) {
        OapiExtcontactGetRequest request = new OapiExtcontactGetRequest();
        request.setUserId(userId);
        final OapiExtcontactGetResponse response = execute(
                GET_EXTCONTACT_INFO,
                request,
                () -> "获取钉钉外部联系人"
        );
        return response.getResult();
    }

    public String addExtContact(OapiExtcontactCreateRequest.OpenExtContact payload) {
        OapiExtcontactCreateRequest request = new OapiExtcontactCreateRequest();
        request.setContact(payload);
        final OapiExtcontactCreateResponse response = execute(
                ADD_EXTCONTACT,
                request,
                () -> "添加外部联系人"
        );
        return response.getUserid();
    }


    @Override
    public String addExtContact(String title, List<Long> labelIds, List<Long> shareDeptIds,
                                String address, String remark, String followerUserId, String name,
                                String stateCode, String companyName, List<String> shareUserIds, String mobile
    ) {
        Preconditions.checkNotNull(labelIds, "联系人标签不能为空");
        Preconditions.checkArgument(StrUtil.isNotEmpty(followerUserId), "关注用户ID不能为空");
        Preconditions.checkArgument(StrUtil.isNotEmpty(name), "联系人名称不能为空");
        Preconditions.checkArgument(StrUtil.isNotEmpty(mobile), "联系人电话不能为空");
        Preconditions.checkArgument(labelIds.size() <= 20, "labelIds一次最多20个");
        Preconditions.checkArgument(StrUtil.isNotEmpty(address), "地址不能为空");
        Preconditions.checkArgument(StrUtil.isNotEmpty(companyName), "企业名称不能为空");
        if (!StringUtils.hasText(stateCode)) {
            stateCode = "86";
        }
        if (CollUtil.isNotEmpty(shareDeptIds)) {
            Preconditions.checkArgument(shareDeptIds.size() <= 20, "shareDeptIds一次最多20个");
        }
        if (CollUtil.isNotEmpty(shareUserIds)) {
            Preconditions.checkArgument(shareUserIds.size() <= 20, "shareUserIds一次最多20个");
        }
        OapiExtcontactCreateRequest.OpenExtContact contact = new OapiExtcontactCreateRequest.OpenExtContact();
        contact.setTitle(title);
        contact.setLabelIds(labelIds);
        contact.setShareDeptIds(shareDeptIds);
        contact.setFollowerUserId(followerUserId);
        contact.setName(name);
        contact.setAddress(address);
        contact.setStateCode(stateCode);
        contact.setCompanyName(companyName);
        contact.setShareUserIds(shareUserIds);
        contact.setMobile(mobile);
        contact.setRemark(remark);
        return this.addExtContact(contact);
    }

    @Override
    public String addExtContact(List<Long> labelIds, String followerUserId, String address, String name, String stateCode, String companyName, String mobile) {
        return addExtContact(null, labelIds, null,
                address, null, followerUserId, name,
                stateCode, companyName, null, mobile);
    }

    @Override
    public String addExtContact(List<Long> labelIds, String followerUserId, String address, String name, String companyName, String mobile) {
        return addExtContact(labelIds, followerUserId, address, name, null, companyName, mobile);
    }

    @Override
    public void updateExtContact(String userId, String title, List<Long> labelIds, List<Long> shareDeptIds,
                                 String address, String remark, String followerUserId, String name,
                                 String companyName, List<String> shareUserIds) {
        Preconditions.checkArgument(StrUtil.isNotEmpty(userId), "联系人userId不能为空");
        Preconditions.checkArgument(CollUtil.isNotEmpty(labelIds), "联系人标签不能为空");
        Preconditions.checkArgument(StrUtil.isNotEmpty(followerUserId), "关注用户ID不能为空");
        Preconditions.checkArgument(StrUtil.isNotEmpty(name), "联系人名称不能为空");
        Preconditions.checkArgument(labelIds.size() <= 20, "labelIds一次最多20个");
        if (CollUtil.isNotEmpty(shareDeptIds)) {
            Preconditions.checkArgument(shareDeptIds.size() <= 20, "shareDeptIds一次最多20个");
        }
        if (CollUtil.isNotEmpty(shareUserIds)) {
            Preconditions.checkArgument(shareUserIds.size() <= 20, "shareUserIds一次最多20个");
        }
        OapiExtcontactUpdateRequest.OpenExtContact contact = new OapiExtcontactUpdateRequest.OpenExtContact();
        contact.setTitle(title);
        contact.setLabelIds(labelIds);
        contact.setShareDeptIds(shareDeptIds);
        contact.setAddress(address);
        contact.setRemark(remark);
        contact.setFollowerUserId(followerUserId);
        contact.setName(name);
        contact.setUserId(userId);
        contact.setCompanyName(companyName);
        contact.setShareUserIds(shareUserIds);
        this.updateExtContact(contact);
    }

    @Override
    public void updateExtContact(String userId, List<Long> labelIds, String followerUserId, String address, String name, String companyName, String mobile) {
        this.updateExtContact(userId, null, labelIds, null, address, null, followerUserId, name, companyName, null);
    }

    public void updateExtContact(OapiExtcontactUpdateRequest.OpenExtContact payload) {
        OapiExtcontactUpdateRequest request = new OapiExtcontactUpdateRequest();
        request.setContact(payload);
        execute(
                UPDATE_EXTCONTACT,
                request,
                () -> "更新外部联系人"
        );
    }

    @Override
    public void deleteExtContactByDingUserId(@NonNull String userId) {
        OapiExtcontactDeleteRequest request = new OapiExtcontactDeleteRequest();
        request.setUserId(userId);
        execute(
                DELETE_EXTCONTACT,
                request,
                () -> "删除外部联系人"
        );
    }

    @Override
    public List<OapiExtcontactListResponse.OpenExtContact> getExtContacts(@NonNull Long page, @NonNull Long size) {
        Preconditions.checkArgument(page >= 1, "page必须大于1");
        Preconditions.checkArgument(size >= 0, "size必须大于0");
        OapiExtcontactListRequest request = new OapiExtcontactListRequest();
        request.setSize(size);
        request.setOffset((page - 1) * size);
        final OapiExtcontactListResponse response = execute(
                GET_EXTCONTACTS,
                request,
                () -> "获取外部联系人列表失败"
        );
        return response.getResults();
    }

    @Override
    public List<OapiExtcontactListlabelgroupsResponse.OpenLabelGroup> getExtContactTags(@NonNull Long page, @NonNull Long size) {
        Preconditions.checkArgument(page >= 1, "page必须大于1");
        Preconditions.checkArgument(size >= 0, "size必须大于0");
        OapiExtcontactListlabelgroupsRequest request = new OapiExtcontactListlabelgroupsRequest();
        request.setSize(size);
        request.setOffset((page - 1) * size);
        final OapiExtcontactListlabelgroupsResponse response = execute(
                GET_EXTCONTACT_TAGS,
                request,
                () -> "获取外部联系人标签列表"
        );
        return response.getResults();
    }
}
