package com.hp.joininmemory.annotation;

import com.hp.joininmemory.constant.ExecuteLevel;
import com.hp.joininmemory.support.JoinInMemoryBasedJoinFieldExecutorFactory;
import org.intellij.lang.annotations.Language;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 内存连接注解的根注解，用于定义字段或类级别的内存数据连接配置。
 * <p>
 * 该注解支持通过 Spring SpEL 表达式实现灵活的数据过滤、键值提取、数据加载和结果转换等功能。
 * 适用于需要在内存中进行数据关联和转换的场景，可显著提升数据处理效率。
 * <p>
 * 使用方法请参考 {@code readme.md} 文档。
 * <p>
 * The root annotation for defining in-memory data join configurations at field or class level.
 * <p>
 * This annotation supports flexible data filtering, key extraction, data loading and result conversion
 * through Spring SpEL expressions. It is suitable for scenarios requiring data association and
 * transformation in memory, which can significantly improve data processing efficiency.
 * <p>
 * For usage instructions, please refer to the {@code readme.md} documentation.
 *
 * @version 1.0.0
 * @developers <a href="mailto:max_verstrappon@outlook.com">Hu Peng</a>
 * @date 2026/1/6
 * @see JoinInMemoryBasedJoinFieldExecutorFactory
 * @see <a href="https://docs.spring.io/spring-framework/docs/3.2.x/spring-framework-reference/html/expressions.html">Spring SpEL</a>
 */
@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface JoinInMemory {

    /**
     * Filter before join Reduce certain numbers of data to be processed.
     * <p>
     * A boolean value is excepted.
     * <p>
     * Exceptions will be thrown if the return value of the Expression is not a boolean.
     * <p>
     * This attribute is used in the grouping process if the {@link JoinInMemoryConfig#fieldProcessPolicy()} is set to
     * GROUP.
     */
    @Language("SpEL")
    String sourceDataFilter() default "";

    /**
     * Using SpEL to extract the field value from the given source.
     * <p>
     * Usage Example: {@code user.id}
     *
     * @return SpEL expression of the key extracted from the given source
     */
    @Language("SpEL")
    String keyFromSourceData() default "";

    /**
     * Using SpEL to extract field value from the datasource. Define using which field to construct a mapping relation.
     * <p>
     * This attribute is used in the grouping process if the {@link JoinInMemoryConfig#fieldProcessPolicy()} is set to
     * GROUP.
     *
     * @return SpEL expression of the field value retrieved from the datasource
     */
    @Language("SpEL")
    String keyFromJoinData() default "";

    /**
     * Define a method which takes one parameter contains all the values composed of keyFromSourceData() and returns a
     * list of values from the datasource. Eventually, the list will be converted into a joinDataKeyConverter keyed
     * map.({@code Map<KEY,List<CONVERTED_OBJECT>>})
     * <p>
     * Usage example: {@code userRepository.findAllById(#root)}
     * <p>
     * This attribute is used in the grouping process if the {@link JoinInMemoryConfig#fieldProcessPolicy()} is set to
     * GROUP.
     *
     */
    @Language("SpEL")
    String loader() default "";

    /**
     * Filter after join Reduce certain numbers of data to be processed.
     * <p>
     * A boolean value is excepted.
     * <p>
     * Exceptions will be thrown if the return value of the Expression is not a boolean.
     * <p>
     * This attribute is used in the grouping process if the {@link JoinInMemoryConfig#fieldProcessPolicy()} is set to
     * GROUP.
     */
    @Language("SpEL")
    String joinDataFilter() default "";

    /**
     * Define a method that takes one parameter,which is the object from the data source, and returns the converted
     * result which is the type of the field annotated with the join annotation.
     * <p>
     * In the end, if data source values can be found through the mapping process, the annotated source field will be
     * set in two ways. If the annotated source field is a List type, it will be set accordingly. Or, if the field is
     * the List's parameter type, it only takes one object as its value. So the field will be set only if the converted
     * list has only one element. Otherwise, the list is discarded.
     * <p>
     * Usage example:
     * <p>
     * {@code #this.name}
     *
     * @return SpEL expression of how to converter values from datasource if match the key value from the
     * keyFromSourceData()
     */
    @Language("SpEL")
    String joinDataConverter() default "";

    /**
     * Runlevel defines the task executing orders in both parallel and serial executors.
     * <p>
     * This attribute is used in the grouping process if the {@link JoinInMemoryConfig#fieldProcessPolicy()} is set to
     * GROUP.
     *
     * @return The default is fifth, which is in the middle.
     */
    ExecuteLevel runLevel() default ExecuteLevel.FIFTH;
}
