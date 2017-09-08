package com.dinglc.autorouter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import router.annoation.RequestBoolean;
import router.annoation.RequestByte;
import router.annoation.RequestChar;
import router.annoation.RequestInt;
import router.annoation.RequestSerializable;
import router.annoation.RequestShort;

@RequestInt("a") 
@RequestShort({"b","d"})
@RequestBoolean({"f","r"})
@RequestSerializable("o")
@RequestChar("w")
@RequestByte("v")
public class BActivity extends AppCompatActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView textView=new TextView(this);
        textView.setText("this is B activity!");
        setContentView(textView);
    }
}
