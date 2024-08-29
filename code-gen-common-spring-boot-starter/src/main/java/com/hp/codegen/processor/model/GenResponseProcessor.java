package com.hp.codegen.processor.model;

import com.google.auto.service.AutoService;
import com.hp.codegen.annotation.model.GenResponse;
import com.hp.codegen.context.CodeGenContextHolder;
import com.hp.codegen.spi.CodeGenProcessor;
import com.hp.codegen.util.CodeGenHelper;
import com.hp.common.base.model.Response;
import com.hp.jpa.BaseJpaAggregate;
import com.hp.mybatisplus.BaseMbpAggregate;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.TypeSpec;
import lombok.Data;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import java.lang.annotation.Annotation;
import java.util.List;

/**
 * @author hp
 */
@AutoService(CodeGenProcessor.class)
public class GenResponseProcessor extends AbstractModelCodeGenProcessor {

    @Override
    public Class<? extends Annotation> getAnnotation() {
        return GenResponse.class;
    }

    @Override
    protected void customizeType(TypeElement typeElement, RoundEnvironment roundEnvironment, TypeSpec.Builder typeSpecBuilder) {
        final List<VariableElement> fields = CodeGenHelper.findNotIgnoreAndNotDeprecatedFields(typeElement);

        typeSpecBuilder
                .addSuperinterface(Response.class)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Data.class);

        if (CodeGenContextHolder.isJpaEnvironment()) {
            new JpaSuperClassCustomizer(typeElement, typeSpecBuilder).customize();
        }

        if (CodeGenContextHolder.isMyBatisEnvironment()) {
            new MybatisSuperClassCustomizer(typeElement, typeSpecBuilder).customize();
        }

        CodeGenHelper.createFields(typeSpecBuilder, fields, CodeGenContextHolder.getFieldCreator(this.currentGeneratingClassName, true));
    }

    private record JpaSuperClassCustomizer(TypeElement typeElement, TypeSpec.Builder typeSpecBuilder) {

        private void customize() {
            CodeGenHelper.getSuperClass(typeElement)
                    .ifPresent(superclass -> {
                        if (superclass.getQualifiedName().contentEquals(BaseJpaAggregate.class.getCanonicalName())) {
                            typeSpecBuilder.addField(FieldSpec.builder(String.class, "id", Modifier.PRIVATE).build());
                            typeSpecBuilder.addField(FieldSpec.builder(String.class, "createdAt", Modifier.PRIVATE).build());
                            typeSpecBuilder.addField(FieldSpec.builder(String.class, "updatedAt", Modifier.PRIVATE).build());
                        }
                    });
        }
    }

    private record MybatisSuperClassCustomizer(TypeElement typeElement, TypeSpec.Builder typeSpecBuilder) {

        private void customize() {
            CodeGenHelper.getSuperClass(typeElement)
                    .ifPresent(superclass -> {
                        if (superclass.getQualifiedName().contentEquals(BaseMbpAggregate.class.getCanonicalName())) {
                            typeSpecBuilder.addField(FieldSpec.builder(String.class, "id", Modifier.PRIVATE).build());
                            typeSpecBuilder.addField(FieldSpec.builder(String.class, "createdAt", Modifier.PRIVATE).build());
                            typeSpecBuilder.addField(FieldSpec.builder(String.class, "updatedAt", Modifier.PRIVATE).build());
                        }
                    });
        }
    }
}
