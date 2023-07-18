package com.hp.codegen.processor.vo;

import com.google.auto.service.AutoService;
import com.hp.codegen.processor.AbstractCodeGenProcessor;
import com.hp.codegen.processor.Ignore;
import com.hp.codegen.spi.CodeGenProcessor;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author HP
 * @date 2022/10/24
 */
@AutoService(CodeGenProcessor.class)
public class GenVoProcessor extends AbstractCodeGenProcessor {

    public static final String SUFFIX = "VO";

    @Override
    protected void generateClass(TypeElement typeElement, RoundEnvironment roundEnvironment) {
        List<VariableElement> fields = findFields(typeElement, v ->
                Objects.isNull(v.getAnnotation(Ignore.class)) &&
                        Objects.isNull(v.getAnnotation(Deprecated.class))
        );
        String sourceClassName = typeElement.getSimpleName() + SUFFIX;
        TypeSpec.Builder builder = TypeSpec.classBuilder(sourceClassName)
                .superclass(AbstractBaseVO.class)
                .addModifiers(Modifier.PUBLIC);
        generateGettersAndSetters(builder, fields, null);
        MethodSpec.Builder constructorSpecBuilder = MethodSpec.constructorBuilder().addParameter(TypeName.get(typeElement.asType()), "source").addModifiers(Modifier.PUBLIC);
        constructorSpecBuilder.addStatement("super(source)");
        fields.forEach(f -> constructorSpecBuilder.addStatement("$T.ofNullable(source.get$L()).ifPresent(this::set$L)", Optional.class, getFieldMethodName(f), getFieldMethodName(f)));
        builder.addMethod(constructorSpecBuilder.build());
        // no args constructor
        builder.addMethod(MethodSpec.constructorBuilder().addModifiers(Modifier.PROTECTED).build());
        generateJavaFile(generatePackage(typeElement), builder);
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
