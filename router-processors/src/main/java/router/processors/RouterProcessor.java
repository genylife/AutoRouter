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
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;

import router.AutoExtra;
import router.AutoRouter;
import router.RouterClass;
import router.RouterKey;
import router.RouterType;

@AutoService(Processor.class)
public class RouterProcessor extends AbstractProcessor {

    private TypeSpec.Builder routerServiceClassBuilder;
    private Filer mFiler;
    private Messager mMessager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mFiler = processingEnv.getFiler();
        mMessager = processingEnv.getMessager();
        routerServiceClassBuilder = TypeSpec.interfaceBuilder("RouterService")
                .addModifiers(Modifier.PUBLIC);
    }


    private TypeName convert(RouterType type) {
        switch (type) {
            case CHAR:
                return TypeName.CHAR;
            case BOOLEAN:
                return TypeName.BOOLEAN;
            case BYTE:
                return TypeName.BYTE;
            case SHORT:
                return TypeName.SHORT;
            case INT:
                return TypeName.INT;
            case LONG:
                return TypeName.LONG;
            case FLOAT:
                return TypeName.FLOAT;
            case DOUBLE:
                return TypeName.DOUBLE;
            case STRING:
            default:
                return ClassName.get(String.class);
        }
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Map<TypeElement, List<RouterElement>> routerMap = scanAutoRouter(roundEnv);
        genMethods(routerMap);
        
        Map<TypeElement, List<ExtraElement>> extraMap = scanAutoExtra(roundEnv, routerMap);
        if(extraMap==null) return true;

        genExtraClass(extraMap);

        JavaFile.Builder builder = JavaFile.builder("router", routerServiceClassBuilder.build());
        JavaFile javaFile = builder.build();
        try {
            javaFile.writeTo(mFiler);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }


    private Object genDefValue(TypeMirror type) {
        switch (type.getKind()) {
            case CHAR:
                return "(char)" + 0;
            case BOOLEAN:
                return false;
            case BYTE:
                return "(byte)" + Byte.MIN_VALUE;
            case INT:
            case LONG:
                return Byte.MIN_VALUE;
            case SHORT:
                return "(short)" + Byte.MIN_VALUE;
            case FLOAT:
            case DOUBLE:
                return -1;
            case DECLARED:
            default:
                return "";
        }
    }

    private void genExtraClass(Map<TypeElement, List<ExtraElement>> extraMap) {
        for (TypeElement typeElement : extraMap.keySet()) {
            String packageName = typeElement.getEnclosingElement().toString();
            MethodSpec constructorMethodSpec = MethodSpec.constructorBuilder()
                    .addParameter(ClassName.get(packageName, typeElement.getSimpleName().toString()), "activity")
                    .addStatement("mActivity = activity")
                    .addStatement("inject()")
                    .build();
            MethodSpec.Builder injectMethodBuilder = MethodSpec.methodBuilder("inject")
                    .addModifiers(Modifier.PRIVATE);
            List<ExtraElement> extraElementList = extraMap.get(typeElement);
            for (ExtraElement extraElement : extraElementList) {
                TypeMirror type = extraElement.getType();
                TypeKind typeKind = type.getKind();
                String methodName = typeKind.name().toLowerCase();
                String sub1 = methodName.substring(1, methodName.length());
                String sub0 = methodName.substring(0, 1).toUpperCase();
                methodName = sub0 + sub1;


                if(typeKind == TypeKind.DECLARED) {
                    injectMethodBuilder.addStatement("mActivity.$L = mActivity.getIntent().get$LExtra(\"$L\")",
                            extraElement.getFieldName(), "String", extraElement.getValue());
                } else if(typeKind == TypeKind.BYTE || typeKind == TypeKind.SHORT || typeKind == TypeKind.CHAR) {
                    injectMethodBuilder.addStatement("mActivity.$L = mActivity.getIntent().get$LExtra(\"$L\", $L)",
                            extraElement.getFieldName(), methodName, extraElement.getValue(), genDefValue(type));
                } else {
                    injectMethodBuilder.addStatement("mActivity.$L = mActivity.getIntent().get$LExtra(\"$L\", $L)",
                            extraElement.getFieldName(), methodName, extraElement.getValue(), genDefValue(type));
                }
            }
            TypeSpec.Builder typeSpecBuilder = TypeSpec.classBuilder(typeElement.getSimpleName() + "_RouterInject")
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .addMethod(injectMethodBuilder.build())
                    .addField(ClassName.get(packageName, typeElement.getSimpleName().toString()), "mActivity")
                    .addMethod(constructorMethodSpec);
            JavaFile javaFile = JavaFile.builder(packageName, typeSpecBuilder.build())
                    .build();
            try {
                javaFile.writeTo(mFiler);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void genMethods(Map<TypeElement, List<RouterElement>> routerMap) {
        for (TypeElement typeElement : routerMap.keySet()) {
            AnnotationSpec methodAnnotationSpec = AnnotationSpec.builder(RouterClass.class)
                    .addMember("value", "\"" + typeElement.getQualifiedName().toString() + "\"")
                    .build();
            TypeName returnType = ClassName.get("router", "IntentWrapper");
            String methodName = typeElement.getSimpleName().toString();
            methodName = methodName.substring(0, 1).toLowerCase() + methodName.substring(1, methodName.length());
            MethodSpec.Builder methodBuild = MethodSpec.methodBuilder(methodName)
                    .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                    .addAnnotation(methodAnnotationSpec)
                    .returns(returnType);
            List<RouterElement> routerElementList = routerMap.get(typeElement);
            for (RouterElement routerElement : routerElementList) {
                String value = routerElement.getValue();
                RouterType type = routerElement.getType();
                AnnotationSpec annotationSpec = AnnotationSpec.builder(RouterKey.class)
                        .addMember("value", "\"" + value + "\"")
                        .build();
                String paramName = value.substring(0, 1).toUpperCase() + value.substring(1, value.length());
                ParameterSpec parameterSpec = ParameterSpec.builder(convert(type), "r" + paramName)
                        .addAnnotation(annotationSpec)
                        .build();
                methodBuild.addParameter(parameterSpec);
            }
            routerServiceClassBuilder.addMethod(methodBuild.build());
        }

    }

    private Map<TypeElement, List<RouterElement>> scanAutoRouter(RoundEnvironment roundEnv) {
        Map<TypeElement, List<RouterElement>> routerMap = new HashMap<>();
        for (Element element : roundEnv.getElementsAnnotatedWith(AutoRouter.class)) {
            if(element.getKind() == ElementKind.CLASS) {
                TypeElement typeElement = (TypeElement) element;
                System.out.println(typeElement.getSimpleName());
                String[] value = typeElement.getAnnotation(AutoRouter.class).value();
                RouterType[] type = typeElement.getAnnotation(AutoRouter.class).type();
                // TODO: 2017/9/22 check value and type 
                List<RouterElement> routerElementList = new ArrayList<>(value.length);
                for (int i = 0; i < value.length; ++i) {
                    RouterElement routerElement = new RouterElement(value[i], type[i]);
                    routerElementList.add(routerElement);
                }
                routerMap.put(typeElement, routerElementList);
            }
        }
        return routerMap;
    }

    private Map<TypeElement, List<ExtraElement>> scanAutoExtra(RoundEnvironment roundEnv, Map<TypeElement,
            List<RouterElement>> routerMap) {
        Map<TypeElement, List<ExtraElement>> extraMap = new HashMap<>();
        for (Element element : roundEnv.getElementsAnnotatedWith(AutoExtra.class)) {
            if(element.getKind() == ElementKind.FIELD) {
                VariableElement fieldElement = (VariableElement) element;
                String fieldName = fieldElement.getSimpleName().toString();
                TypeElement typeElement = (TypeElement) fieldElement.getEnclosingElement();
                String value = fieldElement.getAnnotation(AutoExtra.class).value();
                TypeMirror typeMirror = fieldElement.asType();

                if(value.equals("")) {
                    List<RouterElement> routerElementList = routerMap.get(typeElement);
                    List<RouterElement> sameType = new ArrayList<>();
                    for (RouterElement routerElement : routerElementList) {
                        if(typeMirror.getKind().ordinal() == routerElement.getType().ordinal() ||
                                (typeMirror.getKind().ordinal() == TypeKind.DECLARED.ordinal()
                                        && routerElement.getType().ordinal() == RouterType.STRING.ordinal())) {
                            sameType.add(routerElement);
                        }
                    }
                    if(sameType.size() == 0) {
                        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "can not find any key to inject this " +
                                "field! please see the field '" + fieldName + "'", fieldElement);
                        return null;
                    } else {
                        if(sameType.size() > 1) {
                            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "find one more key to inject this " +
                                    "field,but cannot to select what " + "one!please see the field '" + fieldName + "'", 
                                    fieldElement);
                            return null;
                        } else {
                            value = sameType.get(0).getValue();
                        }
                    }
                }

                ExtraElement extraElement = new ExtraElement(value, fieldName, typeMirror);
                List<ExtraElement> routerElementList = extraMap.get(typeElement);
                if(routerElementList == null) {
                    List<ExtraElement> extraElementList = new ArrayList<>();
                    extraElementList.add(extraElement);
                    extraMap.put(typeElement, extraElementList);
                } else {
                    List<ExtraElement> extraElementList = extraMap.get(typeElement);
                    extraElementList.add(extraElement);
                }
            }
        }
        return extraMap;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        LinkedHashSet<String> annotations = new LinkedHashSet<>();
        annotations.add(AutoRouter.class.getCanonicalName());
        annotations.add(AutoExtra.class.getCanonicalName());
        return annotations;
    }


    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
