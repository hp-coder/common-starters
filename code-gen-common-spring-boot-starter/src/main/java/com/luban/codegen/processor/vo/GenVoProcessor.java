package com.luban.codegen.processor.vo;

import com.google.auto.service.AutoService;
import com.luban.codegen.processor.AbstractCodeGenProcessor;
import com.luban.codegen.spi.CodeGenProcessor;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import java.lang.annotation.Annotation;
import java.util.Objects;
import java.util.Set;

/**
 * @author HP
 * @date 2022/10/24
 */
@AutoService(CodeGenProcessor.class)
public class GenVoProcessor extends AbstractCodeGenProcessor {

    public static final String SUFFIX = "VO";

    @Override
    protected void generateClass(TypeElement typeElement, RoundEnvironment roundEnvironment) {
        Set<VariableElement> fields = findFields(typeElement, v -> Objects.isNull(v.getAnnotation(Ignore.class)));
        String sourceClassName = typeElement.getSimpleName() + SUFFIX;
        TypeSpec.Builder builder = TypeSpec.classBuilder(sourceClassName)
                .superclass(AbstractBaseVO.class)
                .addModifiers(Modifier.PUBLIC);
        generateGettersAndSetters(builder, fields);
        MethodSpec.Builder constructorSpecBuilder = MethodSpec.constructorBuilder().addParameter(TypeName.get(typeElement.asType()), "source").addModifiers(Modifier.PUBLIC);
        constructorSpecBuilder.addStatement("super(source)");
        fields.forEach(f -> constructorSpecBuilder.addStatement("this.set$L(source.get$L())", getFieldMethodName(f), getFieldMethodName(f)));
        builder.addMethod(MethodSpec.constructorBuilder().addModifiers(Modifier.PROTECTED).build());
        builder.addMethod(constructorSpecBuilder.build());
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
