package com.dinglc.autorouter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.util.Locale;

import router.annotation.AutoExtra;
import router.annotation.AutoRouter;


@AutoRouter
public class MainActivity extends AppCompatActivity {

    @AutoExtra("ins") int[] mInts;
    @AutoExtra("in") int mInt;
    @AutoExtra("sts") String[] mStrings;
    @AutoExtra("st") String mString;
    @AutoExtra("bos") boolean[] mBooleans;
    @AutoExtra("bo") boolean mBoolean;
    @AutoExtra("bys") byte[] mBytes;
    @AutoExtra("by") byte mByte;
    @AutoExtra("shs") short[] mShorts;
    @AutoExtra("sh") short mShort;
    @AutoExtra("chs") char[] mChars;
    @AutoExtra("ch") char mChar;
    @AutoExtra("los") long[] mLongs;
    @AutoExtra("lo") long mLong;
    @AutoExtra("fls") float[] mFloats;
    @AutoExtra("fl") float mFloat;
    @AutoExtra("dou") double mDouble;
    @AutoExtra("array") double[] mDoubles;
    @AutoExtra("123s") Bean[] ttts;
    @AutoExtra("123") Bean ttt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        RouterService service = Router.with(this);

        String format = "mInt =%d\nmBoolean =%b\nmByte =%d\nmChar =%c\nmShort =%d\nmLong =%d\nmFloat =%f\nmDouble =%f\nmString " +
                "=%s\nttt=%s";
        String format1 = String.format(Locale.CHINA, format, mInt, mBoolean, mByte, mChar, mShort, mLong, mFloat, mDouble,
                mString, ttt.toString());
        ((TextView) findViewById(R.id.tt)).setText(format1);

        Intent intent = new Intent(this, BActivity.class);
        Bundle bundle = new Bundle();
        //        startActivity(intent,bundle);

//        service.cActivity(1, ((short) 2), ((short) 3), true, ((byte) 33), ((char) 98), false)
//                .go();
    }
}
