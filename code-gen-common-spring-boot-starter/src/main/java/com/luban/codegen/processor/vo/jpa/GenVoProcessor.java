package com.luban.codegen.processor.vo.jpa;

import com.google.auto.service.AutoService;
import com.luban.codegen.constant.Orm;
import com.luban.codegen.processor.AbstractCodeGenProcessor;
import com.luban.codegen.processor.Ignore;
import com.luban.codegen.processor.vo.GenVo;
import com.luban.codegen.spi.CodeGenProcessor;
import com.luban.jpa.BaseJpaAggregate;
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
public class GenVoProcessor extends AbstractCodeGenProcessor {

    public static final String SUFFIX = "VO";

    @Override
    public boolean supportedOrm(Orm orm) {
        return Orm.SPRING_DATA_JPA.equals(orm);
    }

    @Override
    protected void generateClass(TypeElement typeElement, RoundEnvironment roundEnvironment) {
        final List<VariableElement> fields = findFields(typeElement, v ->
                Objects.isNull(v.getAnnotation(Ignore.class)) &&
                        Objects.isNull(v.getAnnotation(Deprecated.class))
        );
        final String sourceClassName = typeElement.getSimpleName() + SUFFIX;
        final TypeSpec.Builder builder = TypeSpec.classBuilder(sourceClassName)
                .addModifiers(Modifier.PUBLIC);
        final MethodSpec.Builder constructorSpecBuilder = MethodSpec.constructorBuilder()
                .addParameter(TypeName.get(typeElement.asType()), "source")
                .addModifiers(Modifier.PUBLIC);

        getSuperClass(typeElement).ifPresent(superclass -> {
            if (superclass.getQualifiedName().contentEquals(BaseJpaAggregate.class.getCanonicalName())) {
                builder.addField(FieldSpec.builder(Long.class, "id", Modifier.PRIVATE).build());
                builder.addField(FieldSpec.builder(Instant.class, "createdAt", Modifier.PRIVATE).build());
                builder.addField(FieldSpec.builder(Instant.class, "updatedAt", Modifier.PRIVATE).build());
                builder.addField(FieldSpec.builder(Integer.class, "version", Modifier.PRIVATE).build());
            }
        });

        generateGettersAndSetters(builder, fields, null);
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
