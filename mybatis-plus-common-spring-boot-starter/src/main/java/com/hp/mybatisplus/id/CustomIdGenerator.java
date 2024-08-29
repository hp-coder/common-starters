package com.hp.mybatisplus.id;

import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.hp.id.IdHelper;

public class CustomIdGenerator implements IdentifierGenerator {

    @Override
    public Number nextId(Object entity) {
        return IdHelper.nextId();
    }

}
