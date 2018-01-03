package com.dinglc.autorouter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import router.annotation.AutoRouter;

@AutoRouter
public class BActivity extends AppCompatActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView textView = new TextView(this);
        textView.setText("this is B activity!");
        setContentView(textView);
        /*Router.create(this)
                .mainActivity(123, "this is a string", true, ((byte) 33),
                        ((byte) 34), 'c', 9731923719L, 1.3F, 44.556D,new double[1] ,new Bean("hehehe"))
                .go();*/
    }
}
