package com.luban.codegen.processor.command.update;

import com.google.auto.service.AutoService;
import com.google.common.base.Preconditions;
import com.luban.codegen.processor.AbstractCodeGenProcessor;
import com.luban.codegen.processor.Ignore;
import com.luban.codegen.spi.CodeGenProcessor;
import com.luban.common.base.command.CommandForUpdateById;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Objects;

/**
 * @author hp
 */
@AutoService(value = CodeGenProcessor.class)
public class GenUpdateCommandProcessor extends AbstractCodeGenProcessor {

    public static final String PREFIX = "Update";
    public static final String SUFFIX = "Command";

    public static String filename(TypeElement typeElement) {
        Preconditions.checkArgument(Objects.nonNull(typeElement));
        return PREFIX + typeElement.getSimpleName() + SUFFIX;
    }

    @Override
    protected void generateClass(TypeElement typeElement, RoundEnvironment roundEnvironment) {
        final List<VariableElement> fields = findFields(
                typeElement,
                v -> Objects.isNull(v.getAnnotation(Ignore.class)) && Objects.isNull(v.getAnnotation(Deprecated.class))
        );
        final String sourceClassName = filename(typeElement);
        final String packageName = generatePackage(typeElement);

        final TypeSpec.Builder builder = TypeSpec.classBuilder(sourceClassName)
                .addSuperinterface(CommandForUpdateById.class)
                .addModifiers(Modifier.PUBLIC);

        builder.addField(FieldSpec.builder(Long.class, "id", Modifier.PRIVATE).build());
        generateGettersAndSettersWithLombok(builder, fields, null);
        generateJavaSourceFile(packageName, typeElement.getAnnotation(GenUpdateCommand.class).sourcePath(), builder);
    }

    @Override
    public Class<? extends Annotation> getAnnotation() {
        return GenUpdateCommand.class;
    }

    @Override
    public String generatePackage(TypeElement typeElement) {
        return typeElement.getAnnotation(GenUpdateCommand.class).pkgName();
    }

    @Override
    public String generatePath(TypeElement typeElement) {
        return typeElement.getAnnotation(GenUpdateCommand.class).sourcePath();
    }
}
