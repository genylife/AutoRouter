package com.dinglc.autorouter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.util.Locale;

import router.annotation.AutoExtra;
import router.annotation.AutoRouter;

@AutoRouter
public class CActivity extends AppCompatActivity {

    @AutoExtra("a") int a;
    @AutoExtra("b") short b;
    @AutoExtra("d") short d;
    @AutoExtra("f") boolean f;
    @AutoExtra("v") byte v;
    @AutoExtra("w") char w;
    @AutoExtra("r") boolean r;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView textView = new TextView(this);
        textView.setText("this ia C activity");
        setContentView(textView);
//        RouterService service = Router.with(this);
        String format = "a =%d\nb =%d\nd =%d\nf =%s\nv =%d\nw =%c\nr =%s";
        String format1 = String.format(Locale.CHINA, format, a, b, d, f, v, w, r);
        textView.setText(format1);
    }
}
