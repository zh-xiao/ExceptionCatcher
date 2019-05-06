package com.xiao.exceptioncatcher;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void nullExceptionOnMainThread(View view) {
        String s=null;
        s.length();
    }

    public void nullExceptionOnSubThread(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String s=null;
                s.length();
            }
        }).start();
    }
}
