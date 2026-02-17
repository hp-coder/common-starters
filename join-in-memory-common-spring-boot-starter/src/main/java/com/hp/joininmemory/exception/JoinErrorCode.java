package com.hp.joininmemory.exception;


import com.hp.common.base.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 关联查询错误码枚举
 *
 * @version 1.0.0
 * @developers <a href="mailto:max_verstrappon@outlook.com">Hu Peng</a>
 * @date 2026/1/6
 */
@Getter
@AllArgsConstructor
public enum JoinErrorCode implements BaseEnum<JoinErrorCode, Integer> {

    /**
     * 基础关联异常
     */
    JOIN_ERROR(700500, "关联查询异常"),

    /**
     * 后置处理异常
     */
    AFTER_JOIN_ERROR(700501, "后置处理异常"),

    /**
     * SpEL表达式解析异常
     */
    SPEL_PARSE_ERROR(700502, "SpEL表达式解析失败"),

    /**
     * 数据加载异常
     */
    DATA_LOADER_ERROR(700503, "数据加载失败"),

    /**
     * 数据转换异常
     */
    DATA_CONVERSION_ERROR(700504, "数据转换失败"),

    /**
     * 字段映射异常
     */
    FIELD_MAPPING_ERROR(700505, "字段映射失败"),

    /**
     * 线程执行异常
     */
    THREAD_EXECUTION_ERROR(700507, "线程执行异常"),
    ;

    private final Integer code;
    private final String name;

    @Override
    public String toString() {
        return String.format("JoinError [%s]:%s", getCode(), getName());
    }
}
