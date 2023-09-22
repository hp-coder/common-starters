package com.luban.codegen.processor.command.create;

import com.google.auto.service.AutoService;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.luban.codegen.constant.Orm;
import com.luban.codegen.processor.AbstractCodeGenProcessor;
import com.luban.codegen.processor.Ignore;
import com.luban.codegen.processor.modifier.BaseEnumFieldSpecModifier;
import com.luban.codegen.processor.modifier.DefaultToStringFieldSpecModifier;
import com.luban.codegen.processor.modifier.FieldSpecModifier;
import com.luban.codegen.processor.modifier.mybatisplus.MybatisplusTypeHandlerFieldSpecModifier;
import com.luban.codegen.spi.CodeGenProcessor;
import com.luban.common.base.command.CommandForCreate;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author hp
 */
@AutoService(value = CodeGenProcessor.class)
public class GenCreateCommandProcessor extends AbstractCodeGenProcessor {

    public static final String PREFIX = "Create";
    public static final String SUFFIX = "Command";

    public static String filename(TypeElement typeElement) {
        Preconditions.checkArgument(Objects.nonNull(typeElement));
        return PREFIX + typeElement.getSimpleName() + SUFFIX;
    }

    @Override
    public boolean supportedOrm(Orm orm) {
        return Arrays.asList(Orm.values()).contains(orm);
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
                .addSuperinterface(CommandForCreate.class)
                .addModifiers(Modifier.PUBLIC);

        final ArrayList<FieldSpecModifier> fieldSpecModifiers = Lists.newArrayList(
                new DefaultToStringFieldSpecModifier(),
                new MybatisplusTypeHandlerFieldSpecModifier(),
                new BaseEnumFieldSpecModifier()
        );
        generateGettersAndSettersWithLombok(builder, fields, fieldSpecModifiers);
        generateJavaSourceFile(packageName, typeElement.getAnnotation(GenCreateCommand.class).sourcePath(), builder);
    }

    @Override
    public Class<? extends Annotation> getAnnotation() {
        return GenCreateCommand.class;
    }

    @Override
    public String generatePackage(TypeElement typeElement) {
        return typeElement.getAnnotation(GenCreateCommand.class).pkgName();
    }

    @Override
    public String generatePath(TypeElement typeElement) {
        return typeElement.getAnnotation(GenCreateCommand.class).sourcePath();
    }
}
