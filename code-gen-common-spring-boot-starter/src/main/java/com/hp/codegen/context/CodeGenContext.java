package com.hp.codegen.context;


import com.hp.codegen.constant.MappingMode;
import com.hp.codegen.constant.Orm;
import com.hp.common.base.context.Context;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.annotation.processing.ProcessingEnvironment;

/**
 * @author hp
 * @date 2022/10/24
 */
@Data
@RequiredArgsConstructor
public class CodeGenContext implements Context {

    protected final ProcessingEnvironment processingEnvironment;

    protected Orm orm = Orm.SPRING_DATA_JPA;

    protected MappingMode mappingMode = MappingMode.MapStruct;

    protected TypeElementContext typeElementContext;
}
