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

    private static Converter converter;
    private static Parser parser;

    static void setConverter(Converter converter) {
        _Router.converter = converter;
    }

    static void setParser(Parser parser) {
        _Router.parser = parser;
    }

    private _Router(Context activity) {
        mContext = activity;
    }

    @SuppressWarnings("unchecked") <T> T create(Class<T> service) {
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
            if(type == String.class) {
                parameter.setParamType(Parameter.Type.TYPE_STRING);
                continue;
            }
            switch (type.toString()) {
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
                    parameter.setParamType(Parameter.Type.TYPE_OBJECT);
                    Class typeClass = (Class) type;
                    Type[] genericInterfaces = typeClass.getGenericInterfaces();
                    for (Type anInterface : genericInterfaces) {
                        if(anInterface instanceof Class) {
                            Class interfaceClass = (Class) anInterface;
                            if(interfaceClass.getCanonicalName().equals(Parcelable.class.getCanonicalName())) {
                                parameter.setParamType(Parameter.Type.TYPE_PARCELABLE);
                                break;
                            }
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
                case Parameter.Type.TYPE_OBJECT:
                    if(converter == null) {
                        throw new IllegalStateException("Router.init(...) should be call at app start!");
                    }
                    intent.putExtra(p.getExtraKey(), converter.convert(objects[i], objects[i].getClass()));
                    break;
                case Parameter.Type.TYPE_PARCELABLE:
                    intent.putExtra(p.getExtraKey(), ((Parcelable) objects[i]));
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
                    .getConstructor(mContext.getClass(), Parser.class);
            constructor.newInstance(mContext, parser);
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