package router.processors;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
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
import javax.lang.model.element.VariableElement;

import router.RouterInjector;
import router.injector.InjectBooleanExtra;
import router.injector.InjectByteExtra;
import router.injector.InjectCharExtra;
import router.injector.InjectDoubleExtra;
import router.injector.InjectFloatExtra;
import router.injector.InjectIntExtra;
import router.injector.InjectLongExtra;
import router.injector.InjectShortExtra;
import router.injector.InjectStringExtra;

@AutoService(Processor.class)
public class InjectProcessor extends AbstractProcessor {

    private Filer mFiler;

    @Override public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mFiler = processingEnv.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Map<TypeElement, Set<InjectElement>> typeElementSetMap = scanAllElements(roundEnv);
        for (Map.Entry<TypeElement, Set<InjectElement>> entry : typeElementSetMap.entrySet()) {
            TypeElement key = entry.getKey();
            Set<InjectElement> value = entry.getValue();
            String packageName = key.getEnclosingElement().toString();
            MethodSpec constructorMethodSpec = MethodSpec.constructorBuilder()
                    .addParameter(ClassName.get(packageName, key.getSimpleName().toString()), "activity")
                    .addModifiers(Modifier.PUBLIC)
                    .addStatement("mActivity = activity")
                    .build();
            MethodSpec.Builder injectMethodBuilder = MethodSpec.methodBuilder("inject")
                    .addAnnotation(Override.class)
                    .addModifiers(Modifier.PUBLIC);
            for (InjectElement e : value) {
                injectMethodBuilder.addStatement("mActivity.$L = mActivity.getIntent().getIntExtra(\"$L\",$L)",
                        e.getFieldName(), e.getKey(), e.getDefValue());
            }
            TypeSpec.Builder typeSpecBuilder = TypeSpec.classBuilder(key.getSimpleName() + "_RouterInject")
                    .addModifiers(Modifier.PUBLIC)
                    .addSuperinterface(RouterInjector.class)
                    .addMethod(injectMethodBuilder.build())
                    .addField(ClassName.get(packageName, key.getSimpleName().toString()), "mActivity")
                    .addMethod(constructorMethodSpec);
            JavaFile javaFile = JavaFile.builder(packageName, typeSpecBuilder.build())
                    .build();
            try {
                javaFile.writeTo(mFiler);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return false;
    }

    private Map<TypeElement, Set<InjectElement>> scanAllElements(RoundEnvironment roundEnv) {
        Map<TypeElement, Set<InjectElement>> result = new HashMap<>();
        //          0
        for (Element element : roundEnv.getElementsAnnotatedWith(InjectIntExtra.class)) {
            if(element.getKind() == ElementKind.FIELD) {
                VariableElement fieldElement = (VariableElement) element;
                String fieldName = fieldElement.getSimpleName().toString();
                TypeElement typeElement = (TypeElement) fieldElement.getEnclosingElement();
                String key = fieldElement.getAnnotation(InjectIntExtra.class).key();
                int defaultValue = fieldElement.getAnnotation(InjectIntExtra.class).defaultValue();
                InjectElement injectElement = new InjectElement(fieldName, InjectIntExtra.class, key, defaultValue);
                intoMap(result, typeElement, injectElement);
            }
        }
        return result;
    }

    private void intoMap(Map<TypeElement, Set<InjectElement>> result, TypeElement key, InjectElement value) {
        if(result.containsKey(key)) {
            result.get(key).add(value);
        } else {
            Set<InjectElement> tempSet = new HashSet<>();
            tempSet.add(value);
            result.put(key, tempSet);
        }
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        LinkedHashSet<String> annotations = new LinkedHashSet<>();
        annotations.add(InjectByteExtra.class.getCanonicalName());
        annotations.add(InjectBooleanExtra.class.getCanonicalName());
        annotations.add(InjectIntExtra.class.getCanonicalName());
        annotations.add(InjectCharExtra.class.getCanonicalName());
        annotations.add(InjectDoubleExtra.class.getCanonicalName());
        annotations.add(InjectFloatExtra.class.getCanonicalName());
        annotations.add(InjectLongExtra.class.getCanonicalName());
        annotations.add(InjectShortExtra.class.getCanonicalName());
        annotations.add(InjectStringExtra.class.getCanonicalName());
        return annotations;
    }


    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
