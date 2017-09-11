package router.processors;

import com.google.auto.service.AutoService;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;

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

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Map<TypeElement, Set<RouterElement>> typeElementSetMap = scanAllElements(roundEnv);
        return false;
    }

    private Map<TypeElement, Set<RouterElement>> scanAllElements(RoundEnvironment roundEnv) {
        Map<TypeElement, Set<RouterElement>> result = new HashMap<>();
        //  0
//        for (Element element : roundEnv.getElementsAnnotatedWith(InjectBooleanExtra.class)) {
//            if(element.getKind() == ElementKind.FIELD) {
//                TypeElement typeElement = (TypeElement) element;
//                RouterElement routerElement = new RouterElement(AutoRouter.class,
//                        new String[]{});
//                intoMap(result, typeElement, routerElement);
//            }
//        }
        return result;
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
