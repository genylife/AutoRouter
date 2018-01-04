package router;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import router.annotation.RouterClass;
import router.annotation.RouterKey;

class _Router {

    private final static Map<Method, RouterMethod> routerMethodCache = new ConcurrentHashMap<>();

    private Context mContext;

    private _Router(Context activity) {
        mContext = activity;
    }

    @SuppressWarnings("unchecked")
    <T> T create(Class<T> service) {
        return (T) Proxy.newProxyInstance(service.getClassLoader(), new Class[]{service},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
                        RouterMethod routerMethod = loadRouterMethod(method);
                        Intent intent = putExtra(routerMethod, objects);
                        return new IntentWrapper(mContext, intent);
                    }
                });
    }

    private RouterMethod loadRouterMethod(Method method) {
        RouterMethod result = routerMethodCache.get(method);
        if(result != null) return result;

        synchronized (routerMethodCache) {
            result = routerMethodCache.get(method);
            if(result == null) {
                result = handleRouterMethod(method);
                routerMethodCache.put(method, result);
            }
        }
        return result;
    }

    private RouterMethod handleRouterMethod(Method method) {
        Annotation[][] parameterAnnotationsArray = method.getParameterAnnotations();

        RouterMethod routerMethod = new RouterMethod(parameterAnnotationsArray.length);

        String toActivityClass = method.getAnnotation(RouterClass.class).value();
        Class<?> toActivity;
        try {
            toActivity = Class.forName(toActivityClass);
            routerMethod.setToActivity(toActivity);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < parameterAnnotationsArray.length; i++) {
            Annotation[] annotations = parameterAnnotationsArray[i];
            String value = ((RouterKey) annotations[0]).value();
            Parameter parameter = new Parameter(value);
            routerMethod.getParameters()[i] = parameter;
        }

        Type[] parameterTypes = method.getGenericParameterTypes();
        for (int i = 0; i < parameterTypes.length; i++) {
            Parameter parameter = routerMethod.getParameters()[i];
            Type type = parameterTypes[i];
            if(type instanceof Class) {
                Class clazz = ((Class) type);
                if(clazz.isArray()) {
                    Class component = clazz.getComponentType();
                    if(component == String.class) {
                        parameter.setParamType(Parameter.Type.ARRAY_STRING);
                        continue;
                    }
                    String componentName = component.toString().toLowerCase();
                    switch (componentName) {
                        case "boolean":
                            parameter.setParamType(Parameter.Type.ARRAY_BOOLEAN);
                            break;
                        case "byte":
                            parameter.setParamType(Parameter.Type.ARRAY_BYTE);
                            break;
                        case "char":
                            parameter.setParamType(Parameter.Type.ARRAY_CHAR);
                            break;
                        case "int":
                            parameter.setParamType(Parameter.Type.ARRAY_INT);
                            break;
                        case "short":
                            parameter.setParamType(Parameter.Type.ARRAY_SHORT);
                            break;
                        case "long":
                            parameter.setParamType(Parameter.Type.ARRAY_LONG);
                            break;
                        case "float":
                            parameter.setParamType(Parameter.Type.ARRAY_FLOAT);
                            break;
                        case "double":
                            parameter.setParamType(Parameter.Type.ARRAY_DOUBLE);
                            break;
                        default:
                            parameter.setParamType(Parameter.Type.ARRAY_PARCELABLE);
                    }
                } else {
                    if(type == String.class) {
                        parameter.setParamType(Parameter.Type.TYPE_STRING);
                        continue;
                    }
                    String typeName = type.toString().toLowerCase();
                    switch (typeName) {
                        case "boolean":
                            parameter.setParamType(Parameter.Type.TYPE_BOOLEAN);
                            break;
                        case "byte":
                            parameter.setParamType(Parameter.Type.TYPE_BYTE);
                            break;
                        case "char":
                            parameter.setParamType(Parameter.Type.TYPE_CHAR);
                            break;
                        case "int":
                            parameter.setParamType(Parameter.Type.TYPE_INT);
                            break;
                        case "short":
                            parameter.setParamType(Parameter.Type.TYPE_SHORT);
                            break;
                        case "long":
                            parameter.setParamType(Parameter.Type.TYPE_LONG);
                            break;
                        case "float":
                            parameter.setParamType(Parameter.Type.TYPE_FLOAT);
                            break;
                        case "double":
                            parameter.setParamType(Parameter.Type.TYPE_DOUBLE);
                            break;
                        default:
                            parameter.setParamType(Parameter.Type.TYPE_PARCELABLE);
                    }
                }
            }
        }
        return routerMethod;
    }

    private Intent putExtra(RouterMethod routerMethod, Object[] objects) {
        Intent intent = new Intent(mContext, routerMethod.getToActivity());
        Parameter[] parameters = routerMethod.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            Parameter p = parameters[i];
            switch (p.getParamType()) {
                case Parameter.Type.TYPE_BOOLEAN:
                    intent.putExtra(p.getExtraKey(), (boolean) objects[i]);
                    break;
                case Parameter.Type.TYPE_BYTE:
                    intent.putExtra(p.getExtraKey(), (byte) objects[i]);
                    break;
                case Parameter.Type.TYPE_CHAR:
                    intent.putExtra(p.getExtraKey(), (char) objects[i]);
                    break;
                case Parameter.Type.TYPE_SHORT:
                    intent.putExtra(p.getExtraKey(), (short) objects[i]);
                    break;
                case Parameter.Type.TYPE_INT:
                    intent.putExtra(p.getExtraKey(), (int) objects[i]);
                    break;
                case Parameter.Type.TYPE_LONG:
                    intent.putExtra(p.getExtraKey(), (long) objects[i]);
                    break;
                case Parameter.Type.TYPE_FLOAT:
                    intent.putExtra(p.getExtraKey(), (float) objects[i]);
                    break;
                case Parameter.Type.TYPE_DOUBLE:
                    intent.putExtra(p.getExtraKey(), (double) objects[i]);
                    break;
                case Parameter.Type.TYPE_STRING:
                    intent.putExtra(p.getExtraKey(), (String) objects[i]);
                    break;
                case Parameter.Type.TYPE_PARCELABLE:
                    intent.putExtra(p.getExtraKey(), ((Parcelable) objects[i]));
                    break;

                case Parameter.Type.ARRAY_BOOLEAN:
                    intent.putExtra(p.getExtraKey(), (boolean[]) objects[i]);
                    break;
                case Parameter.Type.ARRAY_BYTE:
                    intent.putExtra(p.getExtraKey(), (byte[]) objects[i]);
                    break;
                case Parameter.Type.ARRAY_CHAR:
                    intent.putExtra(p.getExtraKey(), (char[]) objects[i]);
                    break;
                case Parameter.Type.ARRAY_SHORT:
                    intent.putExtra(p.getExtraKey(), (short[]) objects[i]);
                    break;
                case Parameter.Type.ARRAY_INT:
                    intent.putExtra(p.getExtraKey(), (int[]) objects[i]);
                    break;
                case Parameter.Type.ARRAY_LONG:
                    intent.putExtra(p.getExtraKey(), (long[]) objects[i]);
                    break;
                case Parameter.Type.ARRAY_FLOAT:
                    intent.putExtra(p.getExtraKey(), (float[]) objects[i]);
                    break;
                case Parameter.Type.ARRAY_DOUBLE:
                    intent.putExtra(p.getExtraKey(), (double[]) objects[i]);
                    break;
                case Parameter.Type.ARRAY_STRING:
                    intent.putExtra(p.getExtraKey(), (String[]) objects[i]);
                    break;
                case Parameter.Type.ARRAY_PARCELABLE:
                    intent.putExtra(p.getExtraKey(), (Parcelable[]) objects[i]);
                    break;
            }
        }
        return intent;
    }

    void inject() {
        String injectClass = mContext.getClass().getName() + "_RouterInject";
        try {
            Constructor<?> constructor = mContext.getClassLoader()
                    .loadClass(injectClass)
                    .getConstructor(mContext.getClass());
            constructor.newInstance(mContext);
        } catch (InstantiationException | NoSuchMethodException | InvocationTargetException |
                IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
        }
    }

    static _Router init(Context activity) {
        return new _Router(activity);
    }


}