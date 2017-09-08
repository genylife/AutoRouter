package router;

import android.app.Activity;
import android.content.Intent;

import java.lang.reflect.InvocationHandler;
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
                            String toActivityClass = method.getAnnotation(RouterClass.class).value();
                            Class<?> toActivity = Class.forName(toActivityClass);
                            Intent i = new Intent(mActivity, toActivity);
                            mActivity.startActivity(i);
                            return null;
                        }
                    });
        } else {
            throw new IllegalArgumentException("The param must be RouterService.class,but this is not!!");
        }
    }
    
    public Router inject(){
//        mActivity.getIntent().
        return this;
    }

    public static Router init(Activity activity) {
        return new Router(activity);
    }


}