package com.dinglc.autorouter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.util.Locale;

import router.Router;
import router.RouterService;
import router.injector.InjectBooleanExtra;
import router.injector.InjectByteExtra;
import router.injector.InjectCharExtra;
import router.injector.InjectIntExtra;
import router.injector.InjectShortExtra;
import router.request.RequestBoolean;
import router.request.RequestByte;
import router.request.RequestChar;
import router.request.RequestInt;
import router.request.RequestShort;

@RequestInt("a")
@RequestShort({"b","d"})
@RequestBoolean({"f","r"})
@RequestChar("w")
@RequestByte("v")
public class CActivity extends AppCompatActivity {

    @InjectIntExtra(key = "a",defaultValue = -1) int a;
    @InjectShortExtra(key = "b",defaultValue = 77) short b;
    @InjectShortExtra(key = "d",defaultValue = 33) short d;
    @InjectBooleanExtra(key = "f",defaultValue = true) boolean f;
    @InjectByteExtra(key = "v",defaultValue = 9) byte v;
    @InjectCharExtra(key = "w",defaultValue = 'w') char w;
    @InjectBooleanExtra(key = "r",defaultValue = false) boolean r;
    

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView textView=new TextView(this);
        textView.setText("this ia C activity");
        setContentView(textView);
        Router.init(this).inject().create(RouterService.class);
        String format = "a =%d\nb =%d\nd =%d\nf =%s\nv =%d\nw =%c\nr =%s";
        String format1 = String.format(Locale.CHINA, format, a,b,d,f,v,w,r);
        textView.setText(format1);
    }
}
