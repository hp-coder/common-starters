package com.hp.jpa.id;

import com.hp.id.IdHelper;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.generator.BeforeExecutionGenerator;
import org.hibernate.generator.EventType;

import java.io.Serial;
import java.util.EnumSet;

/**
 * @author hp
 */
public class CustomIdGenerator implements BeforeExecutionGenerator {
    @Serial
    private static final long serialVersionUID = 6025980939363052682L;

    @Override
    public Object generate(SharedSessionContractImplementor session, Object owner, Object currentValue, EventType eventType) {
        return IdHelper.nextId();
    }

    @Override
    public EnumSet<EventType> getEventTypes() {
        return EnumSet.of(EventType.INSERT);
    }
}
