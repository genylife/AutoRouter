package router.processors;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;

import router.annotation.RouterClass;
import router.annotation.RouterKey;

class ClassFileGenerator {
    private Messager mMessager;

    ClassFileGenerator(Messager messager) {
        mMessager = messager;
    }

    JavaFile apiClassFile() {
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
        return JavaFile.builder("router", routerClass).build();
    }

    JavaFile routerTable(Map<TypeElement, RouterElement> routerMap) {
        TypeSpec.Builder routerServiceClassBuilder = TypeSpec.interfaceBuilder("RouterService").addModifiers(Modifier.PUBLIC);
        TypeName returnType = ClassName.get("router", "IntentWrapper");
        for (TypeElement typeElement : routerMap.keySet()) {
            AnnotationSpec methodAnnotationSpec = AnnotationSpec.builder(RouterClass.class)
                    .addMember("value", "\"" + typeElement.getQualifiedName().toString() + "\"")
                    .build();
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

        //name method
        MethodSpec.Builder nameMethodBuilder = MethodSpec.methodBuilder("name")
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .addParameter(ParameterSpec.builder(String.class, "name").build())
                .returns(returnType);
        routerServiceClassBuilder.addMethod(nameMethodBuilder.build());

        JavaFile.Builder builder = JavaFile.builder("router", routerServiceClassBuilder.build());
        return builder.build();
    }


    private JavaFile singleActivityInjectClassFile(TypeElement typeElement, RouterElement routerElement) {
        //get activity's packageName
        String packageName = typeElement.getEnclosingElement().toString();
        //constructor Method
        MethodSpec constructorMethodSpec = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ClassName.get(packageName, typeElement.getSimpleName().toString()), "activity")
                .addStatement("mActivity = activity")
                .addStatement("inject()")
                .build();
        //inject Method
        MethodSpec.Builder injectMethodBuilder = MethodSpec.methodBuilder("inject")
                .addModifiers(Modifier.PRIVATE)
                .addStatement("$T intent = mActivity.getIntent()",
                        ClassName.get("android.content", "Intent"));

        //get all of injectField
        List<ExtraElement> extraElementList = routerElement.getExtraElement();
        if(extraElementList != null) {
            for (ExtraElement extraElement : extraElementList) {
                //get field type
                TypeMirror type = extraElement.getType();
                TypeKind typeKind = type.getKind();
                String typeKindName = typeKind.name().toLowerCase();

                //PrimitiveType：boolean,char,byte,short,int,long,float,double
                if(type instanceof PrimitiveType) {
                    injectMethodBuilder.addStatement("mActivity.$L = intent.get$LExtra($S, $L)",
                            extraElement.getFieldName(), genMethodName(typeKindName), extraElement.getValue(), genDefValue
                                    (type));
                }
                //DeclaredType：String,Parcelable
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
                                break;
                            }
                        }
                        if(!isParcel) {
                            mMessager.printMessage(Diagnostic.Kind.ERROR, type.toString() + " should implement the " +
                                    "Parcelable", typeElement);
                        }
                    }
                }
                //ArrayType: Array of PrimitiveType, Array of String, Array of Parcelable
                if(type instanceof ArrayType) {
                    String methodName = "";
                    boolean isParcelArray = false;
                    //Array's componentType
                    TypeMirror componentType = ((ArrayType) type).getComponentType();

                    //String,Parcelable
                    if(componentType instanceof DeclaredType) {
                        if(componentType.toString().equals(String.class.getCanonicalName())) {
                            methodName = "String";
                        } else {
                            methodName = "Parcelable";
                            isParcelArray = true;
                        }
                    } else if(componentType instanceof PrimitiveType) {//PrimitiveType
                        methodName = genMethodName(componentType.toString());
                    } else {
                        mMessager.printMessage(Diagnostic.Kind.ERROR, "unknown type [" + componentType.toString() + "]",
                                typeElement);
                    }
                    if(isParcelArray) {
                        injectMethodBuilder.addStatement("$T[] parcels = intent.getParcelableArrayExtra($S)",
                                ClassName.get("android.os", "Parcelable"), extraElement.getValue())
                                .addStatement("int length = parcels.length")
                                .addStatement("mActivity.$L = new $T[length]", extraElement.getFieldName(),
                                        ((ArrayType) type).getComponentType())
                                .beginControlFlow("for(int i = 0; i < length; i++)")
                                .addStatement("mActivity.$L[i] = ($T)parcels[i]", extraElement.getFieldName(),
                                        ((ArrayType) type).getComponentType())
                                .endControlFlow();
                    } else {
                        injectMethodBuilder.addStatement("mActivity.$L = intent.get$LArrayExtra($S)",
                                extraElement.getFieldName(), methodName, extraElement.getValue());
                    }
                }
            }

            //builder inject class
            TypeSpec.Builder typeSpecBuilder = TypeSpec.classBuilder(typeElement.getSimpleName() + "_RouterInject")
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .addMethod(injectMethodBuilder.build())
                    .addField(ClassName.get(packageName, typeElement.getSimpleName().toString()), "mActivity")
                    .addMethod(constructorMethodSpec);
            //generator java file
            return JavaFile.builder(packageName, typeSpecBuilder.build()).build();
        } else {//not need inject
            return null;
        }
    }

    List<JavaFile> allActivityInjectClassFiles(Map<TypeElement, RouterElement> routerMap) {
        ArrayList<JavaFile> files = new ArrayList<>(routerMap.size());
        for (TypeElement typeElement : routerMap.keySet()) {
            JavaFile javaFile = singleActivityInjectClassFile(typeElement, routerMap.get(typeElement));
            files.add(javaFile);
        }
        return files;
    }

    /**
     * Intent default value
     *
     * @param type parameter type
     * @return default value
     */
    private String genDefValue(TypeMirror type) {
        switch (type.getKind()) {
            case CHAR:
                return "Character.MIN_VALUE";
            case BOOLEAN:
                return "false";
            case BYTE:
                return "Byte.MIN_VALUE";
            case INT:
                return "Integer.MIN_VALUE";
            case LONG:
                return "Long.MIN_VALUE";
            case SHORT:
                return "Short.MIN_VALUE";
            case FLOAT:
                return "Float.MIN_VALUE";
            case DOUBLE:
                return "Double.MIN_VALUE";
            default:
                return "";
        }
    }

    /**
     * first char to upperCase
     */
    private String genMethodName(String name) {
        char[] cs = name.toCharArray();
        cs[0] -= 32;
        return String.valueOf(cs);
    }

}
