package com.hp.joininmemory.example;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author hp
 */
@SpringBootTest
public class SpELTests {

    // 用的是 StandardBeanExpressionResolver 有 prefix=#{ suffix=}的要求
    @Value("#{T(java.lang.Math).random()}")
    String random;

    @Autowired
    ApplicationContext applicationContext;

    static StandardEvaluationContext context;
    static ExpressionParser expressionParser;

    @BeforeAll
    public static void init() {
        context = new StandardEvaluationContext();
        expressionParser = new SpelExpressionParser();
    }

    @Test
    public void test_SpEL_without_templating_using_the_value_annotation() {
        System.out.println("random = " + random);
    }

    @Test
    public void test_SpEL_without_templating_calling_static_method() {
        context.setBeanResolver(new BeanFactoryResolver(applicationContext));
        final Expression expression = expressionParser.parseExpression("T(java.lang.Math).random()");
        final Double value = expression.getValue(context, Double.class);
        System.out.println("value = " + value);
    }

    @Test
    public void test_SpEL_without_templating_defining_bean_definition() {
        context.setBeanResolver(new BeanFactoryResolver(applicationContext));
        context.setVariable("ids", Lists.newArrayList(1L, 2L, 3L));
        final Expression expression = expressionParser.parseExpression("@joinRepository.findAllById(#ids)[0]");
        final JoinUser value = expression.getValue(context, JoinUser.class);
        assertThat(value).isNotNull();
        System.out.println("value = " + value);
    }
}
