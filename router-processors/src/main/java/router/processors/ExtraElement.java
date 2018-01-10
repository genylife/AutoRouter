package router.processors;

import javax.lang.model.type.TypeMirror;

/**
 * Created by DingZhu on 2017/9/22.
 *
 * @since 1.0.0
 */

final class ExtraElement {

    private String value;
    private String fieldName;
    private TypeMirror type;

    private boolean optional;

    ExtraElement(String value, String fieldName, TypeMirror type, boolean optional) {
        this.value = value;
        this.fieldName = fieldName;
        this.type = type;
        this.optional = optional;
    }

    String getValue() {
        return value;
    }

    String getFieldName() {
        return fieldName;
    }

    TypeMirror getType() {
        return type;
    }

    boolean isOptional() {
        return optional;
    }
}
