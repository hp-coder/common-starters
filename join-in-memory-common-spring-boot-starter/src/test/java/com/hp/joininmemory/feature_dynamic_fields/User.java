package com.hp.joininmemory.feature_dynamic_fields;

import lombok.Data;

@Data
public class User {

    private Long userId;

    private String username;

    public User(Long userId) {
        this.userId = userId;
        this.username = "User-"+userId;
    }
}
