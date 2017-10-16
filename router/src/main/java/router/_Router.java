package router;

import android.app.Activity;
import android.content.Intent;

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

    private Activity mActivity;

    private static Converter converter;
    private static Parser parser;

    static void setConverter(Converter converter) {
        _Router.converter = converter;
    }

    static void setParser(Parser parser) {
        _Router.parser = parser;
    }

    private _Router(Activity activity) {
        mActivity = activity;
        if(converter == null || parser == null) {
            throw new IllegalStateException("please call _Router.init(...) in Application onCreate!");
        }
    }

    @SuppressWarnings("unchecked") <T> T create(Class<T> service) {
        return (T) Proxy.newProxyInstance(service.getClassLoader(), new Class[]{service},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
                        RouterMethod routerMethod = loadRouterMethod(method, objects);
                        Intent intent = putExtra(routerMethod, objects);
                        return new IntentWrapper(mActivity, intent);
                    }
                });
    }

    private RouterMethod loadRouterMethod(Method method) {
        RouterMethod result = routerMethodCache.get(method);
        if(result != null) return result;

        synchronized (routerMethodCache) {
            result = routerMethodCache.get(method);
            if(result == null) {
                result = new RouterMethod(method.getParameterAnnotations().length);
                String toActivityClass = method.getAnnotation(RouterClass.class).value();
                try {
                    Class<?> toActivity = Class.forName(toActivityClass);
                    result.setToActivity(toActivity);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                routerMethodCache.put(method, result);
            }
        }
        return result;
    }

    private RouterMethod loadRouterMethod(Method method, Object[] objects) {
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
        for (Type type : parameterTypes) {
            if(type == String.class) {

            }
            switch (type.toString()) {
                case "boolean":
                    break;
                case "byte":
                    break;
                case "char":
                    break;
                case "int":
                    break;
                case "short":
                    break;
                case "long":
                    break;
                case "float":
                    break;
                case "double":
                    break;
                default:
            }
        }

        /*for (int i = 0; i < objects.length; i++) {
            Parameter parameter = routerMethod.getParameters()[i];
            if(objects[i] instanceof Boolean) {
                parameter.setParamType(Parameter.Type.TYPE_BOOLEAN);
                continue;
            }
            if(objects[i] instanceof Byte) {
                parameter.setParamType(Parameter.Type.TYPE_BYTE);
                continue;
            }
            if(objects[i] instanceof Character) {
                parameter.setParamType(Parameter.Type.TYPE_CHAR);
                continue;
            }
            if(objects[i] instanceof Short) {
                parameter.setParamType(Parameter.Type.TYPE_SHORT);
                continue;
            }
            if(objects[i] instanceof Integer) {
                parameter.setParamType(Parameter.Type.TYPE_INT);
                continue;
            }
            if(objects[i] instanceof Long) {
                parameter.setParamType(Parameter.Type.TYPE_LONG);
                continue;
            }
            if(objects[i] instanceof Float) {
                parameter.setParamType(Parameter.Type.TYPE_FLOAT);
                continue;
            }
            if(objects[i] instanceof Double) {
                parameter.setParamType(Parameter.Type.TYPE_DOUBLE);
                continue;
            }
            if(objects[i] instanceof String) {
                parameter.setParamType(Parameter.Type.TYPE_STRING);
                continue;
            }
            parameter.setParamType(Parameter.Type.TYPE_OBJECT);
        }*/
        return routerMethod;
    }

    private Intent putExtra(RouterMethod routerMethod, Object[] objects) {
        Intent intent = new Intent(mActivity, routerMethod.getToActivity());
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
                default:
                    if(converter == null) {
                        throw new IllegalStateException("Router.init(...) should be call at app start!");
                    }
                    intent.putExtra(p.getExtraKey(), converter.convert(objects[i], objects[i].getClass()));
            }
        }
        return intent;
    }

    private _Router inject() {
        if(converter == null) {
            throw new IllegalStateException("Router.init(...) should be call at app start!");
        }
        String injectClass = mActivity.getClass().getName() + "_RouterInject";
        try {
            Constructor<?> constructor = mActivity.getClassLoader()
                    .loadClass(injectClass)
                    .getConstructor(mActivity.getClass(), Parser.class);

            constructor.newInstance(mActivity, parser);
        } catch (InstantiationException | NoSuchMethodException | InvocationTargetException |
                IllegalAccessException e) {
            e.printStackTrace();
            return this;
        } catch (ClassNotFoundException e) {
            return this;
        }
        return this;
    }

    static _Router init(Activity activity) {
        return new _Router(activity).inject();
    }


}