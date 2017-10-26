package com.dinglc.autorouter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import router.Converter;
import router.Parser;
import router.Router;
import router.annotation.AutoRouter;

@AutoRouter
public class BActivity extends AppCompatActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Router.init(new Converter() {
            @Override public String convert(Object object, Class clazz) {
                return JSON.toJSONString(object);
            }
        }, new Parser() {
            @Override public <T> T parse(String text, Class<T> clazz) {
                return JSON.parseObject(text, clazz);
            }
        });
        super.onCreate(savedInstanceState);
        TextView textView = new TextView(this);
        textView.setText("this is B activity!");
        setContentView(textView);
        Router.with(this)
                .mainActivity(123, "this is a string", true, ((byte) 33),
                        ((byte) 34), 'c', 9731923719L, 1.3F, 44.556D, new Bean("hehehe"))
                .go();
    }
}
