package com.hp.codegen.processor.dto.jpa;

import com.google.auto.service.AutoService;
import com.hp.codegen.constant.Orm;
import com.hp.codegen.processor.AbstractCodeGenProcessor;
import com.hp.codegen.processor.Ignore;
import com.hp.codegen.processor.dto.GenDto;
import com.hp.codegen.spi.CodeGenProcessor;
import com.hp.jpa.BaseJpaAggregate;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import java.lang.annotation.Annotation;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


/**
 * @author hp
 * @date 2022/10/24
 */
@AutoService(CodeGenProcessor.class)
public class GenDtoProcessor extends AbstractCodeGenProcessor {

    public static final String SUFFIX = "DTO";

    @Override
    public boolean supportedOrm(Orm orm) {
        return Orm.SPRING_DATA_JPA.equals(orm);
    }

    @Override
    protected void generateClass(TypeElement typeElement, RoundEnvironment roundEnvironment) {
        List<VariableElement> fields = findFields(typeElement, v ->
                Objects.isNull(v.getAnnotation(Ignore.class)) &&
                        Objects.isNull(v.getAnnotation(Deprecated.class))
        );
        String sourceClassName = typeElement.getSimpleName() + SUFFIX;
        TypeSpec.Builder builder = TypeSpec.classBuilder(sourceClassName)
                .addModifiers(Modifier.PUBLIC);

        getSuperClass(typeElement).ifPresent(superclass -> {
            if (superclass.getQualifiedName().contentEquals(BaseJpaAggregate.class.getCanonicalName())) {
                builder.addField(FieldSpec.builder(Long.class, "id", Modifier.PRIVATE).build());
                builder.addField(FieldSpec.builder(Instant.class, "updatedAt", Modifier.PRIVATE).build());
            }
        });

        generateGettersAndSetters(builder, fields, null);
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("update" + typeElement.getSimpleName())
                .addParameter(TypeName.get(typeElement.asType()), "source")
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class);
        fields.forEach(f -> methodBuilder.addStatement("$T.ofNullable(this.get$L()).ifPresent(source::set$L)", Optional.class, getFieldMethodName(f), getFieldMethodName(f)));
        builder.addMethod(methodBuilder.build());
        generateJavaFile(generatePackage(typeElement), builder);
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
