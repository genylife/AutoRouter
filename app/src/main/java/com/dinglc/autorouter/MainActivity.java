package com.dinglc.autorouter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.util.Locale;

import router.AutoExtra;
import router.AutoRouter;
import router.Router;
import router.RouterService;
import router.RouterType;


@AutoRouter(value = {"in", "st", "bo", "by", "sh", "ch", "lo", "fl", "dou"},
        type = {RouterType.INT, RouterType.STRING, RouterType.BOOLEAN, RouterType.BYTE, RouterType.SHORT, RouterType.CHAR,
                RouterType.LONG, RouterType.FLOAT, RouterType.DOUBLE})
public class MainActivity extends AppCompatActivity {

    @AutoExtra int mInt;
    @AutoExtra boolean mBoolean;
    @AutoExtra byte mByte;
    @AutoExtra char mChar;
    @AutoExtra short mShort;
    @AutoExtra long mLong;
    @AutoExtra float mFloat;
    @AutoExtra double mDouble;
    @AutoExtra String mString;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RouterService service = Router.init(this).create(RouterService.class);

        String format = "mInt =%d\nmBoolean =%b\nmByte =%d\nmChar =%c\nmShort =%d\nmLong =%d\nmFloat =%f\nmDouble =%f\nmString " +
                "=%s";
        String format1 = String.format(Locale.CHINA, format, mInt, mBoolean, mByte, mChar, mShort, mLong, mFloat, mDouble,
                mString);
        ((TextView) findViewById(R.id.tt)).setText(format1);

        Intent intent = new Intent(this, BActivity.class);
        Bundle bundle = new Bundle();
startActivity(intent,bundle);
        
        //1, ((short) 2), ((short) 3), true, false, ((byte) 98), 'c'
        service.cActivity(1, ((short) 2), ((short) 3), true, false, ((char) 98), ((byte) 33))
                .go();
    }
}
