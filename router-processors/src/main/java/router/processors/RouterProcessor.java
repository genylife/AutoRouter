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

        MethodSpec routerInitMethod = MethodSpec.methodBuilder("init")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(ClassName.get("android.app", "Activity"), "activity")
                .returns(ClassName.get("router", "RouterService"))
                .addStatement("return _Router.init(activity).create(RouterService.class)")
                .build();
        MethodSpec routerInit2Method = MethodSpec.methodBuilder("init")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(ClassName.get("router", "Converter"), "converter")
                .addParameter(ClassName.get("router", "Parser"), "parser")
                .addStatement("_Router.setConverter(converter)")
                .addStatement("_Router.setParser(parser)")
                .build();
        MethodSpec routerPrivateConstructor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PRIVATE)
                .build();
        TypeSpec routerClass = TypeSpec.classBuilder("Router")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(routerInitMethod)
                .addMethod(routerInit2Method)
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
                    .addParameter(ClassName.get("router", "Parser"), "parser")
                    .addStatement("mActivity = activity")
                    .addStatement("mParser = parser")
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
                    String methodName = typeKind.name().toLowerCase();
                    String sub1 = methodName.substring(1, methodName.length());
                    String sub0 = methodName.substring(0, 1).toUpperCase();
                    methodName = sub0 + sub1;

                    if(typeKind == TypeKind.DECLARED) {
                        if(type.toString().equals("java.lang.String")) {
                            injectMethodBuilder.addStatement("mActivity.$L = intent.getStringExtra($S)",
                                    extraElement.getFieldName(), extraElement.getValue());
                        } else {
                            injectMethodBuilder.addStatement("$T str = intent.getStringExtra($S)",
                                    String.class, extraElement.getValue())
                                    .addStatement("mActivity.$L = mParser.parse(str,$T.class)",
                                            extraElement.getFieldName(), TypeName.get(type));
                        }
                    } else if(typeKind == TypeKind.BYTE || typeKind == TypeKind.SHORT || typeKind == TypeKind.CHAR) {
                        injectMethodBuilder.addStatement("mActivity.$L = intent.get$LExtra($S, $L)",
                                extraElement.getFieldName(), methodName, extraElement.getValue(), genDefValue(type));
                    } else {
                        injectMethodBuilder.addStatement("mActivity.$L = intent.get$LExtra($S, $L)",
                                extraElement.getFieldName(), methodName, extraElement.getValue(), genDefValue(type));
                    }
                }

                TypeSpec.Builder typeSpecBuilder = TypeSpec.classBuilder(typeElement.getSimpleName() + "_RouterInject")
                        .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                        .addMethod(injectMethodBuilder.build())
                        .addField(ClassName.get(packageName, typeElement.getSimpleName().toString()), "mActivity")
                        .addField(ClassName.get("router", "Parser"), "mParser")
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
