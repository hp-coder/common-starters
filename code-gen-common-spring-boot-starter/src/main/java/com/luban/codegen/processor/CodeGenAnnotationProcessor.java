package com.luban.codegen.processor;

import com.google.auto.service.AutoService;
import com.luban.codegen.constant.Orm;
import com.luban.codegen.context.ProcessingEnvironmentContextHolder;
import com.luban.codegen.registry.CodeGenProcessorRegistry;
import com.luban.codegen.spi.CodeGenProcessor;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic;
import java.util.Map;
import java.util.Set;

/**
 * @author HP
 * @date 2022/10/24
 */
@AutoService(Processor.class)
public class CodeGenAnnotationProcessor extends AbstractProcessor {
    protected final String orm = "orm";
    protected final Orm defaultOrm = Orm.SPRING_DATA_JPA;

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Orm supportedOrm;
        final Map<String, String> options = ProcessingEnvironmentContextHolder.getEnvironment().getOptions();
        if (options != null && !options.isEmpty()) {
            final Messager messager = ProcessingEnvironmentContextHolder.getMessager();
            options.forEach((k, v) -> messager.printMessage(Diagnostic.Kind.MANDATORY_WARNING, String.format("code-gen获取到编译参数: %s=%s", k, v)));
            final String providedOrm = options.get(orm);
            if (providedOrm != null && !providedOrm.isEmpty()) {
                supportedOrm = Orm.of(providedOrm).orElse(defaultOrm);
            } else {
                supportedOrm = defaultOrm;
            }
        } else {
            supportedOrm = defaultOrm;
        }
        annotations.forEach(annotation -> {
            final Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(annotation);
            final Set<TypeElement> typeElements = ElementFilter.typesIn(elements);
            typeElements.forEach(typeElement -> {
                try {
                    final CodeGenProcessor processor = CodeGenProcessorRegistry.find(annotation.getQualifiedName().toString(), supportedOrm);
                    processor.generate(typeElement, roundEnv);
                } catch (Exception e) {
                    e.printStackTrace();
                    ProcessingEnvironmentContextHolder.getMessager().printMessage(Diagnostic.Kind.ERROR, "代码生成异常: " + e.getLocalizedMessage());
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
