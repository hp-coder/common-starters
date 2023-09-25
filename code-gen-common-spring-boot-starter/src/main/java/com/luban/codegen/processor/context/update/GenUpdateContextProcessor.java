package com.luban.codegen.processor.context.update;

import com.google.auto.service.AutoService;
import com.google.common.base.Preconditions;
import com.luban.codegen.constant.Orm;
import com.luban.codegen.context.ProcessingEnvironmentContextHolder;
import com.luban.codegen.processor.AbstractCodeGenProcessor;
import com.luban.codegen.spi.CodeGenProcessor;
import com.luban.codegen.util.StringUtils;
import com.luban.common.base.context.AbstractContext;
import com.squareup.javapoet.*;
import lombok.Data;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Objects;

/**
 * @author hp
 */
@AutoService(value = CodeGenProcessor.class)
public class GenUpdateContextProcessor extends AbstractCodeGenProcessor {

    public static final String PREFIX = "Update";
    public static final String SUFFIX = "Context";

    public static String filename(TypeElement typeElement) {
        Preconditions.checkArgument(Objects.nonNull(typeElement));
        return PREFIX + typeElement.getSimpleName() + SUFFIX;
    }

    @Override
    protected void generateClass(TypeElement typeElement, RoundEnvironment roundEnvironment) {
        if (StringUtils.containsNull(getNameContext().getUpdateCommandPackageName(), getNameContext().getUpdateCommandClassName())) {
            ProcessingEnvironmentContextHolder
                    .getMessager()
                    .printMessage(
                            Diagnostic.Kind.MANDATORY_WARNING,
                            String.format("To generate %s requires %s to be present.", filename(typeElement), "@GenUpdateCommand")
                    );
            return;
        }
        final String sourceClassName = filename(typeElement);
        final String packageName = generatePackage(typeElement);

        final ClassName updateContextName = ClassName.get(getNameContext().getUpdateContextPackageName(), getNameContext().getUpdateContextClassName());
        final ClassName updateCommandName = ClassName.get(getNameContext().getUpdateCommandPackageName(), getNameContext().getUpdateCommandClassName());

        final TypeSpec.Builder builder = TypeSpec.classBuilder(sourceClassName)
                .addAnnotation(Data.class)
                .addModifiers(Modifier.PUBLIC)
                .superclass(ParameterizedTypeName.get(ClassName.get(AbstractContext.class), ClassName.get(typeElement), updateCommandName));

        builder.addField(FieldSpec.builder(Long.class, "updatedBy", Modifier.PRIVATE).build());
        builder.addMethod(MethodSpec.methodBuilder("create")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(
                        ParameterSpec.builder(updateCommandName, "command").build()
                )
                .addCode(
                        CodeBlock.of(
                                "final $T context = new $T();\n" +
                                        "context.setCommand(command);\n" +
                                        "context.init();\n" +
                                        "return context;",
                                updateContextName,
                                updateContextName
                        )
                )
                .returns(updateContextName)
                .build()
        );
        builder.addMethod(MethodSpec.methodBuilder("init").addModifiers(Modifier.PRIVATE).build());
        generateJavaSourceFile(packageName, typeElement.getAnnotation(GenUpdateContext.class).sourcePath(), builder);
    }

    @Override
    public Class<? extends Annotation> getAnnotation() {
        return GenUpdateContext.class;
    }

    @Override
    public String generatePackage(TypeElement typeElement) {
        return typeElement.getAnnotation(GenUpdateContext.class).pkgName();
    }

    @Override
    public String generatePath(TypeElement typeElement) {
        return typeElement.getAnnotation(GenUpdateContext.class).sourcePath();
    }
}
