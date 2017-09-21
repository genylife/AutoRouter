package router;

import android.app.Activity;
import android.content.Intent;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class Router {

    private Activity mActivity;

    private Router(Activity activity) {
        mActivity = activity;
    }

    @SuppressWarnings("unchecked")
    public <T> T create(Class<T> service) {
        if(service.getCanonicalName().equals("router.RouterService")) {
            return (T) Proxy.newProxyInstance(service.getClassLoader(), new Class[]{service},
                    new InvocationHandler() {
                        @Override
                        public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
                            RouterMethod routerMethod = loadRouterMethod(method, objects);
                            Intent intent = putExtra(routerMethod, objects);
                            return new IntentWrapper(mActivity,intent);
                        }
                    });
        } else {
            throw new IllegalArgumentException("The param must be RouterService.class,but this is not!!");
        }
    }

    private RouterMethod loadRouterMethod(Method method, Object[] objects) {
        Annotation[][] parameterAnnotationsArray = method.getParameterAnnotations();

        RouterMethod routerMethod = new RouterMethod(parameterAnnotationsArray.length);

        String toActivityClass = method.getAnnotation(RouterClass.class).value();
        try {
            Class<?> toActivity = Class.forName(toActivityClass);
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

        for (int i = 0; i < objects.length; i++) {
            if(objects[i] instanceof Boolean) {
                routerMethod.getParameters()[i].setParamType(1);
                continue;
            }
            if(objects[i] instanceof Byte) {
                routerMethod.getParameters()[i].setParamType(2);
                continue;
            }
            if(objects[i] instanceof Character) {
                routerMethod.getParameters()[i].setParamType(3);
                continue;
            }
            if(objects[i] instanceof Short) {
                routerMethod.getParameters()[i].setParamType(4);
                continue;
            }
            if(objects[i] instanceof Integer) {
                routerMethod.getParameters()[i].setParamType(5);
                continue;
            }
            if(objects[i] instanceof Long) {
                routerMethod.getParameters()[i].setParamType(6);
                continue;
            }
            if(objects[i] instanceof Float) {
                routerMethod.getParameters()[i].setParamType(7);
                continue;
            }
            if(objects[i] instanceof Double) {
                routerMethod.getParameters()[i].setParamType(8);
                continue;
            }
            if(objects[i] instanceof String) {
                routerMethod.getParameters()[i].setParamType(9);
            }
        }
        return routerMethod;
    }

    private Intent putExtra(RouterMethod routerMethod, Object[] objects) {
        Intent intent = new Intent(mActivity, routerMethod.getToActivity());

        Parameter[] parameters = routerMethod.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            Parameter p = parameters[i];
            switch (p.getParamType()) {
                case Parameter.ParameterType.TYPE_BOOLEAN:
                    intent.putExtra(p.getExtraKey(), (boolean) objects[i]);
                    break;
                case Parameter.ParameterType.TYPE_BYTE:
                    intent.putExtra(p.getExtraKey(), (byte) objects[i]);
                    break;
                case Parameter.ParameterType.TYPE_CHAR:
                    intent.putExtra(p.getExtraKey(), (char) objects[i]);
                    break;
                case Parameter.ParameterType.TYPE_SHORT:
                    intent.putExtra(p.getExtraKey(), (short) objects[i]);
                    break;
                case Parameter.ParameterType.TYPE_INT:
                    intent.putExtra(p.getExtraKey(), (int) objects[i]);
                    break;
                case Parameter.ParameterType.TYPE_LONG:
                    intent.putExtra(p.getExtraKey(), (long) objects[i]);
                    break;
                case Parameter.ParameterType.TYPE_FLOAT:
                    intent.putExtra(p.getExtraKey(), (float) objects[i]);
                    break;
                case Parameter.ParameterType.TYPE_DOUBLE:
                    intent.putExtra(p.getExtraKey(), (double) objects[i]);
                    break;
                case Parameter.ParameterType.TYPE_STRING:
                    intent.putExtra(p.getExtraKey(), (String) objects[i]);
                    break;
            }
        }
        return intent;
    }

    public Router inject() {
        String injectClass = mActivity.getClass().getName() + "_RouterInject";
        try {
            Constructor<?> constructor = mActivity.getClassLoader()
                    .loadClass(injectClass)
                    .getConstructor(mActivity.getClass());
            constructor.newInstance(mActivity);
        } catch (InstantiationException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException | 
                IllegalAccessException e) {
            e.printStackTrace();
            return this;
        }
        return this;
    }

    public static Router init(Activity activity) {
        return new Router(activity);
    }


}