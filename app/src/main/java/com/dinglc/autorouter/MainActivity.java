package com.dinglc.autorouter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import router.Router;
import router.RouterService;
import router.annoation.RequestInt;
import router.annoation.RequestString;
import router.injector.InjectIntExtra;
import router.injector.InjectStringExtra;


@RequestInt({"type", "main"})
@RequestString("param")
public class MainActivity extends AppCompatActivity {

    @InjectIntExtra(key = "type", defaultValue = -1)
    int mInt;
    @InjectStringExtra(key = "param",defaultValue = "default") String mString;
    
    @InjectIntExtra(key = "main",defaultValue = -2) int main;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RouterService service = Router.init(this).create(RouterService.class);
        new MainActivity_RouterInject(this).inject();
        

        Intent intent = new Intent(this, BActivity.class);
        Bundle bundle = new Bundle();
        
//        service.toBActivity(1,(short) 1,(short)1,true,null,true,(byte)2,'c');
//        service.toCActivity();
    }
}
