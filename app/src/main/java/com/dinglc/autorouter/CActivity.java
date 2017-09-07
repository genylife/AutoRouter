package com.dinglc.autorouter;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.dinglc.router.annoation.AutoRouter;

/**
 * Created by DingZhu on 2017/9/7.
 *
 * @since 1.0.0
 */

@AutoRouter
public class CActivity extends AppCompatActivity {


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }
}
