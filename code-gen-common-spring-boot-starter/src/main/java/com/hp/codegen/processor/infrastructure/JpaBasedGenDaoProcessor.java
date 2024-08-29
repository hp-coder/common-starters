package com.hp.codegen.processor.infrastructure;


import com.google.auto.service.AutoService;
import com.hp.codegen.annotation.infrastructure.GenDao;
import com.hp.codegen.annotation.infrastructure.GenPo;
import com.hp.codegen.constant.Orm;
import com.hp.codegen.context.CodeGenContextHolder;
import com.hp.codegen.spi.CodeGenProcessor;
import com.hp.jpa.BaseRepository;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import org.springframework.stereotype.Repository;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.lang.annotation.Annotation;

/**
 * @author hp
 */
@AutoService(value = CodeGenProcessor.class)
public class JpaBasedGenDaoProcessor extends AbstractInfrastructureCodeGenProcessor {

    @Override
    protected boolean isInterface() {
        return true;
    }

    @Override
    public boolean supportedOrm(Orm orm) {
        return Orm.SPRING_DATA_JPA.equals(orm);
    }

    @Override
    public Class<? extends Annotation> getAnnotation() {
        return GenDao.class;
    }

    @Override
    protected boolean creatable(TypeElement typeElement, RoundEnvironment roundEnvironment) {
        if (!CodeGenContextHolder.isAnnotationPresent(GenPo.class)) {
            CodeGenContextHolder.log(
                    Diagnostic.Kind.MANDATORY_WARNING,
                    String.format("To generate %s requires @GenPo.", this.currentGeneratingTypeName)
            );
            return false;
        }
        return true;
    }

    @Override
    protected void customizeType(TypeElement typeElement, RoundEnvironment roundEnvironment, TypeSpec.Builder typeSpecBuilder) {
        typeSpecBuilder
                .addAnnotation(Repository.class)
                .addSuperinterface(ParameterizedTypeName.get(ClassName.get(BaseRepository.class), CodeGenContextHolder.getClassName(GenPo.class), ClassName.get(Long.class)))
                .addModifiers(Modifier.PUBLIC);
    }
}
