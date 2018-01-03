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
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;

import router.annotation.AutoExtra;
import router.annotation.AutoRouter;
import router.annotation.RouterClass;
import router.annotation.RouterKey;

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

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Map<TypeElement, RouterElement> routerMap = scanAutoRouter(roundEnv);
        boolean bool = scanAutoExtra(roundEnv, routerMap);
        if(!bool) return false;
        genMethods(routerMap);

        if(routerMap == null) return false;

        genExtraClass(routerMap);

        JavaFile.Builder builder = JavaFile.builder("router", routerServiceClassBuilder.build());
        JavaFile javaFile = builder.build();

        MethodSpec routerMethod1 = MethodSpec.methodBuilder("inject")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(ClassName.get("android.content", "Context"), "context")
                .addStatement("_Router.init(context).inject()")
                .build();
        MethodSpec routerMethod2 = MethodSpec.methodBuilder("injectWithCreate")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(ClassName.get("android.content", "Context"), "context")
                .returns(ClassName.get("router", "RouterService"))
                .addStatement("_Router r = _Router.init(context)")
                .addStatement("r.inject()")
                .addStatement("return r.create(RouterService.class)")
                .build();
        MethodSpec routerMethod3 = MethodSpec.methodBuilder("create")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(ClassName.get("android.content", "Context"), "context")
                .returns(ClassName.get("router", "RouterService"))
                .addStatement("_Router r = _Router.init(context)")
                .addStatement("return r.create(RouterService.class)")
                .build();
        MethodSpec routerPrivateConstructor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PRIVATE)
                .build();
        TypeSpec routerClass = TypeSpec.classBuilder("Router")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(routerMethod1)
                .addMethod(routerMethod2)
                .addMethod(routerMethod3)
                .addMethod(routerPrivateConstructor)
                .build();
        JavaFile routerFile = JavaFile.builder("router", routerClass).build();
        try {
            javaFile.writeTo(mFiler);
            routerFile.writeTo(mFiler);
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

    private void genExtraClass(Map<TypeElement, RouterElement> routerMap) {
        for (TypeElement typeElement : routerMap.keySet()) {
            String packageName = typeElement.getEnclosingElement().toString();
            MethodSpec constructorMethodSpec = MethodSpec.constructorBuilder()
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(ClassName.get(packageName, typeElement.getSimpleName().toString()), "activity")
                    .addStatement("mActivity = activity")
                    .addStatement("inject()")
                    .build();
            MethodSpec.Builder injectMethodBuilder = MethodSpec.methodBuilder("inject")
                    .addModifiers(Modifier.PRIVATE)
                    .addStatement("$T intent = mActivity.getIntent()",
                            ClassName.get("android.content", "Intent"));
            RouterElement routerElement = routerMap.get(typeElement);
            List<ExtraElement> extraElementList = routerElement.getExtraElement();
            if(extraElementList != null) {
                for (ExtraElement extraElement : extraElementList) {
                    TypeMirror type = extraElement.getType();
                    TypeKind typeKind = type.getKind();
                    String typeKindName = typeKind.name().toLowerCase();

                    if(type instanceof PrimitiveType) {
                        injectMethodBuilder.addStatement("mActivity.$L = intent.get$LExtra($S, $L)",
                                extraElement.getFieldName(), genMethodName(typeKindName), extraElement.getValue(), genDefValue
                                        (type));
                    }
                    if(type instanceof DeclaredType) {
                        if(type.toString().equals(String.class.getCanonicalName())) {
                            injectMethodBuilder.addStatement("mActivity.$L = intent.getStringExtra($S)",
                                    extraElement.getFieldName(), extraElement.getValue());
                        } else {
                            List<? extends TypeMirror> interfaces = ((TypeElement) ((DeclaredType) type).asElement())
                                    .getInterfaces();
                            boolean isParcel = false;
                            for (TypeMirror mirror : interfaces) {
                                if(mirror.toString().equals("android.os.Parcelable")) {
                                    isParcel = true;
                                    injectMethodBuilder.addStatement("mActivity.$L = intent.getParcelableExtra($S)",
                                            extraElement.getFieldName(), extraElement.getValue());
                                }
                            }
                            if(!isParcel) {
                                mMessager.printMessage(Diagnostic.Kind.ERROR, type.toString() + " should implement the " +
                                        "Parcelable", typeElement);
                            }
                        }
                    }
                    if(type instanceof ArrayType) {
                        String methodName = "";
                        boolean isParcelArray = false;
                        TypeMirror componentType = ((ArrayType) type).getComponentType();
                        if(componentType instanceof DeclaredType) {
                            if(componentType.toString().equals(String.class.getCanonicalName())) {
                                methodName = "String";
                            } else {
                                methodName = "Parcelable";
                                isParcelArray = true;
                            }
                        } else if(componentType instanceof PrimitiveType) {
                            methodName = genMethodName(componentType.toString());
                        } else {
                            mMessager.printMessage(Diagnostic.Kind.ERROR, "unknown type [" + componentType.toString() + "]",
                                    typeElement);
                        }
                        if(isParcelArray) {
                            injectMethodBuilder.addStatement("mActivity.$L = ($T)intent.getParcelableArrayExtra($S)",
                                    extraElement.getFieldName(), type,extraElement.getValue());
                        } else {
                            injectMethodBuilder.addStatement("mActivity.$L = intent.get$LArrayExtra($S)",
                                    extraElement.getFieldName(), methodName, extraElement.getValue());
                        }
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
    }

    private String genMethodName(String name) {
        char[] cs = name.toCharArray();
        cs[0] -= 32;
        return String.valueOf(cs);
    }

    private void genMethods(Map<TypeElement, RouterElement> routerMap) {
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
            RouterElement routerElement = routerMap.get(typeElement);
            List<ExtraElement> extraElements = routerElement.getExtraElement();
            if(extraElements != null) {
                for (ExtraElement element : extraElements) {
                    String value = element.getValue();
                    TypeMirror type = element.getType();
                    AnnotationSpec annotationSpec = AnnotationSpec.builder(RouterKey.class)
                            .addMember("value", "\"" + value + "\"")
                            .build();
                    String paramName = value.substring(0, 1).toUpperCase() + value.substring(1, value.length());
                    ParameterSpec parameterSpec = ParameterSpec.builder(ClassName.get(type), "r" + paramName)
                            .addAnnotation(annotationSpec)
                            .build();
                    methodBuild.addParameter(parameterSpec);
                }
            }
            routerServiceClassBuilder.addMethod(methodBuild.build());
        }

    }

    private Map<TypeElement, RouterElement> scanAutoRouter(RoundEnvironment roundEnv) {
        Map<TypeElement, RouterElement> routerMap = new HashMap<>();
        for (Element element : roundEnv.getElementsAnnotatedWith(AutoRouter.class)) {
            if(element.getKind() == ElementKind.CLASS) {
                TypeElement typeElement = (TypeElement) element;
                System.out.println(typeElement.getSimpleName());
                String value = typeElement.getAnnotation(AutoRouter.class).value();
                // TODO: 2017/9/22 check value and type 
                RouterElement routerElement = new RouterElement(value);
                routerMap.put(typeElement, routerElement);
            }
        }
        return routerMap;
    }

    private boolean scanAutoExtra(RoundEnvironment roundEnv, Map<TypeElement, RouterElement> routerMap) {
        for (Element element : roundEnv.getElementsAnnotatedWith(AutoExtra.class)) {
            if(element.getKind() == ElementKind.FIELD) {
                VariableElement fieldElement = (VariableElement) element;
                String fieldName = fieldElement.getSimpleName().toString();
                TypeElement typeElement = (TypeElement) fieldElement.getEnclosingElement();
                if(!routerMap.containsKey(typeElement)) {
                    mMessager.printMessage(Diagnostic.Kind.ERROR, "please check "
                            + typeElement.getSimpleName() + "is with annotation 'AutoRouter' !", typeElement);
                    return false;
                }
                String value = fieldElement.getAnnotation(AutoExtra.class).value();
                TypeMirror typeMirror = fieldElement.asType();

                if(value.equals("")) {
                    mMessager.printMessage(Diagnostic.Kind.ERROR, "AutoExtra annotation can not be ''!", typeElement);
                    return false;
                }

                RouterElement routerElement = routerMap.get(typeElement);
                List<ExtraElement> extraElements = routerElement.getExtraElement();
                if(extraElements == null) {
                    extraElements = new ArrayList<>();
                }
                ExtraElement extraElement = new ExtraElement(value, fieldName, typeMirror);
                extraElements.add(extraElement);
                routerElement.setExtraElement(extraElements);
            }
        }
        return true;
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
