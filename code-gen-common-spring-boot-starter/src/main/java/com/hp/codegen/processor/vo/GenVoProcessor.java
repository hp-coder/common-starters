package com.hp.codegen.processor.vo;

import com.google.auto.service.AutoService;
import com.hp.codegen.processor.AbstractCodeGenProcessor;
import com.hp.codegen.processor.response.GenResponse;
import com.hp.codegen.spi.CodeGenProcessor;
import com.luban.common.jpa.vo.AbstractBaseJpaVO;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import lombok.Data;

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
                .superclass(AbstractBaseJpaVO.class)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Data.class);
        generateGettersAndSettersWithLombok(builder, fields);
        MethodSpec.Builder constructorSpecBuilder = MethodSpec.constructorBuilder()
                .addParameter(TypeName.get(typeElement.asType()), "source")
                .addModifiers(Modifier.PUBLIC);
        constructorSpecBuilder.addStatement("super(source)");
        fields.forEach(f -> constructorSpecBuilder.addStatement("this.set$L(source.get$L())", getFieldMethodName(f), getFieldMethodName(f)));
        builder.addMethod(MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PROTECTED)
                .build());
        builder.addMethod(constructorSpecBuilder.build());
        generateJavaSourceFile(generatePackage(typeElement),typeElement.getAnnotation(GenResponse.class).sourcePath(), builder);
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
