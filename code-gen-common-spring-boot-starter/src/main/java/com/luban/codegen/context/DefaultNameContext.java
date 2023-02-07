package com.luban.codegen.context;

import lombok.Data;

/**
 * 默认名称名称上下文，便于其他引入类方便使用
 */
@Data
public class DefaultNameContext {

  private String voPackageName;

  private String voClassName;

  private String dtoPackageName;

  private String dtoClassName;

  private String mapperPackageName;

  private String mapperClassName;

  private String repositoryPackageName;

  private String repositoryClassName;

  private String servicePackageName;

  private String serviceClassName;

  private String implPackageName;

  private String implClassName;

  private String controllerPackageName;

  private String controllerClassName;

  /**
   * API 相关
   */
  private String requestPackageName;

  private String requestClassName;

  private String responsePackageName;

  private String responseClassName;

  private String feignPackageName;

  private String feignClassName;

}
