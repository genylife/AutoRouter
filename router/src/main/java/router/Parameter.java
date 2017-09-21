package router;

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

    void setParamType(int paramType) {
        mParamType = paramType;
    }

    static class ParameterType {

        static final int TYPE_BOOLEAN = 1;
        static final int TYPE_BYTE = 2;
        static final int TYPE_CHAR = 3;
        static final int TYPE_SHORT = 4;
        static final int TYPE_INT = 5;
        static final int TYPE_LONG = 6;
        static final int TYPE_FLOAT = 7;
        static final int TYPE_DOUBLE = 8;
        static final int TYPE_STRING = 9;
    }
}
