package router;

import android.app.Activity;
import android.content.Intent;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class Router {

    private Activity mActivity;

    public Router(Activity activity) throws ClassNotFoundException {
        mActivity = activity;
        Class<?> routerService;
        try {
            routerService = Class.forName("router.RouterService");
            Object o = create(routerService);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        
    }

    @SuppressWarnings("unchecked")
    private <T> T create(Class<T> service) {
        


        return (T) Proxy.newProxyInstance(service.getClassLoader(), new Class[]{service},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
                        String toActivityClass = method.getAnnotation(RouterClass.class).value();
                        Class<?> toActivity = Class.forName(toActivityClass);
                        Intent i=new Intent(mActivity,toActivity);
                        mActivity.startActivity(i);
                        return null;
                    }
                });
    }


}
