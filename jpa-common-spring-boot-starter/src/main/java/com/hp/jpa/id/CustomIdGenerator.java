package com.hp.jpa.id;

import com.hp.id.IdHelper;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serial;

/**
 * @author hp
 */
public class CustomIdGenerator implements IdentifierGenerator {
    @Serial
    private static final long serialVersionUID = 6025980939363052682L;

    @Override
    public Object generate(SharedSessionContractImplementor sharedSessionContractImplementor, Object o) {
        return IdHelper.nextId();
    }
}
