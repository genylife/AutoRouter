package com.dinglc.autorouter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.util.Locale;

import router.Router;
import router.RouterService;
import router.request.RequestBoolean;
import router.request.RequestByte;
import router.request.RequestChar;
import router.request.RequestDouble;
import router.request.RequestFloat;
import router.request.RequestInt;
import router.request.RequestLong;
import router.request.RequestShort;
import router.request.RequestString;
import router.injector.InjectBooleanExtra;
import router.injector.InjectByteExtra;
import router.injector.InjectCharExtra;
import router.injector.InjectDoubleExtra;
import router.injector.InjectFloatExtra;
import router.injector.InjectIntExtra;
import router.injector.InjectLongExtra;
import router.injector.InjectShortExtra;
import router.injector.InjectStringExtra;


@RequestInt("in")
@RequestString("st")
@RequestBoolean("bo")
@RequestByte("by")
@RequestShort("sh")
@RequestChar("ch")
@RequestLong("lo")
@RequestFloat("fl")
@RequestDouble("dou")
public class MainActivity extends AppCompatActivity {

    @InjectIntExtra(key = "in", defaultValue = -1) int mInt;
    @InjectBooleanExtra(key = "bo", defaultValue = false) boolean mBoolean;
    @InjectByteExtra(key = "by", defaultValue = 'a') byte mByte;
    @InjectCharExtra(key = "ch", defaultValue = 98) char mChar;
    @InjectShortExtra(key = "sh", defaultValue = 6) short mShort;
    @InjectLongExtra(key = "lo", defaultValue = 9999999999L) long mLong;
    @InjectFloatExtra(key = "fl", defaultValue = 2.2F) float mFloat;
    @InjectDoubleExtra(key = "dou", defaultValue = 4.4D) double mDouble;
    @InjectStringExtra(key = "st") String mString;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RouterService service = Router.init(this).inject().create(RouterService.class);

        String format = "mInt =%d\nmBoolean =%b\nmByte =%d\nmChar =%c\nmShort =%d\nmLong =%d\nmFloat =%f\nmDouble =%f\nmString " +
                "=%s";
        String format1 = String.format(Locale.CHINA, format, mInt, mBoolean, mByte, mChar, mShort, mLong, mFloat, mDouble,
                mString);
        ((TextView) findViewById(R.id.tt)).setText(format1);

        Intent intent = new Intent(this, BActivity.class);
        Bundle bundle = new Bundle();

        //1, ((short) 2), ((short) 3), true, false, ((byte) 98), 'c'
        service.cActivity(1, ((short) 2), ((short) 3), true, false, ((byte) 98), 'c')
                .go();
    }
}
