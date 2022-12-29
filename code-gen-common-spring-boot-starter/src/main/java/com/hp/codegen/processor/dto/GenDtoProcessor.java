package com.hp.codegen.processor.dto;

import com.google.auto.service.AutoService;
import com.hp.codegen.processor.AbstractCodeGenProcessor;
import com.hp.codegen.processor.response.GenResponse;
import com.hp.codegen.processor.vo.Ignore;
import com.hp.codegen.spi.CodeGenProcessor;
import com.luban.common.jpa.dto.AbstractBaseJpaDTO;
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
import java.util.Optional;
import java.util.Set;



/**
 * @author HP
 * @date 2022/10/24
 */
@AutoService(CodeGenProcessor.class)
public class GenDtoProcessor extends AbstractCodeGenProcessor {

    public static final String SUFFIX = "DTO";

    @Override
    protected void generateClass(TypeElement typeElement, RoundEnvironment roundEnvironment) {
        Set<VariableElement> fields = findFields(typeElement, v -> Objects.isNull(v.getAnnotation(Ignore.class)));
        String sourceClassName = typeElement.getSimpleName() + SUFFIX;
        TypeSpec.Builder builder = TypeSpec.classBuilder(sourceClassName)
                .superclass(AbstractBaseJpaDTO.class)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Data.class);
        generateGettersAndSettersWithLombok(builder, fields);
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("update" + typeElement.getSimpleName())
                .addParameter(TypeName.get(typeElement.asType()), "source")
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class);
        fields.forEach(f -> methodBuilder.addStatement("$T.ofNullable(this.get$L()).ifPresent(source::set$L)", Optional.class, getFieldMethodName(f), getFieldMethodName(f)));
        builder.addMethod(methodBuilder.build());
        generateJavaSourceFile(generatePackage(typeElement),typeElement.getAnnotation(GenResponse.class).sourcePath(), builder);
    }

    @Override
    public Class<? extends Annotation> getAnnotation() {
        return GenDto.class;
    }

    @Override
    public String generatePackage(TypeElement typeElement) {
        return typeElement.getAnnotation(GenDto.class).pkgName();
    }
}
