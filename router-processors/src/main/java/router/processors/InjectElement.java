package router.processors;

import javax.lang.model.type.TypeMirror;

/**
 * Created by wanqi on 2017/9/11.
 *
 * @since 1.0.0
 */

public class InjectElement {

    private String mFieldName;
    private Class<?> mAnnotation;
    private String key;
    private Object defValue;
    private TypeMirror mType;

    public TypeMirror getType() {
        return mType;
    }

    public void setType(TypeMirror type) {
        mType = type;
    }


    public String getFieldName() {
        return mFieldName;
    }

    public void setFieldName(String fieldName) {
        mFieldName = fieldName;
    }

    public InjectElement(String fieldName,TypeMirror type, String key, Object defValue) {
        mFieldName =fieldName;
        mType = type;
        this.key = key;
        this.defValue = defValue;
    }

    public Class<?> getAnnotation() {
        return mAnnotation;
    }

    public void setAnnotation(Class<?> annotation) {
        mAnnotation = annotation;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Object getDefValue() {
        return defValue;
    }

    public void setDefValue(Object defValue) {
        this.defValue = defValue;
    }
}
