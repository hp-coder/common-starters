package com.hp.dingtalk.service;

import com.dingtalk.api.response.OapiExtcontactGetResponse;
import com.dingtalk.api.response.OapiExtcontactListResponse;
import com.dingtalk.api.response.OapiExtcontactListlabelgroupsResponse;
import lombok.NonNull;

import java.util.List;

/**
 * 钉钉企业外部联系人接口
 * <p>
 * https://www.dingtalk.com/qidian/help-detail-13488021.html
 * 您好，一个团队/企业可添加的外部联系人数量，根据企业等级不同上限不一样哦，数量限制仅针对整个企业总共可添加的数量，无法针对员工个人添加数量做限制。
 * 未认证的【团队】最多可添加20000人；
 * 已经认证的【中级、高级企业】最多可添加20万人。
 * 温馨提示：单次导入最多1万人，人数较多可以分批导入，每次导入一部分后请确认是否正常再继续导入，excel文件大小不要超过5M。
 *
 * @author hp
 */
public interface IDingExtContactHandler {


    /**
     * 获取外部联系人详情
     *
     * @param userId 钉钉外部联系人userId
     * @return 联系人信息
     */
    OapiExtcontactGetResponse.OpenExtContact getExtContactByDingUserId(@NonNull String userId);

    /**
     * 添加外部联系人
     *
     * @param title          职位
     * @param labelIds       标签 一次最多传20个
     * @param shareDeptIds   共享给的部门ID 一次最多20个
     * @param address        地址
     * @param remark         备注
     * @param followerUserId 负责人的userId
     * @param name           外部联系人的姓名
     * @param stateCode      手机号国家码
     * @param companyName    外部联系人的企业名称
     * @param shareUserIds   共享给的员工userid列表 一次最多20个
     * @param mobile         外部联系人的手机号
     * @return
     */
    String addExtContact(String title, List<Long> labelIds, List<Long> shareDeptIds,
                         String address, String remark, String followerUserId, String name,
                         String stateCode, String companyName, List<String> shareUserIds, String mobile
    );

    /**
     * 添加外部联系人
     *
     * @param labelIds       标签 一次最多传2个
     * @param followerUserId 负责人的userId
     * @param address        地址
     * @param name           外部联系人的姓名
     * @param stateCode      手机号国家码
     * @param mobile         外部联系人的手机号
     * @return
     */
    String addExtContact(List<Long> labelIds, String followerUserId, String address, String name, String companyName, String stateCode, String mobile);

    /**
     * 添加外部联系人
     *
     * @param labelIds       标签 一次最多传20个
     * @param followerUserId 负责人的userId
     * @param address        地址
     * @param name           外部联系人的姓名
     * @param mobile         外部联系人的手机号
     * @return 外部联系人userId
     */
    String addExtContact(List<Long> labelIds, String followerUserId, String address, String name, String companyName, String mobile);

    /**
     * 更新外部联系人
     *
     * @param userId         外部联系人userId
     * @param title          职位
     * @param labelIds       标签 一次最多传20个
     * @param shareDeptIds   共享给的部门ID 一次最多20个
     * @param address        地址
     * @param remark         备注
     * @param followerUserId 负责人的userId
     * @param name           外部联系人的姓名
     * @param companyName    外部联系人的企业名称
     * @param shareUserIds   共享给的员工userid列表 一次最多20个
     */
    void updateExtContact(String userId, String title, List<Long> labelIds, List<Long> shareDeptIds,
                          String address, String remark, String followerUserId, String name,
                          String companyName, List<String> shareUserIds);


    /**
     * 更新外部联系人
     *
     * @param userId         外部联系人userId
     * @param labelIds       标签 一次最多传20个
     * @param followerUserId 负责人的userId
     * @param address        企业地址
     * @param name           外部联系人的姓名
     * @param companyName    企业名称
     * @param mobile         电话
     */
    void updateExtContact(String userId, List<Long> labelIds, String followerUserId, String address, String name, String companyName, String mobile);

    /**
     * 删除外部联系人
     * 33012 无效的USERID
     *
     * @param userId 外部联系人userId
     */
    void deleteExtContactByDingUserId(@NonNull String userId);


    /**
     * 获取外部联系人列表
     *
     * @param page 页码 从1开始。
     * @param size 条数 最大100
     * @return 外部联系人列表
     */
    List<OapiExtcontactListResponse.OpenExtContact> getExtContacts(@NonNull Long page, @NonNull Long size);

    /**
     * 获取外部联系人标签列表
     *
     * @param page 页码 从1开始。
     * @param size 条数 最大100
     * @return 外部联系人标签列表
     */
    List<OapiExtcontactListlabelgroupsResponse.OpenLabelGroup> getExtContactTags(@NonNull Long page, @NonNull Long size);
}
