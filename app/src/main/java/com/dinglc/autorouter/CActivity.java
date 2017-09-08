package com.dinglc.autorouter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import router.annoation.AutoRouter;

@AutoRouter
public class CActivity extends AppCompatActivity {


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView textView=new TextView(this);
//        ViewGroup.LayoutParams layoutParams = textView.getLayoutParams();
//        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
//        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
//        textView.setLayoutParams(layoutParams);
        textView.setText("8436463436436464545555555555555555555555555555555");
        setContentView(textView);
    }
}
