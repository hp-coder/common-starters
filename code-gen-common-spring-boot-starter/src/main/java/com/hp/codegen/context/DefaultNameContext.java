package com.hp.codegen.context;

import lombok.Data;

/**
 * 默认名称名称上下文，便于其他引入类方便使用
 */
@Data
public class DefaultNameContext {

  private String eventListenerPackageName;
  private String eventListenerClassName;

  private String eventPackageName;
  private String eventClassName;

  private String createContextPackageName;
  private String createContextClassName;

  private String createCommandPackageName;
  private String createCommandClassName;

  private String updateContextPackageName;
  private String updateContextClassName;

  private String updateCommandPackageName;
  private String updateCommandClassName;

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

  private String serviceImplPackageName;
  private String serviceImplClassName;

  private String controllerPackageName;
  private String controllerClassName;

  private String requestPackageName;
  private String requestClassName;

  private String createRequestPackageName;
  private String createRequestClassName;

  private String updateRequestPackageName;
  private String updateRequestClassName;

  private String pageRequestPackageName;
  private String pageRequestClassName;

  private String responsePackageName;
  private String responseClassName;

  private String pageResponsePackageName;
  private String pageResponseClassName;

  private String feignPackageName;
  private String feignClassName;

}
