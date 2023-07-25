package com.luban.mybatisplus.convertor;

import org.apache.ibatis.type.TypeHandler;

/**
 * 为了方便代码生成器检测到需要转换的类型, 增加该接口来扩展范型
 * <p>
 * 目前是没有通过内部代码约束, 使用时需要注意
 *
 * @author hp
 */
public interface TypeHandlerCodeGenAdapter<SOURCE, TARGET> extends TypeHandler<SOURCE> {
}
