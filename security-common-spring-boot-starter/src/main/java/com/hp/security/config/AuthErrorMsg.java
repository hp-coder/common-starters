package com.hp.security.config;

import com.hp.common.base.enums.BaseEnum;
import org.springframework.http.HttpStatus;

import java.util.Optional;

public enum AuthErrorMsg implements BaseEnum<AuthErrorMsg,Integer> {
  /**
   * 认证
   */
  forceLoginOut(HttpStatus.INTERNAL_SERVER_ERROR.value(),"您的账号在其他设备登录"),
  hasUserOnline(HttpStatus.INTERNAL_SERVER_ERROR.value(),"当前已有用户在登录"),
  tokenInvalid(HttpStatus.UNAUTHORIZED.value(), "登录已经过期，请重新登录"),
  tokenIllegal(HttpStatus.UNAUTHORIZED.value(),"无效令牌"),
  methodNotSupport(HttpStatus.METHOD_NOT_ALLOWED.value(),"请求的方法不支持"),
  passwordIncorrect(HttpStatus.INTERNAL_SERVER_ERROR.value(),"用户名或者密码错误"),
  accessDenied(HttpStatus.INTERNAL_SERVER_ERROR.value(),"没有操作该功能的权限"),
  verifyCodeIncorrect(HttpStatus.INTERNAL_SERVER_ERROR.value(),"验证码不正确"),
  accountNotExist(HttpStatus.INTERNAL_SERVER_ERROR.value(),"账户不存在"),
  tokenExpired(HttpStatus.UNAUTHORIZED.value(),"登录已经过期，请重新登录"),
  dataInvalid(HttpStatus.INTERNAL_SERVER_ERROR.value(),"数据格式不正确"),
  requestFail(HttpStatus.INTERNAL_SERVER_ERROR.value(),"请求失败"),
  AccountIsLock(HttpStatus.INTERNAL_SERVER_ERROR.value(),"账号已经被禁用"),
  AuthFailure(HttpStatus.UNAUTHORIZED.value(),"认证失败"),
  DeviceNotSupport(HttpStatus.INTERNAL_SERVER_ERROR.value(),"设备未识别");

  AuthErrorMsg(Integer code, String name) {
    this.code = code;
    this.name = name;
  }

  private Integer code;
  private String name;

  @Override
  public Integer getCode() {
    return this.code;
  }

  @Override
  public String getName() {
    return this.name;
  }

  public static Optional<AuthErrorMsg> of(Integer code) {
    return Optional.ofNullable(
        BaseEnum.parseByCode(AuthErrorMsg.class, code));
  }

}
