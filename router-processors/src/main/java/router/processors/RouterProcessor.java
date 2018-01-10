package router.processors;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;

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
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;

import router.annotation.AutoExtra;
import router.annotation.AutoRouter;

@AutoService(Processor.class)
public class RouterProcessor extends AbstractProcessor {

    private Filer mFiler;
    private Messager mMessager;

    private ClassFileGenerator mGenerator;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mFiler = processingEnv.getFiler();
        mMessager = processingEnv.getMessager();

        mGenerator = new ClassFileGenerator(mMessager);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Map<TypeElement, RouterElement> routerMap = scanAutoRouter(roundEnv);
        if(routerMap == null) return false;

        boolean bool = scanAutoExtra(roundEnv, routerMap);
        if(!bool) return false;

        JavaFile routerInterfaceFile = mGenerator.routerTable(routerMap);

        final List<JavaFile> injectClassFiles = mGenerator.allActivityInjectClassFiles(routerMap);

        JavaFile apiClassFile = mGenerator.apiClassFile();

        try {
            routerInterfaceFile.writeTo(mFiler);
            for (JavaFile file : injectClassFiles) {
                if(file != null) {
                    file.writeTo(mFiler);
                }
            }
            apiClassFile.writeTo(mFiler);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
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
                boolean optional = fieldElement.getAnnotation(AutoExtra.class).optional();
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
                ExtraElement extraElement = new ExtraElement(value, fieldName, typeMirror, optional);
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
