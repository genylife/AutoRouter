package router;

public class Parameter {

    private String mExtraKey;
    private int mParamType;

    public Parameter(String extraKey) {
        mExtraKey = extraKey;
    }

    public String getExtraKey() {
        return mExtraKey;
    }

    public void setExtraKey(String extraKey) {
        mExtraKey = extraKey;
    }

    public int getParamType() {
        return mParamType;
    }

    public void setParamType(int paramType) {
        mParamType = paramType;
    }

    public static class ParameterType {

        static final int Type_Boolean = 1;
        static final int Type_Byte = 2;
        static final int Type_char = 3;
        static final int Type_short = 4;
        static final int Type_int = 5;
        static final int Type_long = 6;
        static final int Type_float = 7;
        static final int Type_double = 8;
        static final int Type_string = 9;
    }
}
