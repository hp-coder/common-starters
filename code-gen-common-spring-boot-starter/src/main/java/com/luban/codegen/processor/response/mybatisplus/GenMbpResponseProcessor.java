package com.luban.codegen.processor.response.mybatisplus;

import com.google.auto.service.AutoService;
import com.google.common.collect.Lists;
import com.luban.codegen.constant.Orm;
import com.luban.codegen.processor.AbstractCodeGenProcessor;
import com.luban.codegen.processor.Ignore;
import com.luban.codegen.processor.modifier.BaseEnumFieldSpecModifier;
import com.luban.codegen.processor.modifier.FieldSpecModifier;
import com.luban.codegen.processor.modifier.LongToStringFieldSpecModifier;
import com.luban.codegen.processor.modifier.mybatisplus.MybatisplusTypeHandlerFieldSpecModifier;
import com.luban.codegen.processor.response.GenResponse;
import com.luban.codegen.spi.CodeGenProcessor;
import com.luban.common.base.model.Response;
import com.luban.mybatisplus.BaseMbpAggregate;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author HP
 * @date 2022/10/24
 */
@AutoService(CodeGenProcessor.class)
public class GenMbpResponseProcessor extends AbstractCodeGenProcessor {

    public static final String RESPONSE_SUFFIX = "Response";

    @Override
    public boolean supportedOrm(Orm orm) {
        return Objects.equals(orm, Orm.MYBATIS_PLUS);
    }

    @Override
    protected void generateClass(TypeElement typeElement, RoundEnvironment roundEnvironment) {
        List<VariableElement> fields = findFields(typeElement, v ->
                Objects.isNull(v.getAnnotation(Ignore.class)) &&
                        Objects.isNull(v.getAnnotation(Deprecated.class))
        );
        String sourceClassName = typeElement.getSimpleName() + RESPONSE_SUFFIX;
        TypeSpec.Builder builder = TypeSpec.classBuilder(sourceClassName)
                .addSuperinterface(Response.class)
                .addModifiers(Modifier.PUBLIC);

        getSuperClass(typeElement).ifPresent(superclass -> {
            if (superclass.getQualifiedName().contentEquals(BaseMbpAggregate.class.getCanonicalName())) {
                builder.superclass(AbstractMbpBaseResponse.class);
            }
        });

        final ArrayList<FieldSpecModifier> fieldSpecModifiers = Lists.newArrayList(
                new LongToStringFieldSpecModifier(),
                new MybatisplusTypeHandlerFieldSpecModifier(),
                new BaseEnumFieldSpecModifier()
        );
        generateGettersAndSettersWithLombok(builder, fields, fieldSpecModifiers);
        String packageName = generatePackage(typeElement);
        generateJavaSourceFile(packageName, typeElement.getAnnotation(GenResponse.class).sourcePath(), builder);
    }

    @Override
    public Class<? extends Annotation> getAnnotation() {
        return GenResponse.class;
    }

    @Override
    public String generatePackage(TypeElement typeElement) {
        return typeElement.getAnnotation(GenResponse.class).pkgName();
    }
}
