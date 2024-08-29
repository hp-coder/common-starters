package com.hp.jpa;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * @author hp
 * @date 2022/10/19
 */
@Configuration
@RequiredArgsConstructor
public class SpringDataJpaOrmAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(value = JPAQueryFactory.class)
    public JPAQueryFactory jpaQueryFactory(EntityManager entityManager) {
        return new JPAQueryFactory(entityManager);
    }

}
