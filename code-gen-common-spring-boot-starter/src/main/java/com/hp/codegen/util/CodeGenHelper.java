package com.hp.codegen.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.google.auto.common.MoreTypes;
import com.hp.codegen.annotation.api.GenApi;
import com.hp.codegen.modifier.FieldSpecCreator;
import com.hp.codegen.modifier.FieldSpecCreatorFactory;
import com.hp.codegen.annotation.Ignore;
import com.hp.codegen.context.CodeGenContextHolder;
import com.hp.common.base.enums.BaseEnum;
import com.squareup.javapoet.*;
import jakarta.annotation.Nonnull;
import lombok.experimental.UtilityClass;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author hp
 */
@UtilityClass
public class CodeGenHelper {

    public static void createFields(TypeSpec.Builder builder, Collection<VariableElement> fields, ClassName currentGeneratingClassName) {
        final FieldSpecCreator creator = FieldSpecCreatorFactory.createDefaultEmptyConverterFieldSpecCreator(currentGeneratingClassName);
        createFields(builder, fields, creator);
    }

    public static void createFields(TypeSpec.Builder builder, Collection<VariableElement> fields, FieldSpecCreator creator) {
        if (CollUtil.isEmpty(fields)) {
            return;
        }
        fields.forEach(ve -> Optional.ofNullable(creator.createFields(ve)).ifPresent(builder::addFields));
    }

    public static String getFieldMethodName(VariableElement variableElement) {
        return StrUtil.upperFirst(variableElement.getSimpleName());
    }

    public static List<VariableElement> findFields(TypeElement typeElement, Predicate<VariableElement> predicate) {
        final List<? extends Element> enclosedElements = typeElement
                .getEnclosedElements()
                .stream()
                .filter(element -> element.getKind().isField())
                .collect(Collectors.toList());

        final List<VariableElement> variableElements = ElementFilter.fieldsIn(enclosedElements);

        return variableElements.stream().filter(predicate).distinct().collect(Collectors.toList());
    }

    public static Optional<DeclaredType> getBaseEnumType(VariableElement variableElement) {
        final TypeMirror type = variableElement.asType();
        if (type.getKind().isPrimitive()) {
            return Optional.empty();
        }
        final TypeElement typeElement = MoreTypes.asTypeElement(type);
        if (typeElement.getKind() != ElementKind.ENUM) {
            return Optional.empty();
        }
        return typeElement.getInterfaces()
                .stream()
                .map(MoreTypes::asDeclared)
                .filter(dt -> MoreTypes.isTypeOf(BaseEnum.class, dt))
                .findAny();
    }

    public static TypeName getBaseEnumCodeType(DeclaredType declaredType) {
        return ClassName.bestGuess(MoreTypes.asTypeElement(declaredType.getTypeArguments().get(1)).getQualifiedName().toString());
    }

    public static TypeName getBaseEnumType(DeclaredType declaredType) {
        return ClassName.bestGuess(MoreTypes.asTypeElement(declaredType.getTypeArguments().get(0)).getQualifiedName().toString());
    }

    public static boolean isBaseEnumType(VariableElement variableElement) {
        return getBaseEnumType(variableElement).isPresent();
    }

    public static List<VariableElement> findNotIgnoreAndNotDeprecatedFields(TypeElement typeElement) {
        final Predicate<VariableElement> predicate = v -> Objects.isNull(v.getAnnotation(Ignore.class)) && Objects.isNull(v.getAnnotation(Deprecated.class));
        return findFields(typeElement, predicate);
    }

    public static Optional<TypeElement> getSuperClass(TypeElement typeElement) {
        final TypeMirror superclass = typeElement.getSuperclass();
        if (superclass instanceof DeclaredType) {
            final Element element = ((DeclaredType) superclass).asElement();
            if (element instanceof TypeElement) {
                return Optional.of(((TypeElement) element));
            }
        }
        return Optional.empty();
    }

    public static ParameterSpec.Builder createParameterSpecBuilder(Class<? extends Annotation> clazz, @Nonnull String parameterName) {
        return ParameterSpec.builder(CodeGenContextHolder.getClassName(clazz), parameterName);
    }

    public static FieldSpec.Builder createFieldSpecBuilder(Class<? extends Annotation> clazz) {
        return createFieldSpecBuilder(clazz, CodeGenContextHolder.getClassFieldName(clazz));
    }

    public static FieldSpec.Builder createFieldSpecBuilder(Class<? extends Annotation> clazz, String fieldName) {
        return FieldSpec.builder(CodeGenContextHolder.getClassName(clazz), fieldName);
    }

    public static String getApiVersion(TypeElement typeElement, Class<? extends Annotation> apiAnnotation) {
        return Optional.ofNullable(typeElement.getAnnotation(GenApi.class))
                .map(GenApi::apiVersion)
                .orElse(Optional.ofNullable(typeElement.getAnnotation(apiAnnotation))
                        .flatMap(ann ->
                                Optional.ofNullable(ReflectUtil.getMethod(apiAnnotation, "apiVersion"))
                                        .map(method -> ReflectUtil.invoke(ann, method))
                                        .map(String::valueOf)
                        )
                        .orElse("v1")
                );
    }
}


