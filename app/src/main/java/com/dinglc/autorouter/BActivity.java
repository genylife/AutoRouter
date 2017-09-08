package com.dinglc.autorouter;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import router.annoation.RequestBoolean;
import router.annoation.RequestByte;
import router.annoation.RequestChar;
import router.annoation.RequestInt;
import router.annoation.RequestSerializable;
import router.annoation.RequestShort;

@RequestInt("id") 
@RequestShort({"p1","p2"})
@RequestBoolean({"b1","b2"})
@RequestSerializable("s1")
@RequestChar("c1")
@RequestByte("by1")
public class BActivity extends AppCompatActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }
}
