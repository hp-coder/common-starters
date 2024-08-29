package com.hp.codegen.processor;

import cn.hutool.core.collection.CollUtil;
import com.google.auto.service.AutoService;
import com.hp.codegen.constant.MappingMode;
import com.hp.codegen.constant.Orm;
import com.hp.codegen.context.CodeGenContextHolder;
import com.hp.codegen.registry.CodeGenProcessorRegistry;
import com.hp.codegen.spi.CodeGenProcessor;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic;
import java.util.Map;
import java.util.Set;

/**
 * @author hp
 * @date 2022/10/24
 */
@SupportedAnnotationTypes("com.hp.codegen.annotation.*")
@AutoService(Processor.class)
public class CodeGenAnnotationProcessor extends AbstractProcessor {

    protected final String orm = "orm";
    protected final String mappingMode = "mapping.mode";

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        final Map<String, String> compileArgs = CodeGenContextHolder.getCompileArgs();
        if (compileArgs != null && !compileArgs.isEmpty()) {
            compileArgs.forEach((k, v) -> CodeGenContextHolder.getMessager().printMessage(Diagnostic.Kind.NOTE, String.format("code-gen getting a compile arg: %s=%s", k, v)));
            CodeGenContextHolder.setOrm(Orm.of(compileArgs.get(orm)).orElse(Orm.SPRING_DATA_JPA));
            CodeGenContextHolder.setMappingMode(MappingMode.of(compileArgs.get(mappingMode)).orElse(MappingMode.MapStruct));
        }
        annotations.forEach(annotation -> {
            final Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(annotation);
            final Set<TypeElement> typeElements = ElementFilter.typesIn(elements);
            typeElements.forEach(typeElement -> {
                try {
                    CodeGenContextHolder.createTypeElementContext(typeElement);
                    final Set<CodeGenProcessor> processors = CodeGenProcessorRegistry.findAll(annotation.getQualifiedName().toString());
                    if (CollUtil.isNotEmpty(processors)) {
                        processors.forEach(processor -> {
                            processor.init(typeElement, roundEnv);
                            processor.generate(typeElement, roundEnv);
                        });
                    }
                } catch (Exception e) {
                    CodeGenContextHolder.getMessager().printMessage(Diagnostic.Kind.ERROR, String.format("生成%s代码异常: %s", typeElement, e), typeElement);
                    e.printStackTrace();
                }
            });
        });
        return false;
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        CodeGenContextHolder.setEnvironment(processingEnv);
        CodeGenProcessorRegistry.initProcessors();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return CodeGenProcessorRegistry.getSupportedAnnotationTypes();
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
