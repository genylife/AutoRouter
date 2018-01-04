package com.dinglc.autorouter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import router.Router;
import router.annotation.AutoRouter;

@AutoRouter
public class BActivity extends AppCompatActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView textView = new TextView(this);
        textView.setText("this is B activity!");
        setContentView(textView);
        Router.create(this)
                .mainActivity(
                        new int[]{1, 2}, 10,
                        new String[]{"q", "w"}, "qw",
                        new boolean[]{false, false}, false,
                        new byte[]{1, 2}, ((byte) 3),
                        new short[]{3, 4}, (short) 4,
                        new char[]{'a', 'b'}, 'q',
                        new long[]{2L, 3L}, 4L,
                        new float[]{.1F, .2F}, .3F,
                        .7D, new double[]{.4D, .5D},
                        new Bean[2], new Bean("123")
                )
                .go();
    }
}
