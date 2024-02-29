package com.hp.codegen.processor.command.update;

import com.google.auto.service.AutoService;
import com.google.common.base.Preconditions;
import com.hp.codegen.processor.AbstractCodeGenProcessor;
import com.hp.codegen.processor.Ignore;
import com.hp.codegen.spi.CodeGenProcessor;
import com.hp.common.base.command.CommandForUpdateById;
import com.squareup.javapoet.*;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
                .addSuperinterface(ParameterizedTypeName.get(CommandForUpdateById.class, Long.class))
                .addModifiers(Modifier.PUBLIC);

        builder.addField(FieldSpec.builder(Long.class, "id", Modifier.PRIVATE).build());

        updaterMethod(typeElement, fields).ifPresent(builder::addMethod);

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


    private Optional<MethodSpec> updaterMethod(TypeElement typeElement, List<VariableElement> fields) {
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("update" + typeElement.getSimpleName())
                .addParameter(TypeName.get(typeElement.asType()), "source")
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class);
        fields.forEach(f -> methodBuilder.addStatement("$T.ofNullable(this.get$L()).ifPresent(source::set$L)", Optional.class, getFieldMethodName(f), getFieldMethodName(f)));
        return Optional.of(methodBuilder.build());
    }
}
