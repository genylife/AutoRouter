package com.dinglc.autorouter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.dinglc.router.annoation.RequestInt;

@RequestInt("type")
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
