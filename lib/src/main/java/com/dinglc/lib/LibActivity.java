package com.dinglc.lib;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import router.annotation.AutoRouter;

@AutoRouter("cn.lib")
public class LibActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lib);
    }
}
