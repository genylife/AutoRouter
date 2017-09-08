package router.processors;

import java.lang.annotation.Annotation;

/**
 * Created by DingZhu on 2017/9/7.
 *
 * @since 1.0.0
 */

public class RouterElement {

    private Class<? extends Annotation> mAnnotation;
    private String[] mValue;

    public RouterElement(Class<? extends Annotation> annotation, String[] value) {
        mAnnotation = annotation;
        mValue = value;
    }

    /*@Override
    public boolean equals(Object obj) {
        if(obj instanceof RouterElement) {
            RouterElement element = (RouterElement) obj;
            return mAnnotation.getCanonicalName().equals(element.mAnnotation.getCanonicalName())
                    && (Arrays.equals(mValue, element.mValue));
        } else {
            return false;
        }
    }*/

    public Class<? extends Annotation> getAnnotation() {
        return mAnnotation;
    }

    public void setAnnotation(Class<? extends Annotation> annotation) {
        mAnnotation = annotation;
    }

    public String[] getValue() {
        return mValue;
    }

    public void setValue(String[] value) {
        mValue = value;
    }
}
