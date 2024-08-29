
package com.hp.codegen.processor.infrastructure;


import com.google.auto.service.AutoService;
import com.google.common.collect.Lists;
import com.hp.codegen.annotation.domain.GenCommandRepository;
import com.hp.codegen.annotation.infrastructure.GenCommandRepositoryAdapter;
import com.hp.codegen.annotation.infrastructure.GenDao;
import com.hp.codegen.annotation.infrastructure.GenMapperAdapter;
import com.hp.codegen.annotation.infrastructure.GenPo;
import com.hp.codegen.constant.Orm;
import com.hp.codegen.context.CodeGenContextHolder;
import com.hp.codegen.spi.CodeGenProcessor;
import com.hp.codegen.util.CodeGenHelper;
import com.hp.common.base.facade.publisher.EventPublisher;
import com.hp.common.base.facade.repository.AbstractCommandRepository;
import com.squareup.javapoet.*;
import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Component;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author hp
 */
@AutoService(value = CodeGenProcessor.class)
public class MybatisBasedGenCommandRepositoryAdapterProcessor extends AbstractInfrastructureCodeGenProcessor {

    @Override
    public Class<? extends Annotation> getAnnotation() {
        return GenCommandRepositoryAdapter.class;
    }

    @Override
    public boolean supportedOrm(Orm orm) {
        return Objects.equals(orm, Orm.MYBATIS_PLUS);
    }

    @Override
    protected void customizeType(TypeElement typeElement, RoundEnvironment roundEnvironment, TypeSpec.Builder typeSpecBuilder) {
        typeSpecBuilder
                .addAnnotation(Component.class)
                .addSuperinterface(CodeGenContextHolder.getClassName(GenCommandRepository.class))
                .addModifiers(Modifier.PUBLIC)
                .superclass(ParameterizedTypeName.get(ClassName.get(AbstractCommandRepository.class), CodeGenContextHolder.getCurrentTypeClassName()));

        typeSpecBuilder.addFields(createInjectionFields());
        typeSpecBuilder.addMethod(createConstructor());

        createSaveMethod().ifPresent(typeSpecBuilder::addMethod);
    }

    @Nonnull
    private static List<FieldSpec> createInjectionFields() {
        final List<FieldSpec> fieldSpecs = Lists.newArrayList();
        if (CodeGenContextHolder.isAnnotationPresent(GenDao.class)) {
            final FieldSpec serviceInjectionField = CodeGenHelper.createFieldSpecBuilder(GenDao.class)
                    .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                    .build();
            fieldSpecs.add(serviceInjectionField);
        }
        return fieldSpecs;
    }

    protected boolean methodNotCreatable(String methodName) {
        if (CodeGenContextHolder.missingAnyAnnotated(
                GenPo.class,
                GenMapperAdapter.class,
                GenDao.class
        )) {
            CodeGenContextHolder.log(
                    Diagnostic.Kind.MANDATORY_WARNING,
                    String.format("To generate %s() on %s requires @GenPo @GenMapperAdapter and @GenDao.", methodName, this.currentGeneratingTypeName)
            );
            return true;
        }
        return false;
    }

    protected MethodSpec createConstructor() {
        return MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ClassName.get(EventPublisher.class), "eventPublisher")
                .addParameter(CodeGenContextHolder.getClassName(GenDao.class), CodeGenContextHolder.getClassFieldName(GenDao.class))
                .addCode(
                        CodeBlock.of(
                                """
                                        super(eventPublisher);
                                        this.$L = $L;
                                         """,
                                CodeGenContextHolder.getClassFieldName(GenDao.class),
                                CodeGenContextHolder.getClassFieldName(GenDao.class)
                        )
                )
                .build();
    }

    protected Optional<MethodSpec> createSaveMethod() {
        final String methodName = "save%s".formatted(CodeGenContextHolder.getCurrentTypeName());
        if (methodNotCreatable(methodName)) {
            return Optional.empty();
        }

        final MethodSpec methodSpec = MethodSpec.methodBuilder(methodName)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(
                        ParameterSpec.builder(CodeGenContextHolder.getCurrentTypeClassName(), CodeGenContextHolder.getCurrentTypeFieldName())
                                .build()
                )
                .addCode(
                        CodeBlock.of(
                                """
                                        final $T $L = $T.ADAPTER.entityToPO($L);
                                        $L.insert($L);
                                        """,
                                CodeGenContextHolder.getClassName(GenPo.class),
                                CodeGenContextHolder.getClassFieldName(GenPo.class),
                                CodeGenContextHolder.getClassName(GenMapperAdapter.class),
                                CodeGenContextHolder.getCurrentTypeFieldName(),
                                CodeGenContextHolder.getClassFieldName(GenDao.class),
                                CodeGenContextHolder.getClassFieldName(GenPo.class)
                        )
                )
                .addCode(
                        CodeBlock.of("afterSync($L);", CodeGenContextHolder.getCurrentTypeFieldName())
                )
                .build();
        return Optional.of(methodSpec);

    }
}
