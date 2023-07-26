package com.luban.codegen.processor.response;

import com.google.auto.service.AutoService;
import com.google.common.collect.Lists;
import com.luban.codegen.processor.AbstractCodeGenProcessor;
import com.luban.codegen.processor.Ignore;
import com.luban.codegen.processor.modifier.*;
import com.luban.codegen.spi.CodeGenProcessor;
import com.luban.common.base.model.Response;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author HP
 * @date 2022/10/24
 */
@AutoService(CodeGenProcessor.class)
public class GenResponseProcessor extends AbstractCodeGenProcessor {

    public static final String RESPONSE_SUFFIX = "Response";

    @Override
    protected void generateClass(TypeElement typeElement, RoundEnvironment roundEnvironment) {
        List<VariableElement> fields = findFields(typeElement, v ->
                Objects.isNull(v.getAnnotation(Ignore.class)) &&
                        Objects.isNull(v.getAnnotation(Deprecated.class))
        );
        String sourceClassName = typeElement.getSimpleName() + RESPONSE_SUFFIX;
        TypeSpec.Builder builder = TypeSpec.classBuilder(sourceClassName)
                .superclass(AbstractBaseResponse.class)
                .addSuperinterface(Response.class)
                .addModifiers(Modifier.PUBLIC);

        final ArrayList<FieldSpecModifier> fieldSpecModifiers = Lists.newArrayList(
                new LongToStringFieldSpecModifier(),
                new JpaConverterFieldSpecModifier(),
                new BaseEnumFieldSpecModifier()
        );
        generateGettersAndSettersWithLombok(builder, fields, fieldSpecModifiers);
        String packageName = generatePackage(typeElement);
        generateJavaSourceFile(packageName, typeElement.getAnnotation(GenResponse.class).sourcePath(), builder);
    }

    @Override
    public Class<? extends Annotation> getAnnotation() {
        return GenResponse.class;
    }

    @Override
    public String generatePackage(TypeElement typeElement) {
        return typeElement.getAnnotation(GenResponse.class).pkgName();
    }
}
