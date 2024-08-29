package com.hp.id;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Objects;

/**
 * @author hp
 */
public class IdHelper implements ApplicationContextAware {

    private static IdGenerator idGenerator;

    public static long nextId() {
        if (Objects.isNull(idGenerator)) {
            throw new IllegalStateException("The idGenerator is not properly instantiated.");
        }
        return idGenerator.nextId();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        idGenerator = applicationContext.getBean(IdGenerator.class);
    }
}
