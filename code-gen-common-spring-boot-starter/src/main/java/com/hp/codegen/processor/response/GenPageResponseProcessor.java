package com.hp.codegen.processor.response;

import com.google.auto.service.AutoService;
import com.hp.codegen.constant.Orm;
import com.hp.codegen.context.ProcessingEnvironmentContextHolder;
import com.hp.codegen.processor.AbstractCodeGenProcessor;
import com.hp.codegen.processor.Ignore;
import com.hp.codegen.spi.CodeGenProcessor;
import com.hp.common.base.model.Response;
import com.hp.jpa.BaseJpaAggregate;
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
 * @date 2022/10/24
 */
@AutoService(CodeGenProcessor.class)
public class GenPageResponseProcessor extends AbstractCodeGenProcessor {

    public static final String RESPONSE_SUFFIX = "PageResponse";

    @Override
    public boolean supportedOrm(Orm orm) {
        return super.supportedOrm(orm);
    }


    @Override
    protected void generateClass(TypeElement typeElement, RoundEnvironment roundEnvironment) {
        final List<VariableElement> fields = findFields(typeElement, v ->
                Objects.isNull(v.getAnnotation(Ignore.class)) && Objects.isNull(v.getAnnotation(Deprecated.class))
        );
        final TypeSpec.Builder builder = TypeSpec.classBuilder(nameContext.getPageResponseClassName())
                .addSuperinterface(Response.class)
                .addModifiers(Modifier.PUBLIC);

        getSuperClass(typeElement).ifPresent(
                superclass -> {
                    if (superclass.getQualifiedName().contentEquals(BaseJpaAggregate.class.getCanonicalName())) {
                        builder.addField(FieldSpec.builder(String.class,"id", Modifier.PRIVATE).build());
                        builder.addField(FieldSpec.builder(String.class,"createdAt", Modifier.PRIVATE).build());
                        builder.addField(FieldSpec.builder(String.class,"updatedAt", Modifier.PRIVATE).build());
                    }
                }
        );
        generateGettersAndSettersWithLombok(builder, fields, ProcessingEnvironmentContextHolder.getFieldSpecModifiers());
        generateJavaSourceFile(generatePackage(typeElement), generatePath(typeElement), builder);
    }

    @Override
    public Class<? extends Annotation> getAnnotation() {
        return GenPageResponse.class;
    }

    @Override
    public String generatePackage(TypeElement typeElement) {
        return typeElement.getAnnotation(GenPageResponse.class).pkgName();
    }

    @Override
    public String generatePath(TypeElement typeElement) {
        return typeElement.getAnnotation(GenPageResponse.class).sourcePath();
    }

}
