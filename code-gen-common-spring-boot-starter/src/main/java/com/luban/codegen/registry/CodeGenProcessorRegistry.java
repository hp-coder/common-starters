package com.hp.codegen.registry;

import com.google.common.collect.Maps;
import com.hp.codegen.spi.CodeGenProcessor;

import java.lang.annotation.Annotation;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;

/**
 * 处理器注册中心*
 * @author HP
 * @date 2022/10/24
 */
public final class CodeGenProcessorRegistry {

    private static Map<String, ? extends CodeGenProcessor> PROCESSORS;

    private CodeGenProcessorRegistry() {
        throw new UnsupportedOperationException();
    }

    public static Set<String> getSupportedAnnotations() {
        return PROCESSORS.keySet();
    }

    public static CodeGenProcessor find(String annotationClassName) {
        return PROCESSORS.get(annotationClassName);
    }

    public static void initProcessors() {
        final LinkedHashMap<String, CodeGenProcessor> map = Maps.newLinkedHashMap();
        final ServiceLoader<CodeGenProcessor> processors = ServiceLoader.load(CodeGenProcessor.class, CodeGenProcessor.class.getClassLoader());
        for (CodeGenProcessor processor : processors) {
            final Class<? extends Annotation> annotation = processor.getAnnotation();
            map.put(annotation.getName(), processor);
        }
        PROCESSORS = map;
    }
}
