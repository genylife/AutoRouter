package router;

final class RouterMethod {

    private Class<?> mToActivity;
    private Parameter[] mParameters;

    RouterMethod(int paramsCount) {
        mParameters = new Parameter[paramsCount];
    }

    Class<?> getToActivity() {
        return mToActivity;
    }

    void setToActivity(Class<?> toActivity) {
        mToActivity = toActivity;
    }

    Parameter[] getParameters() {
        return mParameters;
    }

    void setParameters(Parameter[] parameters) {
        mParameters = parameters;
    }
}
