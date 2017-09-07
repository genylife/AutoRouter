package com.dinglc.autorouter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.dinglc.router.annoation.RequestInt;
import com.dinglc.router.annoation.RequestString;
import com.dinglc.router.injector.InjectIntExtra;

@RequestInt({"type", "main"})
@RequestString("param")
public class MainActivity extends AppCompatActivity {

    @InjectIntExtra(key = "type", defaultValue = -1)
    int mInt;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this, BActivity.class);
        Bundle bundle = new Bundle();
        //        getIntent().getIn;
    }
}
