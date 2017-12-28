package router;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

final class Parameter {

    private String mExtraKey;
    private int mParamType;

    Parameter(String extraKey) {
        mExtraKey = extraKey;
    }

    String getExtraKey() {
        return mExtraKey;
    }

    public void setExtraKey(String extraKey) {
        mExtraKey = extraKey;
    }

    int getParamType() {
        return mParamType;
    }

    void setParamType(@ParameterType int paramType) {
        mParamType = paramType;
    }

    @IntDef({
            Type.TYPE_BOOLEAN,
            Type.TYPE_BYTE,
            Type.TYPE_CHAR,
            Type.TYPE_SHORT,
            Type.TYPE_INT,
            Type.TYPE_LONG,
            Type.TYPE_FLOAT,
            Type.TYPE_DOUBLE,
            Type.TYPE_STRING,
            Type.TYPE_OBJECT,
            Type.TYPE_PARCELABLE
    })
    @Retention(RetentionPolicy.SOURCE)
    @interface ParameterType {

    }

    static class Type {

        static final int TYPE_BOOLEAN = 1;
        static final int TYPE_BYTE = 2;
        static final int TYPE_CHAR = 3;
        static final int TYPE_SHORT = 4;
        static final int TYPE_INT = 5;
        static final int TYPE_LONG = 6;
        static final int TYPE_FLOAT = 7;
        static final int TYPE_DOUBLE = 8;
        static final int TYPE_STRING = 9;
        static final int TYPE_OBJECT = 10;
        static final int TYPE_PARCELABLE = 11;

        static final int ARRAY_BOOLEAN = 12;
        static final int ARRAY_BYTE = 13;
        static final int ARRAY_CHAR = 14;
        static final int ARRAY_SHORT = 15;
        static final int ARRAY_INT = 16;
        static final int ARRAY_LONG = 17;
        static final int ARRAY_FLOAT = 18;
        static final int ARRAY_DOUBLE = 19;
        static final int ARRAY_STRING = 20;
        static final int ARRAY_OBJECT = 21;
        static final int ARRAY_PARCELABLE = 22;
    }
}
