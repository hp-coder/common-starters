package com.hp.codegen.processor.infrastructure;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.TableName;
import com.google.auto.service.AutoService;
import com.hp.codegen.annotation.infrastructure.GenInfrastructure;
import com.hp.codegen.annotation.infrastructure.GenPo;
import com.hp.codegen.constant.Orm;
import com.hp.codegen.context.CodeGenContextHolder;
import com.hp.codegen.modifier.FieldSpecCreator;
import com.hp.codegen.modifier.FieldSpecCreatorFactory;
import com.hp.codegen.spi.CodeGenProcessor;
import com.hp.codegen.util.CodeGenHelper;
import com.hp.mybatisplus.BaseMbpAggregate;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.TypeSpec;
import lombok.Data;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author hp
 */
@AutoService(value = CodeGenProcessor.class)
public class MybatisBasedGenPOProcessor extends AbstractInfrastructureCodeGenProcessor {

    @Override
    public Class<? extends Annotation> getAnnotation() {
        return GenPo.class;
    }

    @Override
    public boolean supportedOrm(Orm orm) {
        return Objects.equals(orm, Orm.MYBATIS_PLUS);
    }

    @Override
    protected void customizeType(TypeElement typeElement, RoundEnvironment roundEnvironment, TypeSpec.Builder typeSpecBuilder) {
        final List<VariableElement> fields = CodeGenHelper.findNotIgnoreAndNotDeprecatedFields(typeElement);

        final String tablePrefix = Optional.ofNullable(typeElement.getAnnotation(GenInfrastructure.class))
                .map(GenInfrastructure::tablePrefix)
                .orElse(Optional.ofNullable(typeElement.getAnnotation(GenPo.class)).map(GenPo::tablePrefix).orElse(""));

        typeSpecBuilder
                .superclass(BaseMbpAggregate.class)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Data.class)
                .addAnnotation(
                        AnnotationSpec
                                .builder(TableName.class)
                                .addMember("value", "$S", tablePrefix + StrUtil.toCamelCase(CodeGenContextHolder.getCurrentTypeFieldName()))
                                .addMember("autoResultMap", "$L", true)
                                .build()
                );

        final FieldSpecCreator creator = FieldSpecCreatorFactory.createDefaultOrmConverterFieldSpecCreator(currentGeneratingClassName);
        CodeGenHelper.createFields(typeSpecBuilder, fields, creator);
    }
}
