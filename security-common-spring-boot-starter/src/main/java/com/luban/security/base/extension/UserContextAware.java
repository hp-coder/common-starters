package com.luban.security.base.extension;


import com.luban.security.base.BaseJwtUser;

/**
 * @author hp
 */
public interface UserContextAware {

  BaseJwtUser processToken(String token);

}
