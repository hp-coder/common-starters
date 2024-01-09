package com.luban.joininmemory.context;

import com.luban.joininmemory.JoinFieldExecutor;
import lombok.Getter;
import lombok.Setter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * @author hp
 */
@Getter
@Setter
public class JoinFieldExecutorContext<DATA, A extends Annotation> {

    JoinFieldExecutor<DATA> executor;

    Field field;

    A annotation;
}
