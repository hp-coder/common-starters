package com.luban.codegen.processor.context.create;

import com.google.auto.service.AutoService;
import com.google.common.base.Preconditions;
import com.luban.codegen.context.ProcessingEnvironmentContextHolder;
import com.luban.codegen.processor.AbstractCodeGenProcessor;
import com.luban.codegen.spi.CodeGenProcessor;
import com.luban.codegen.util.StringUtils;
import com.luban.common.base.context.AbstractContext;
import com.squareup.javapoet.*;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.lang.annotation.Annotation;
import java.util.Objects;

/**
 * @author hp
 */
@AutoService(value = CodeGenProcessor.class)
public class GenCreateContextProcessor extends AbstractCodeGenProcessor {

    public static final String PREFIX = "Create";
    public static final String SUFFIX = "Context";

    public static String filename(TypeElement typeElement) {
        Preconditions.checkArgument(Objects.nonNull(typeElement));
        return PREFIX + typeElement.getSimpleName() + SUFFIX;
    }

    @Override
    protected void generateClass(TypeElement typeElement, RoundEnvironment roundEnvironment) {
        if (StringUtils.containsNull(getNameContext().getCreateCommandPackageName(), getNameContext().getCreateCommandClassName())) {
            ProcessingEnvironmentContextHolder
                    .getMessager()
                    .printMessage(
                            Diagnostic.Kind.MANDATORY_WARNING,
                            String.format("To generate %s requires %s to be present.", filename(typeElement), "@GenCreateCommand")
                    );
            return;
        }
        final String sourceClassName = filename(typeElement);
        final String packageName = generatePackage(typeElement);

        final ClassName createCommandName = ClassName.get(getNameContext().getCreateCommandPackageName(), getNameContext().getCreateCommandClassName());
        final ClassName createContextName = ClassName.get(getNameContext().getCreateContextPackageName(), getNameContext().getCreateContextClassName());

        final TypeSpec.Builder builder = TypeSpec.classBuilder(sourceClassName)
                .addModifiers(Modifier.PUBLIC)
                .superclass(ParameterizedTypeName.get(ClassName.get(AbstractContext.class), ClassName.get(typeElement), createCommandName));

        builder.addMethod(MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ParameterSpec.builder(createCommandName, "command").build())
                .addCode(
                        CodeBlock.of("super(command);")
                )
                .build()
        );

        builder.addField(
                FieldSpec.builder(Long.class, "createdBy", Modifier.PRIVATE)
                        .addAnnotation(Getter.class)
                        .addAnnotation(Setter.class)
                        .build()
        );

        builder.addMethod(MethodSpec.methodBuilder("create")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(
                        ParameterSpec.builder(
                                createCommandName, "command").build()
                )
                .addCode(
                        CodeBlock.of(
                                "final $T context = new $T(command);\n" +
                                        "context.init();\n" +
                                        "return context;",
                                createContextName,
                                createContextName
                        )
                )
                .returns(createContextName)
                .build()
        );
        builder.addMethod(MethodSpec.methodBuilder("init").addModifiers(Modifier.PRIVATE).build());
        generateJavaSourceFile(packageName, typeElement.getAnnotation(GenCreateContext.class).sourcePath(), builder);
    }

    @Override
    public Class<? extends Annotation> getAnnotation() {
        return GenCreateContext.class;
    }

    @Override
    public String generatePackage(TypeElement typeElement) {
        return typeElement.getAnnotation(GenCreateContext.class).pkgName();
    }

    @Override
    public String generatePath(TypeElement typeElement) {
        return typeElement.getAnnotation(GenCreateContext.class).sourcePath();
    }
}
