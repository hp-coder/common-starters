package com.luban.security.base;

import com.luban.common.base.annotations.FieldDesc;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

/**
 * 基础session 数据 通过token 直接能换取的信息
 * @author hp
 */
@Data
public abstract class BaseJwtUser implements Serializable {

    private static final long serialVersionUID = 6312856382068055376L;
    @FieldDesc("用户Id")
    private Long userId;

    @FieldDesc("用户名")
    private String username;

    @FieldDesc("额外信息")
    private Map<String, String> extInfo;

    @FieldDesc("权限信息")
    private Collection<? extends GrantedAuthority> authorities;
}
