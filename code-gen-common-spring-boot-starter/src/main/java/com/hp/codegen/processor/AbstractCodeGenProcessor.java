package com.hp.codegen.processor;

import com.hp.codegen.context.ProcessingEnvironmentContextHolder;
import com.hp.codegen.spi.CodeGenProcessor;
import com.squareup.javapoet.*;
import lombok.Data;
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

    public void generateGettersAndSettersWithLombok(TypeSpec.Builder typeSpecBuilder, Set<VariableElement> variableElements) {
        typeSpecBuilder.addAnnotation(Data.class);
        typeSpecBuilder.addAnnotation(AnnotationSpec.builder(Accessors.class)
                .addMember("chain", "$L", true).build());
        variableElements.forEach(_0 -> {
            final TypeName typeName = TypeName.get(_0.asType());
            final String fieldName = _0.getSimpleName().toString();
            final FieldSpec field = FieldSpec.builder(typeName, fieldName, Modifier.PRIVATE).build();
            typeSpecBuilder.addField(field);
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
                    .addModifiers(Modifier.PUBLIC)
                    .returns(void.class)
                    .addStatement("this.$L = $L", fieldName, fieldName).build();

            typeSpecBuilder.addMethod(getter);
            typeSpecBuilder.addMethod(setter);
        });

    }

    protected String getFieldMethodName(VariableElement variableElement) {
        final String fieldName = variableElement.getSimpleName().toString();
        final char[] chars = fieldName.toCharArray();
        chars[0] += 32;
        return new String(chars);
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


}
