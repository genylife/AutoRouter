package com.dinglc.router.processors;

import com.dinglc.router.annoation.RequestInt;
import com.dinglc.router.annoation.RequestString;
import com.dinglc.router.annoation.RouterKey;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

@AutoService(Processor.class)
public class RouterProcessor extends AbstractProcessor {

    private TypeSpec.Builder routerServiceClassBuilder;
    private Filer mFiler;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mFiler = processingEnv.getFiler();
        routerServiceClassBuilder = TypeSpec.interfaceBuilder("RouterService")
                .addModifiers(Modifier.PUBLIC);
        //        JavaFile routerService = JavaFile.builder("com.test",)
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(RequestInt.class)) {
            //            System.out.println("------------------------------");
            //            if(element.getKind() == ElementKind.CLASS) {
            //                TypeElement typeElement = (TypeElement) element;
            //                System.out.println(typeElement.getSimpleName());
            //                System.out.println(typeElement.getAnnotation(RequestInt.class).value());
            //            }
            //            System.out.println("------------------------------");
            if(element.getKind() == ElementKind.CLASS) {
                TypeElement typeElement = (TypeElement) element;
                String annotationValue = typeElement.getAnnotation(RequestInt.class).value();
                AnnotationSpec annotationSpec = AnnotationSpec.builder(RouterKey.class)
                        .addMember("value", "\"" + annotationValue + "\"")
                        .build();
                ParameterSpec parameterSpec = ParameterSpec.builder(TypeName.INT, annotationValue)
                        .addAnnotation(annotationSpec)
                        .build();
                MethodSpec tempMethod = MethodSpec.methodBuilder("to" + typeElement.getSimpleName())
                        .addParameter(parameterSpec)
                        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                        .build();
                routerServiceClassBuilder.addMethod(tempMethod);
            }
        }
        JavaFile.Builder builder = JavaFile.builder("com.test", routerServiceClassBuilder.build());
        JavaFile javaFile = builder.build();
        try {
            javaFile.writeTo(mFiler);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //        System.out.println("------------------------------------------");
        //        try {
        //            javaFile.writeTo(System.out);
        //        } catch (IOException e) {
        //            e.printStackTrace();
        //        }
        //        System.out.println("------------------------------------------");
        return false;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        LinkedHashSet<String> annotations = new LinkedHashSet<>();
        annotations.add(RequestInt.class.getCanonicalName());
        annotations.add(RequestString.class.getCanonicalName());
        return annotations;
    }


    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
