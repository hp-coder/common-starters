package com.luban.jpa;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;

/**
 * @author HP
 * @date 2022/10/19
 */
@Configuration
public class JPAQueryFactoryAutoConfiguration {

    @ConditionalOnMissingBean(value = JPAQueryFactory.class)
    @Bean
    public JPAQueryFactory jpaQueryFactory(EntityManager entityManager) {
        return new JPAQueryFactory(entityManager);
    }
}
