package com.luban.joininmemory.annotation;

import com.luban.joininmemory.constant.ExecuteLevel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * @author hp
 */
@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface JoinInMemory {

    /**
     * Using SpEL to extract the field value from the given source.
     * <p>
     * Usage Example:
     * <p>
     * "#{user.id}"
     *
     * @return SpEL expression of the key extracted from the given source
     */
    String keyFromSourceData();

    /**
     * Convert the source field value to a new value according
     * to the SpEL expression given.
     * Won't convert if it is empty.
     * <p>
     * In some cases, the type of the field in the entity
     * is different from the type of the field defined in
     * the source object.
     * <p>
     * Usage Example:
     * <p>
     * "#{ #root != null ? T(java.lang.Long).parseLong(#root) : null }"
     *
     * @return SpEL expression of the converted source field value. This value is used in
     * the mapping process.
     */
    String sourceDataKeyConverter() default "";

    /**
     * Using SpEL to extract field value from the datasource.
     * Define using which field to construct a mapping relation.
     * <p>
     * Usage Example:
     * <p>
     * "#{userId}"
     *
     * @return SpEL expression of the field value retrieved from the datasource
     */
    String keyFromJoinData();

    /**
     * Same as the sourceDataKeyConverter()
     * Won't convert if it is empty.
     *
     * @return SpEL expression of the converted field value extracted from the datasource
     */
    String joinDataKeyConverter() default "";

    /**
     * Define a method which takes one parameter contains all the values composed of keyFromSourceData()
     * and returns a list of values from the datasource.
     * Eventually, the list will be converted into a joinDataKeyConverter keyed map.(Map<KEY,List<CONVERTED_OBJECT>>)
     * <p>
     * Usage example:
     * <p>
     * "#{@userRepository.findAllById(#root)}"
     *
     * @return SpEL expression of the method that extracts the data from datasource
     */
    String loader();

    /**
     * Define a method that takes one parameter,which is the object from the data source,
     * and returns the converted result which is the type of the field annotated with the join annotation.
     * <p>
     * In the end, if data source values can be found through the mapping process,
     * the annotated source field will be set in two ways.
     * If the annotated source field is a List type, it will be set accordingly.
     * Or, if the field is the List's parameter type, it only takes one object as its value.
     * So the field will be set only if the converted list has only one element.
     * Otherwise, the list is discarded.
     * <p>
     * Usage example:
     * <p>
     * "#{#root.name}"
     *
     * @return SpEL expression of how to converter values from datasource if match the key value from the keyFromSourceData()
     */
    String joinDataConverter() default "";

    ExecuteLevel runLevel() default ExecuteLevel.FIFTH;
}
