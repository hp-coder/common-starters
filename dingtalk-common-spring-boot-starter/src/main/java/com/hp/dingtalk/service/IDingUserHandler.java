package com.hp.dingtalk.service;

import com.dingtalk.api.response.OapiUserListsimpleResponse;
import com.dingtalk.api.response.OapiV2UserGetResponse;
import com.dingtalk.api.response.OapiV2UserGetuserinfoResponse;
import com.hp.dingtalk.constant.DeptUserOrder;
import com.hp.dingtalk.constant.Language;
import lombok.NonNull;

/**
 * 钉钉用户信息处理器
 *
 * @author hp
 */
public interface IDingUserHandler {

    /**
     * 根据手机号查询专属帐号用户 手机获取企业内用户userId
     * <p>
     * 员工离职后，无法再通过手机号获取userId
     *
     * @param mobile 企业内钉钉用户电话
     * @return 钉钉userId
     */
    String findUserIdByMobile(@NonNull String mobile);

    /**
     * 根据回调code获取钉钉用户unionId
     *
     * @param code 前端回调后请求的code
     * @return 钉钉用户unionId
     */
    String findUnionIdByCode(@NonNull String code);

    /**
     * 根据钉钉用户unionId获取钉钉用户userId
     *
     * @param unionId 钉钉用户unionId
     * @return 钉钉用户userId
     */
    String findUserIdByUnionId(@NonNull String unionId);

    /**
     * 根据钉钉用户userId获取钉钉用户信息
     * 默认写死用中文了
     *
     * @param userId 钉钉用户userId
     * @return 钉钉用户信息json，两种情况，企业内和非企业内
     */
    OapiV2UserGetResponse.UserGetResponse findUserByUserId(@NonNull String userId);

    /**
     * 根据手机号获取钉钉用户信
     *
     * @param mobile 用户手机
     * @return 钉钉用户信息json，两种情况，企业内和非企业内
     */
    OapiV2UserGetResponse.UserGetResponse findUserByMobile(@NonNull String mobile);

    /**
     * 根据钉钉前端回调后提交的code获取钉钉用户信息json字符串
     *
     * @param code 前端回调后请求的code
     * @return 钉钉用户信息json，两种情况，企业内和非企业内
     */
    OapiV2UserGetResponse.UserGetResponse findUserByCode(@NonNull String code);

    /**
     * 通过免登码获取用户信息
     *
     * @param authCode 免登录授权码,此授权码五分钟内有效，且只能使用一次
     * @return 用户基本信息，包含unionId和userId
     */
    OapiV2UserGetuserinfoResponse.UserGetByCodeResponse findUserByLoginAuthCode(@NonNull  String authCode);

    /**
     * 获取部门下用户基本信息
     *
     * @param deptId 部门ID，如果是根部门，该参数传1。
     * @param cursor 分页查询的游标，最开始传0，后续传返回参数中的next_cursor值。
     * @param size   分页长度，最大值100。
     * @return PageResult;
     * has_more 是否还有更多的数据。
     * next_cursor 下一次分页的游标，如果has_more为false，表示没有更多的分页数据。
     * list{userid,name} 用户信息列表。
     */
    OapiUserListsimpleResponse.PageResult findNameAndUserIdByDept(Long deptId, Long cursor, Long size);

    /**
     * @param deptId             部门ID，如果是根部门，该参数传1。
     * @param cursor             分页查询的游标，最开始传0，后续传返回参数中的next_cursor值。
     * @param size               分页长度，最大值100。
     * @param order              部门成员的排序规则。默认值，custom。
     * @param containAccessLimit 是否返回访问受限的员工。
     * @param language           通讯录语言，取值。
     * @return PageResult;
     * has_more 是否还有更多的数据。
     * next_cursor 下一次分页的游标，如果has_more为false，表示没有更多的分页数据。
     * list{userid,name} 用户信息列表。
     */
    OapiUserListsimpleResponse.PageResult findNameAndUserIdByDept(
            Long deptId,
            Long cursor,
            Long size,
            DeptUserOrder order,
            Boolean containAccessLimit,
            Language language
    );
}
