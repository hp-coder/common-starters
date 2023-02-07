package com.luban.codegen.processor;

import com.luban.codegen.context.DefaultNameContext;
import com.luban.codegen.context.ProcessingEnvironmentContextHolder;
import com.luban.codegen.processor.api.GenFeign;
import com.luban.codegen.processor.controller.GenController;
import com.luban.codegen.processor.controller.GenControllerProcessor;
import com.luban.codegen.processor.dto.GenDto;
import com.luban.codegen.processor.dto.GenDtoProcessor;
import com.luban.codegen.processor.mapper.GenMapper;
import com.luban.codegen.processor.mapper.GenMapperProcessor;
import com.luban.codegen.processor.repository.GenRepository;
import com.luban.codegen.processor.repository.GenRepositoryProcessor;
import com.luban.codegen.processor.request.GenRequest;
import com.luban.codegen.processor.request.GenRequestProcessor;
import com.luban.codegen.processor.response.GenResponse;
import com.luban.codegen.processor.response.GenResponseProcessor;
import com.luban.codegen.processor.service.GenService;
import com.luban.codegen.processor.service.GenServiceImpl;
import com.luban.codegen.processor.service.GenServiceImplProcessor;
import com.luban.codegen.processor.service.GenServiceProcessor;
import com.luban.codegen.processor.vo.GenVo;
import com.luban.codegen.processor.vo.GenVoProcessor;
import com.luban.codegen.spi.CodeGenProcessor;
import com.squareup.javapoet.*;
import lombok.experimental.Accessors;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author HP
 * @date 2022/10/24
 */
public abstract class AbstractCodeGenProcessor implements CodeGenProcessor {

    protected static final String FILE_COMMENT = "--- Auto Generated By CodeGen Module ---";


    @Override
    public void generate(TypeElement typeElement, RoundEnvironment roundEnvironment) {
        generateClass(typeElement, roundEnvironment);
    }

    protected abstract void generateClass(TypeElement typeElement, RoundEnvironment roundEnvironment);


    public Set<VariableElement> findFields(TypeElement element, Predicate<VariableElement> predicate) {
        final List<? extends Element> enclosedElements = element.getEnclosedElements();
        final List<VariableElement> variableElements = ElementFilter.fieldsIn(enclosedElements);
        return variableElements.stream().filter(predicate).collect(Collectors.toSet());
    }

    public TypeElement getSuperClass(TypeElement typeElement) {
        final TypeMirror superclass = typeElement.getSuperclass();
        if (superclass instanceof DeclaredType) {
            final Element element = ((DeclaredType) superclass).asElement();
            if (element instanceof TypeElement) {
                return ((TypeElement) element);
            }
        }
        return null;
    }

    public void generateGettersAndSettersWithLombok(TypeSpec.Builder builder, Set<VariableElement> variableElements) {
        builder.addAnnotation(AnnotationSpec.builder(Accessors.class)
                .addMember("chain", "$L", true).build());
        variableElements.forEach(_0 -> {
            final TypeName typeName = TypeName.get(_0.asType());
            final String fieldName = _0.getSimpleName().toString();
            final FieldSpec field = FieldSpec.builder(typeName, fieldName, Modifier.PRIVATE).build();
            builder.addField(field);
        });
    }

    public void generateGettersAndSetters(TypeSpec.Builder typeSpecBuilder, Set<VariableElement> variableElements) {
        ProcessingEnvironmentContextHolder.getMessager().printMessage(Diagnostic.Kind.MANDATORY_WARNING, "Starting To Create Getters & Setters");
        variableElements.forEach(_0 -> {
            final TypeName typeName = TypeName.get(_0.asType());
            final String fieldName = _0.getSimpleName().toString();
            final FieldSpec field = FieldSpec.builder(typeName, fieldName, Modifier.PRIVATE).build();
            typeSpecBuilder.addField(field);

            final String fieldMethodName = getFieldMethodName(_0);
            final MethodSpec getter = MethodSpec.methodBuilder("get" + fieldMethodName)
                    .addModifiers(Modifier.PUBLIC)
                    .returns(typeName)
                    .addStatement("return $L", fieldName).build();

            final MethodSpec setter = MethodSpec.methodBuilder("set" + fieldMethodName)
                    .addParameter(typeName, _0.getSimpleName().toString())
                    .addModifiers(Modifier.PUBLIC)
                    .returns(void.class)
                    .addStatement("this.$L = $L", fieldName, fieldName).build();

            typeSpecBuilder.addMethod(getter);
            typeSpecBuilder.addMethod(setter);
        });
    }

