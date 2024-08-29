package com.hp.codegen.registry;

import cn.hutool.core.map.WeakConcurrentMap;
import com.hp.codegen.context.CodeGenContextHolder;
import com.hp.codegen.spi.CodeGenProcessor;

import javax.tools.Diagnostic;
import java.lang.annotation.Annotation;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author hp
 */
public final class CodeGenProcessorRegistry {
    private static Map<String, Set<CodeGenProcessor>> PROCESSORS;

    private CodeGenProcessorRegistry() {
        throw new UnsupportedOperationException();
    }

    public static Set<String> getSupportedAnnotationTypes() {
        return PROCESSORS.keySet();
    }

    public static Set<CodeGenProcessor> getSupportedProcessors() {
        return PROCESSORS.values().stream().flatMap(Collection::stream).collect(Collectors.toSet());
    }

    public static CodeGenProcessor find(String annotationClassName) {
        final Set<? extends CodeGenProcessor> codeGenProcessors = PROCESSORS.get(annotationClassName);
        if (codeGenProcessors == null || codeGenProcessors.isEmpty()) {
            CodeGenContextHolder.getMessager().printMessage(Diagnostic.Kind.ERROR, "未找到对应注解的处理器, 请检查代码生成器");
        }
        assert codeGenProcessors != null;
        return codeGenProcessors.stream().filter(i -> i.supportedOrm(CodeGenContextHolder.getOrm())).findFirst().orElseThrow();
    }

    public static Set<CodeGenProcessor> findAll(String annotationClassName) {
        final Set<? extends CodeGenProcessor> codeGenProcessors = PROCESSORS.get(annotationClassName);
        if (codeGenProcessors == null || codeGenProcessors.isEmpty()) {
            CodeGenContextHolder.getMessager().printMessage(Diagnostic.Kind.ERROR, "未找到对应注解的处理器, 请检查代码生成器");
        }
        assert codeGenProcessors != null;
        return codeGenProcessors.stream().filter(i -> i.supportedOrm(CodeGenContextHolder.getOrm())).collect(Collectors.toSet());
    }

    public static void initProcessors() {
        final Map<String, Set<CodeGenProcessor>> map = new WeakConcurrentMap<>();
        final ServiceLoader<CodeGenProcessor> processors = ServiceLoader.load(CodeGenProcessor.class, CodeGenProcessor.class.getClassLoader());
        for (CodeGenProcessor processor : processors) {
            final Class<? extends Annotation> annotation = processor.getAnnotation();
            map.compute(annotation.getName(), (key, set) -> {
                if (Objects.isNull(set)) {
                    final Set<CodeGenProcessor> processorSet = new HashSet<>();
                    processorSet.add(processor);
                    return processorSet;
                } else {
                    set.add(processor);
                    return set;
                }
            });
        }
        initProcessorGroups(map);
        PROCESSORS = map;
    }

    private static void initProcessorGroups(Map<String, Set<CodeGenProcessor>> map) {
        if (Objects.isNull(map)) {
            return;
        }
        final Map<String, Set<CodeGenProcessor>> groupedProcessors = map.values()
                .stream()
                .flatMap(Collection::stream)
                .collect(
                        Collectors.groupingBy(
                                pro -> pro.getAnnotationGroup().getName(),
                                Collectors.toSet()
                        )
                );
        map.putAll(groupedProcessors);
    }
}
