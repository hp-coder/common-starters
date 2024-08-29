package com.hp.codegen.context;


import cn.hutool.core.util.StrUtil;
import com.alibaba.ttl.TransmittableThreadLocal;
import com.hp.codegen.constant.GenerateTarget;
import com.hp.codegen.constant.MappingMode;
import com.hp.codegen.constant.Orm;
import com.hp.codegen.modifier.FieldSpecCreator;
import com.hp.codegen.modifier.FieldSpecCreatorFactory;
import com.hp.common.base.context.Context;
import com.squareup.javapoet.ClassName;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @author hp
 */
public final class CodeGenContextHolder implements Context {

    private static final TransmittableThreadLocal<CodeGenContext> ENVIRONMENT_THREAD_LOCAL = new TransmittableThreadLocal<>();

    public static CodeGenContext getContext() {
        return ENVIRONMENT_THREAD_LOCAL.get();
    }

    public static void setEnvironment(ProcessingEnvironment environment) {
        ENVIRONMENT_THREAD_LOCAL.set(new CodeGenContext(environment));
    }

    public static ProcessingEnvironment getEnvironment() {
        return ENVIRONMENT_THREAD_LOCAL.get().getProcessingEnvironment();
    }

    public static Map<String, String> getCompileArgs() {
        return ENVIRONMENT_THREAD_LOCAL.get().getProcessingEnvironment().getOptions();
    }

    public static Messager getMessager() {
        return getEnvironment().getMessager();
    }

    public static void log(Diagnostic.Kind kind, String message) {
        getMessager().printMessage(kind, message);
    }

    public static void setOrm(Orm orm) {
        ENVIRONMENT_THREAD_LOCAL.get().setOrm(Objects.requireNonNull(orm));
    }

    public static Orm getOrm() {
        return ENVIRONMENT_THREAD_LOCAL.get().getOrm();
    }

    public static boolean isJpaEnvironment() {
        return Objects.equals(getOrm(), Orm.SPRING_DATA_JPA);
    }

    public static boolean isMyBatisEnvironment() {
        return Objects.equals(getOrm(), Orm.MYBATIS_PLUS);
    }

    public static void setMappingMode(MappingMode mappingMode) {
        ENVIRONMENT_THREAD_LOCAL.get().setMappingMode(Objects.requireNonNull(mappingMode));
    }

    public static boolean isMapStructMappingMode() {
        return Objects.equals(ENVIRONMENT_THREAD_LOCAL.get().getMappingMode(), MappingMode.MapStruct);
    }

    public static boolean isJacksonMappingMode() {
        return Objects.equals(ENVIRONMENT_THREAD_LOCAL.get().getMappingMode(), MappingMode.Jackson);
    }

    public static FieldSpecCreator getFieldCreator(ClassName currentGeneratingClassName, boolean serialization) {
        if (isMapStructMappingMode()) {
            return FieldSpecCreatorFactory.createDefaultMapStructMappingFieldSpecCreator(currentGeneratingClassName);
        } else if (isJacksonMappingMode()) {
            if (serialization){
                return FieldSpecCreatorFactory.createDefaultJacksonSerializerMappingFieldSpecCreator(currentGeneratingClassName);
            }
            return FieldSpecCreatorFactory.createDefaultJacksonDeserializerMappingFieldSpecCreator(currentGeneratingClassName);
        }
        throw new IllegalStateException("not support other mapping mode besides mapstruct");
    }

    public static void createTypeElementContext(TypeElement typeElement) {
        getContext().setTypeElementContext(new TypeElementContext(typeElement));
    }

    public static TypeElement getCurrentTypeElement() {
        return getCurrentTypeElementContext().getTypeElement();
    }

    public static TypeElementContext getCurrentTypeElementContext() {
        return Optional.ofNullable(getContext().getTypeElementContext())
                .orElseThrow(() -> new IllegalStateException("TypeElementContext has not been set yet."));
    }

    public static String getCurrentTypeName() {
        return getCurrentTypeElementContext().getTypeElement().getSimpleName().toString();
    }

    public static String getCurrentTypeFieldName() {
        return StrUtil.lowerFirst(getCurrentTypeName());
    }

    public static ClassName getCurrentTypeClassName() {
        return ClassName.get(getCurrentTypeElementContext().getTypeElement());
    }

    public static boolean isGenerateToSource(Class<? extends Annotation> clazz) {
        return getCurrentTypeElementContext()
                .getGenerateTarget(clazz)
                .equals(GenerateTarget.SOURCE);
    }

    public static boolean isAnnotationPresent(Class<? extends Annotation> annotation) {
        return getCurrentTypeElementContext().isPresent(annotation);
    }

    @SafeVarargs
    public static boolean missingAnyAnnotated(Class<? extends Annotation> annotation, Class<? extends Annotation>... annotations) {
        final boolean annotationPresent = isAnnotationPresent(annotation);
        if (!annotationPresent) {
            return true;
        } else {
            if (annotations != null) {
                for (final Class<? extends Annotation> ann : annotations) {
                    if (!isAnnotationPresent(ann)) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

    public static String getPackageName(Class<? extends Annotation> annotation) {
        return getCurrentTypeElementContext().getPackageName(annotation);
    }

    public static String getSourcePath(Class<? extends Annotation> annotation) {
        return getCurrentTypeElementContext().getSourcePath(annotation);
    }

    public static String getClassSimpleName(Class<? extends Annotation> annotation) {
        return getCurrentTypeElementContext().getClassName(annotation);
    }

    public static ClassName getClassName(Class<? extends Annotation> annotation) {
        return ClassName.get(getPackageName(annotation), getClassSimpleName(annotation));
    }

    public static String getClassFieldName(Class<? extends Annotation> annotation) {
        final String classFieldName = getCurrentTypeElementContext().getClassFieldName(annotation);
        return StrUtil.lowerFirst(classFieldName);
    }
}
