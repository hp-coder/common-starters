package com.hp.security.base.extension;


import com.hp.security.base.BaseJwtUser;

/**
 * @author hp
 */
public interface UserContextAware {

  BaseJwtUser processToken(String token);

}
