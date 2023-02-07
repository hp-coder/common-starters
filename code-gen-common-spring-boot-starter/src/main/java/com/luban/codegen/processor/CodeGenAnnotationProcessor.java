package com.hp.codegen.processor;

import com.google.auto.service.AutoService;
import com.hp.codegen.context.ProcessingEnvironmentContextHolder;
import com.hp.codegen.registry.CodeGenProcessorRegistry;
import com.hp.codegen.spi.CodeGenProcessor;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic;
import java.util.Set;

/**
 * @author HP
 * @date 2022/10/24
 */
@AutoService(Processor.class)
public class CodeGenAnnotationProcessor extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        annotations.forEach(_0 -> {
            final Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(_0);
            final Set<TypeElement> typeElements = ElementFilter.typesIn(elements);
            typeElements.forEach(_1 -> {
                try {
                    final CodeGenProcessor processor = CodeGenProcessorRegistry.find(_0.getQualifiedName().toString());
                    processor.generate(_1, roundEnv);
                } catch (Exception e) {
                    e.printStackTrace();
                    ProcessingEnvironmentContextHolder.getEnvironment().getMessager().printMessage(Diagnostic.Kind.ERROR,"代码生成异常: " + e.getLocalizedMessage());
                }
            });
        });
        return false;
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        ProcessingEnvironmentContextHolder.setEnvironment(processingEnv);
        CodeGenProcessorRegistry.initProcessors();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return CodeGenProcessorRegistry.getSupportedAnnotations();
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
