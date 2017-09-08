package com.dinglc.autorouter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.test.RouterService;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import router.RouterClass;
import router.annoation.RequestInt;
import router.annoation.RequestString;
import router.injector.InjectIntExtra;


@RequestInt({"type", "main"})
@RequestString("param")
public class MainActivity extends AppCompatActivity {

    @InjectIntExtra(key = "type", defaultValue = -1)
    int mInt;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this, BActivity.class);
        Bundle bundle = new Bundle();
        //        getIntent().getIn;
        RouterService service = (RouterService) Proxy.newProxyInstance(RouterService.class.getClassLoader(), new Class[]{RouterService.class},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        Log.d("TAG","msg");
                        String toActivityClass = method.getAnnotation(RouterClass.class).value();
                        Class<?> toActivity = Class.forName(toActivityClass);
                        Intent i=new Intent(MainActivity.this,toActivity);
                        startActivity(i);
                        return null;
                    }
                });
//        service.toBActivity(2,234,"111");
        service.toCActivity();
    }
}
