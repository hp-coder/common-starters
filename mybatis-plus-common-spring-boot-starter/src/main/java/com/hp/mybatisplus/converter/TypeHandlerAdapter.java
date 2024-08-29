package com.hp.mybatisplus.converter;

import org.apache.ibatis.type.TypeHandler;

/**
 * 为了方便代码生成器检测到需要转换的类型, 增加该接口来扩展范型
 *
 * @author hp
 */
public interface TypeHandlerAdapter<FIELD, COLUMN> extends TypeHandler<FIELD> {

    COLUMN fieldToColumn(FIELD field);

    FIELD columnToField(COLUMN column);

}
