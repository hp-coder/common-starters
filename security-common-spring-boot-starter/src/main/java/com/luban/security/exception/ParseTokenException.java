package com.luban.security.exception;

/**
 * @author hp
 */
public class ParseTokenException extends RuntimeException{

  private static final long serialVersionUID = 8656006470182321029L;

  public ParseTokenException(Integer code, String msg){
    super(msg);
    this.code = code;
    this.msg = msg;
  }
  private Integer code;

  private String msg;

  public Integer getCode() {
    return code;
  }

  public String getMsg() {
    return msg;
  }
}
