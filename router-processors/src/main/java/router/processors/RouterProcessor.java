package router.processors;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
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

import router.RouterClass;
import router.RouterKey;
import router.request.AutoRouter;
import router.request.RequestBoolean;
import router.request.RequestByte;
import router.request.RequestChar;
import router.request.RequestDouble;
import router.request.RequestFloat;
import router.request.RequestInt;
import router.request.RequestLong;
import router.request.RequestShort;
import router.request.RequestString;

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
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(AutoRouter.class)) {
            System.out.println("------------------------------");
            if(element.getKind() == ElementKind.CLASS) {
                TypeElement typeElement = (TypeElement) element;
                System.out.println(typeElement.getSimpleName());
                //                System.out.println(typeElement.getAnnotation(RequestInt.class).value());
            }
            System.out.println("------------------------------");
        }

        Map<TypeElement, Set<RouterElement>> typeElementSetMap = scanAllElements(roundEnv);
        for (TypeElement next : typeElementSetMap.keySet()) {//per method
            Set<RouterElement> routerElements = typeElementSetMap.get(next);
            AnnotationSpec methodAnnotationSpec = AnnotationSpec.builder(RouterClass.class)
                    .addMember("value", "\"" + next.getQualifiedName().toString() + "\"")
                    .build();
            String methodName = next.getSimpleName().toString();
            methodName = methodName.substring(0, 1).toLowerCase() + methodName.substring(1, methodName.length());
            MethodSpec.Builder tempMethodBuild = MethodSpec.methodBuilder(methodName)
                    .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                    .addAnnotation(methodAnnotationSpec);
            List<ParameterSpec> parameterSpecList = new ArrayList<>();
            for (RouterElement re : routerElements) {//per annotation
                String[] routerValues = re.getValue();
                for (String str : routerValues) {//per param
                    AnnotationSpec annotationSpec = AnnotationSpec.builder(RouterKey.class)
                            .addMember("value", "\"" + str + "\"")
                            .build();
                    str = str.substring(0, 1).toUpperCase() + str.substring(1, str.length());
                    if(re.getAnnotation().getCanonicalName().equals(RequestInt.class.getCanonicalName())) {
                        ParameterSpec parameterSpec = ParameterSpec.builder(TypeName.INT, "r" + str)
                                .addAnnotation(annotationSpec)
                                .build();
                        parameterSpecList.add(parameterSpec);
                    }
                    if(re.getAnnotation().getCanonicalName().equals(RequestString.class.getCanonicalName())) {
                        ParameterSpec parameterSpec = ParameterSpec.builder(ClassName.get(String.class), "r" + str)
                                .addAnnotation(annotationSpec)
                                .build();
                        parameterSpecList.add(parameterSpec);
                    }
                    if(re.getAnnotation().getCanonicalName().equals(RequestChar.class.getCanonicalName())) {
                        ParameterSpec parameterSpec = ParameterSpec.builder(TypeName.CHAR, "r" + str)
                                .addAnnotation(annotationSpec)
                                .build();
                        parameterSpecList.add(parameterSpec);
                    }
                    if(re.getAnnotation().getCanonicalName().equals(RequestBoolean.class.getCanonicalName())) {
                        ParameterSpec parameterSpec = ParameterSpec.builder(TypeName.BOOLEAN, "r" + str)
                                .addAnnotation(annotationSpec)
                                .build();
                        parameterSpecList.add(parameterSpec);
                    }
                    if(re.getAnnotation().getCanonicalName().equals(RequestByte.class.getCanonicalName())) {
                        ParameterSpec parameterSpec = ParameterSpec.builder(TypeName.BYTE, "r" + str)
                                .addAnnotation(annotationSpec)
                                .build();
                        parameterSpecList.add(parameterSpec);
                    }
                    if(re.getAnnotation().getCanonicalName().equals(RequestShort.class.getCanonicalName())) {
                        ParameterSpec parameterSpec = ParameterSpec.builder(TypeName.SHORT, "r" + str)
                                .addAnnotation(annotationSpec)
                                .build();
                        parameterSpecList.add(parameterSpec);
                    }
                    if(re.getAnnotation().getCanonicalName().equals(RequestLong.class.getCanonicalName())) {
                        ParameterSpec parameterSpec = ParameterSpec.builder(TypeName.LONG, "r" + str)
                                .addAnnotation(annotationSpec)
                                .build();
                        parameterSpecList.add(parameterSpec);
                    }
                    if(re.getAnnotation().getCanonicalName().equals(RequestFloat.class.getCanonicalName())) {
                        ParameterSpec parameterSpec = ParameterSpec.builder(TypeName.FLOAT, "r" + str)
                                .addAnnotation(annotationSpec)
                                .build();
                        parameterSpecList.add(parameterSpec);
                    }
                    if(re.getAnnotation().getCanonicalName().equals(RequestDouble.class.getCanonicalName())) {
                        ParameterSpec parameterSpec = ParameterSpec.builder(TypeName.DOUBLE, "r" + str)
                                .addAnnotation(annotationSpec)
                                .build();
                        parameterSpecList.add(parameterSpec);
                    }
                }
            }
            parameterSpecList.sort(Comparator.comparing(o -> o.name));
            TypeName returnType = ClassName.get("router", "IntentWrapper");
            tempMethodBuild.addParameters(parameterSpecList).returns(returnType);
            routerServiceClassBuilder.addMethod(tempMethodBuild.build());
        }
        //        System.out.println("------------------------------");
        //        System.out.println(roundEnv.processingOver());
        //        if(roundEnv.processingOver()) {
        JavaFile.Builder builder = JavaFile.builder("router", routerServiceClassBuilder.build());
        JavaFile javaFile = builder.build();
        try {
            javaFile.writeTo(mFiler);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //        }
        //        System.out.println("------------------------------");

        return true;
    }


    private Map<TypeElement, Set<RouterElement>> scanAllElements(RoundEnvironment roundEnv) {
        Map<TypeElement, Set<RouterElement>> result = new HashMap<>();
        //  0
        for (Element element : roundEnv.getElementsAnnotatedWith(AutoRouter.class)) {
            if(element.getKind() == ElementKind.CLASS) {
                TypeElement typeElement = (TypeElement) element;
                RouterElement routerElement = new RouterElement(AutoRouter.class,
                        new String[]{});
                intoMap(result, typeElement, routerElement);
            }
        }
        //  1
        for (Element element : roundEnv.getElementsAnnotatedWith(RequestInt.class)) {
            if(element.getKind() == ElementKind.CLASS) {
                TypeElement typeElement = (TypeElement) element;
                RouterElement routerElement = new RouterElement(RequestInt.class,
                        typeElement.getAnnotation(RequestInt.class).value());
                intoMap(result, typeElement, routerElement);
            }
        }
        //  2
        for (Element element : roundEnv.getElementsAnnotatedWith(RequestString.class)) {
            if(element.getKind() == ElementKind.CLASS) {
                TypeElement typeElement = (TypeElement) element;
                RouterElement routerElement = new RouterElement(RequestString.class,
                        typeElement.getAnnotation(RequestString.class).value());
                intoMap(result, typeElement, routerElement);
            }
        }
        //  3
        for (Element element : roundEnv.getElementsAnnotatedWith(RequestByte.class)) {
            if(element.getKind() == ElementKind.CLASS) {
                TypeElement typeElement = (TypeElement) element;
                RouterElement routerElement = new RouterElement(RequestByte.class,
                        typeElement.getAnnotation(RequestByte.class).value());
                intoMap(result, typeElement, routerElement);
            }
        }
        //  4
        for (Element element : roundEnv.getElementsAnnotatedWith(RequestBoolean.class)) {
            if(element.getKind() == ElementKind.CLASS) {
                TypeElement typeElement = (TypeElement) element;
                RouterElement routerElement = new RouterElement(RequestBoolean.class,
                        typeElement.getAnnotation(RequestBoolean.class).value());
                intoMap(result, typeElement, routerElement);
            }
        }
        //  5
        for (Element element : roundEnv.getElementsAnnotatedWith(RequestChar.class)) {
            if(element.getKind() == ElementKind.CLASS) {
                TypeElement typeElement = (TypeElement) element;
                RouterElement routerElement = new RouterElement(RequestChar.class,
                        typeElement.getAnnotation(RequestChar.class).value());
                intoMap(result, typeElement, routerElement);
            }
        }
        //  6
        for (Element element : roundEnv.getElementsAnnotatedWith(RequestShort.class)) {
            if(element.getKind() == ElementKind.CLASS) {
                TypeElement typeElement = (TypeElement) element;
                RouterElement routerElement = new RouterElement(RequestShort.class,
                        typeElement.getAnnotation(RequestShort.class).value());
                intoMap(result, typeElement, routerElement);
            }
        }
        //  7
        for (Element element : roundEnv.getElementsAnnotatedWith(RequestLong.class)) {
            if(element.getKind() == ElementKind.CLASS) {
                TypeElement typeElement = (TypeElement) element;
                RouterElement routerElement = new RouterElement(RequestLong.class,
                        typeElement.getAnnotation(RequestLong.class).value());
                intoMap(result, typeElement, routerElement);
            }
        }
        //  8
        for (Element element : roundEnv.getElementsAnnotatedWith(RequestFloat.class)) {
            if(element.getKind() == ElementKind.CLASS) {
                TypeElement typeElement = (TypeElement) element;
                RouterElement routerElement = new RouterElement(RequestFloat.class,
                        typeElement.getAnnotation(RequestFloat.class).value());
                intoMap(result, typeElement, routerElement);
            }
        }
        //  9
        for (Element element : roundEnv.getElementsAnnotatedWith(RequestDouble.class)) {
            if(element.getKind() == ElementKind.CLASS) {
                TypeElement typeElement = (TypeElement) element;
                RouterElement routerElement = new RouterElement(RequestDouble.class,
                        typeElement.getAnnotation(RequestDouble.class).value());
                intoMap(result, typeElement, routerElement);
            }
        }
        return result;
    }

    private void intoMap(Map<TypeElement, Set<RouterElement>> result, TypeElement key, RouterElement value) {
        if(result.containsKey(key)) {
            result.get(key).add(value);
        } else {
            Set<RouterElement> tempSet = new HashSet<>();
            tempSet.add(value);
            result.put(key, tempSet);
        }
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        LinkedHashSet<String> annotations = new LinkedHashSet<>();
        annotations.add(AutoRouter.class.getCanonicalName());
        annotations.add(RequestBoolean.class.getCanonicalName());
        annotations.add(RequestByte.class.getCanonicalName());
        annotations.add(RequestChar.class.getCanonicalName());
        annotations.add(RequestShort.class.getCanonicalName());
        annotations.add(RequestInt.class.getCanonicalName());
        annotations.add(RequestLong.class.getCanonicalName());
        annotations.add(RequestFloat.class.getCanonicalName());
        annotations.add(RequestDouble.class.getCanonicalName());
        annotations.add(RequestString.class.getCanonicalName());
        return annotations;
    }


    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
