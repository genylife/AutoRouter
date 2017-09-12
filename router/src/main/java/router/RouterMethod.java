package router;

public class RouterMethod {

    private Class<?> mToActivity;
    private Parameter[] mParameters;

    public RouterMethod(int paramsCount) {
        mParameters = new Parameter[paramsCount];
    }

    public Class<?> getToActivity() {
        return mToActivity;
    }

    public void setToActivity(Class<?> toActivity) {
        mToActivity = toActivity;
    }

    public Parameter[] getParameters() {
        return mParameters;
    }

    public void setParameters(Parameter[] parameters) {
        mParameters = parameters;
    }
}
