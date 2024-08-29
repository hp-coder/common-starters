package com.hp.codegen.context;

import cn.hutool.core.map.WeakConcurrentMap;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.base.Preconditions;
import com.hp.codegen.constant.GenerateTarget;
import com.hp.codegen.registry.CodeGenProcessorRegistry;
import com.hp.codegen.spi.CodeGenProcessor;
import com.hp.common.base.context.Context;
import com.hp.common.base.utils.ParameterHelper;
import jakarta.annotation.Nullable;
import lombok.Getter;

import javax.lang.model.element.TypeElement;
import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * @author hp
 */
public class TypeElementContext implements Context {

    private final Map<Class<? extends Annotation>, CodeGenAnnotationMeta> CACHE = new WeakConcurrentMap<>();

    @Getter
    private final TypeElement typeElement;

    public TypeElementContext(TypeElement typeElement) {
        this.typeElement = typeElement;
        final Set<CodeGenProcessor> supportedProcessors = CodeGenProcessorRegistry.getSupportedProcessors();

        supportedProcessors.forEach(
                processor -> Optional.ofNullable(typeElement.getAnnotation(processor.getAnnotationGroup()))
                        .ifPresent(groupedAnnotation -> {
                            final Annotation annotation = groupedAnnotation.annotationType().getAnnotation(processor.getAnnotation());
                            if (Objects.nonNull(annotation)) {
                                CACHE.put(processor.getAnnotation(), new CodeGenAnnotationMeta(typeElement, groupedAnnotation, annotation));
                            }
                        })
        );

        supportedProcessors.forEach(
                processor -> Optional.ofNullable(typeElement.getAnnotation(processor.getAnnotation()))
                        .ifPresent(annotation -> CACHE.put(processor.getAnnotation(), new CodeGenAnnotationMeta(typeElement, null, annotation)))
        );
    }

    public GenerateTarget getGenerateTarget(Class<? extends Annotation> clazz) {
        if (CACHE.containsKey(clazz)) {
            return CACHE.get(clazz).getGenerateTarget();
        }
        throw new IllegalArgumentException("The %s is not annotated on the %s".formatted(clazz.getSimpleName(), typeElement.getSimpleName()));
    }

    public String getPackageName(Class<? extends Annotation> clazz) {
        if (CACHE.containsKey(clazz)) {
            return CACHE.get(clazz).getPackageName();
        }
        throw new IllegalArgumentException("The %s is not annotated on the %s".formatted(clazz.getSimpleName(), typeElement.getSimpleName()));
    }

    public String getSourcePath(Class<? extends Annotation> clazz) {
        if (CACHE.containsKey(clazz)) {
            return CACHE.get(clazz).getSourcePath();
        }
        throw new IllegalArgumentException("The %s is not annotated on the %s".formatted(clazz.getSimpleName(), typeElement.getSimpleName()));
    }

    public boolean isPresent(Class<? extends Annotation> annotation) {
        return CACHE.containsKey(annotation);
    }

    public String getClassFieldName(Class<? extends Annotation> clazz) {
        if (CACHE.containsKey(clazz)) {
            final CodeGenAnnotationMeta meta = CACHE.get(clazz);
            if (Objects.equals(meta.getPrefix(), "I")) {
                return meta.getClassName().substring(1);
            }
            return meta.getClassName();
        }
        throw new IllegalArgumentException("The %s is not annotated on the %s".formatted(clazz.getSimpleName(), typeElement.getSimpleName()));
    }

    public String getClassName(Class<? extends Annotation> clazz) {
        if (CACHE.containsKey(clazz)) {
            return CACHE.get(clazz).getClassName();
        }
        throw new IllegalArgumentException("The %s is not annotated on the %s".formatted(clazz.getSimpleName(), typeElement.getSimpleName()));
    }

    @Getter
    public static class CodeGenAnnotationMeta {

        private String packageName;

        private final String sourcePath;

        private String prefix;

        private String suffix;

        private final String className;

        private final GenerateTarget generateTarget;

        public CodeGenAnnotationMeta(TypeElement typeElement, @Nullable Annotation groupedAnnotation, Annotation annotation) {
            String packageName;
            if (Objects.nonNull(groupedAnnotation)) {
                packageName = ReflectUtil.invoke(groupedAnnotation, ReflectUtil.getMethod(groupedAnnotation.annotationType(), "packageName"));
                Preconditions.checkArgument(StrUtil.isNotEmpty(packageName), "The packageName in the @%s annotated on class %s can not be empty".formatted(groupedAnnotation.annotationType().getSimpleName(), typeElement.getSimpleName()));
                this.sourcePath = ReflectUtil.invoke(groupedAnnotation, ReflectUtil.getMethod(groupedAnnotation.annotationType(), "sourcePath"));
            } else {
                packageName = ReflectUtil.invoke(annotation, ReflectUtil.getMethod(annotation.annotationType(), "packageName"));
                this.sourcePath = ReflectUtil.invoke(annotation, ReflectUtil.getMethod(annotation.annotationType(), "sourcePath"));
            }
            Preconditions.checkArgument(StrUtil.isNotEmpty(packageName), "The packageName in the @%s annotated on class %s can not be empty".formatted(annotation.annotationType().getSimpleName(), typeElement.getSimpleName()));

            Optional.ofNullable(ReflectUtil.getMethod(annotation.annotationType(), "subPackageName"))
                    .ifPresentOrElse(
                            method -> {
                                final String subPackageName = Optional.ofNullable(ReflectUtil.invoke(annotation, method))
                                        .map(String::valueOf)
                                        .map(s -> ParameterHelper.Strings.ofNullable(s).map(i -> "." + i)
                                                .orElse(""))
                                        .orElse("");
                                this.packageName = packageName + subPackageName;
                            },
                            () -> this.packageName = packageName
                    );

            Optional.ofNullable(ReflectUtil.getMethod(annotation.annotationType(), "classNamePrefix")).ifPresent(method -> this.prefix = ReflectUtil.invoke(annotation, method));

            Optional.ofNullable(ReflectUtil.getMethod(annotation.annotationType(), "classNameSuffix")).ifPresent(method -> this.suffix = ReflectUtil.invoke(annotation, method));

            this.className = this.prefix + typeElement.getSimpleName().toString() + this.suffix;
            Preconditions.checkArgument(!Objects.equals(this.className, typeElement.getSimpleName().toString()), "The @%s can not be used to generate the source class=%s".formatted(annotation.annotationType().getSimpleName(), typeElement.getSimpleName()));

            this.generateTarget = Optional.ofNullable(ReflectUtil.getMethod(annotation.annotationType(), "target"))
                    .map(method -> (GenerateTarget) ReflectUtil.invoke(annotation, method))
                    .orElse(GenerateTarget.SOURCE);
        }
    }
}
