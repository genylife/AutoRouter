package com.dinglc.autorouter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import router.Router;
import router.RouterService;
import router.request.AutoRouter;

@AutoRouter
public class BActivity extends AppCompatActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView textView = new TextView(this);
        textView.setText("this is B activity!");
        setContentView(textView);
        Router.init(this).create(RouterService.class)
                .mainActivity(true, (byte) 97, 's', 98.4D, 78.3F, 34, 123456789L, (short) 11, "this is a string!")
                .go();
    }
}