    protected String getFieldMethodName(VariableElement ve) {
        return ve.getSimpleName().toString().substring(0, 1).toUpperCase() + ve.getSimpleName()
                .toString().substring(1);
    }


    protected void generateJavaFile(String packageName, TypeSpec.Builder typeSpecBuilder) {
        final JavaFile javaFile = JavaFile.builder(packageName, typeSpecBuilder.build())
                .addFileComment(FILE_COMMENT)
                .build();
        try {
            javaFile.writeTo(ProcessingEnvironmentContextHolder.getEnvironment().getFiler());
        } catch (IOException e) {
            ProcessingEnvironmentContextHolder.getEnvironment().getMessager()
                    .printMessage(Diagnostic.Kind.ERROR, e.getMessage());
        }
    }

    protected void generateJavaSourceFile(String packageName, String path, TypeSpec.Builder typeSpecBuilder) {
        final TypeSpec typeSpec = typeSpecBuilder.build();
        final JavaFile javaFile = JavaFile.builder(packageName, typeSpec)
                .addFileComment(FILE_COMMENT)
                .build();
        final String fileName = typeSpec.name + ".java";
        final String packagePath = packageName.replace(".", File.separator) + File.separator + fileName;
        try {
            final String absolutePath = Paths.get(path).toFile().getAbsolutePath();
            File file = new File(absolutePath);
            if (!file.exists()) {
                return;
            }
            String sourceFileName = absolutePath + File.separator + packagePath;
            File sourceFile = new File(sourceFileName);
            if (!sourceFile.exists()) {
                javaFile.writeTo(file);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取名称默认上下文
     *
     * @param typeElement
     * @return
     */
    public DefaultNameContext getNameContext(TypeElement typeElement) {
        DefaultNameContext context = new DefaultNameContext();
        String serviceName = GenServiceProcessor.SERVICE_PREFIX + typeElement.getSimpleName() + GenServiceProcessor.SERVICE_SUFFIX;
        String implName = typeElement.getSimpleName() + GenServiceImplProcessor.IMPL_SUFFIX;
        String repositoryName = typeElement.getSimpleName() + GenRepositoryProcessor.REPOSITORY_SUFFIX;
        String mapperName = typeElement.getSimpleName() + GenMapperProcessor.SUFFIX;
        String voName = typeElement.getSimpleName() + GenVoProcessor.SUFFIX;
        String dtoName = typeElement.getSimpleName() + GenDtoProcessor.SUFFIX;
        String responseName = typeElement.getSimpleName() + GenResponseProcessor.RESPONSE_SUFFIX;
        String requestName = typeElement.getSimpleName() + GenRequestProcessor.SUFFIX;
        String controllerName = typeElement.getSimpleName() + GenControllerProcessor.CONTROLLER_SUFFIX;

        context.setServiceClassName(serviceName);
        context.setRepositoryClassName(repositoryName);
        context.setMapperClassName(mapperName);
        context.setVoClassName(voName);
        context.setDtoClassName(dtoName);
        context.setImplClassName(implName);
        context.setResponseClassName(responseName);
        context.setRequestClassName(requestName);
        context.setControllerClassName(controllerName);

        Optional.ofNullable(typeElement.getAnnotation(GenController.class)).ifPresent(anno -> context.setControllerPackageName(anno.pkgName()));
        Optional.ofNullable(typeElement.getAnnotation(GenDto.class)).ifPresent(anno -> context.setDtoPackageName(anno.pkgName()));
        Optional.ofNullable(typeElement.getAnnotation(GenVo.class)).ifPresent(anno -> context.setVoPackageName(anno.pkgName()));
        Optional.ofNullable(typeElement.getAnnotation(GenRepository.class)).ifPresent(anno -> context.setRepositoryPackageName(anno.pkgName()));
        Optional.ofNullable(typeElement.getAnnotation(GenMapper.class)).ifPresent(anno -> context.setMapperPackageName(anno.pkgName()));
        Optional.ofNullable(typeElement.getAnnotation(GenService.class)).ifPresent(anno -> context.setServicePackageName(anno.pkgName()));
        Optional.ofNullable(typeElement.getAnnotation(GenServiceImpl.class)).ifPresent(anno -> context.setImplPackageName(anno.pkgName()));
        Optional.ofNullable(typeElement.getAnnotation(GenRequest.class)).ifPresent(anno -> context.setRequestPackageName(anno.pkgName()));
        Optional.ofNullable(typeElement.getAnnotation(GenResponse.class)).ifPresent(anno -> context.setResponsePackageName(anno.pkgName()));
        Optional.ofNullable(typeElement.getAnnotation(GenFeign.class)).ifPresent(anno -> context.setFeignPackageName(anno.pkgName()));
        return context;
    }


}
