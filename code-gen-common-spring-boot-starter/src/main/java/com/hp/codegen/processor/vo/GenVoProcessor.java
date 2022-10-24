package com.hp.codegen.processor.vo;

import com.google.auto.service.AutoService;
import com.hp.codegen.context.ProcessingEnvironmentContextHolder;
import com.hp.codegen.processor.AbstractCodeGenProcessor;
import com.hp.codegen.spi.CodeGenProcessor;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.Objects;
import java.util.Set;

/**
 * @author HP
 * @date 2022/10/24
 */
@AutoService(CodeGenProcessor.class)
public class GenVoProcessor extends AbstractCodeGenProcessor {

    private static final String SUFFIX = "VO";

    @Override
    protected void generateClass(TypeElement typeElement, RoundEnvironment roundEnvironment) {
        ProcessingEnvironmentContextHolder.getMessager().printMessage(Diagnostic.Kind.MANDATORY_WARNING, "Starting To Create VO Class For " + typeElement.getSimpleName().toString());
        final Set<VariableElement> variableElements = findFields(typeElement, _0 -> !Objects.nonNull(_0.getAnnotation(Ignore.class)));
        final String className = typeElement.getSimpleName() + SUFFIX;
        final TypeSpec.Builder builder = TypeSpec.classBuilder(className)
                .addSuperinterface(Serializable.class)
                .addModifiers(Modifier.PUBLIC);
        generateGettersAndSettersWithLombok(builder, variableElements);
        generateJavaSourceFile(generatePackage(typeElement),
                typeElement.getAnnotation(GenVo.class).sourcePath(),
                builder);
    }

    @Override
    public Class<? extends Annotation> getAnnotation() {
        return GenVo.class;
    }

    @Override
    public String generatePackage(TypeElement typeElement) {
        return typeElement.getAnnotation(GenVo.class).pkgName();
    }
}
