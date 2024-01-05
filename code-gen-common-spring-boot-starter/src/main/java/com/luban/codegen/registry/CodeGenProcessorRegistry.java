package com.luban.codegen.registry;

import com.luban.codegen.constant.Orm;
import com.luban.codegen.context.ProcessingEnvironmentContextHolder;
import com.luban.codegen.spi.CodeGenProcessor;

import javax.tools.Diagnostic;
import java.lang.annotation.Annotation;
import java.util.*;

/**
 * 处理器注册中心*
 *
 * @author hp
 * @date 2022/10/24
 */
public final class CodeGenProcessorRegistry {

    private static Map<String, Set<CodeGenProcessor>> PROCESSORS;

    private CodeGenProcessorRegistry() {
        throw new UnsupportedOperationException();
    }

    public static Set<String> getSupportedAnnotations() {
        return PROCESSORS.keySet();
    }

    public static CodeGenProcessor find(String annotationClassName, Orm supportedOrm) {
        final Set<? extends CodeGenProcessor> codeGenProcessors = PROCESSORS.get(annotationClassName);
        if (codeGenProcessors == null || codeGenProcessors.isEmpty()) {
            // By printing out error messages, the compilation process will be interrupted.
            ProcessingEnvironmentContextHolder.getMessager().printMessage(Diagnostic.Kind.ERROR, "未找到对应注解的处理器, 请检查代码生成器");
        }
        assert codeGenProcessors != null;
        return codeGenProcessors.stream().filter(i -> i.supportedOrm(supportedOrm)).findFirst().orElseThrow();
    }

    public static void initProcessors() {
        final Map<String, Set<CodeGenProcessor>> map = new HashMap<>();
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
        PROCESSORS = map;
    }
}
