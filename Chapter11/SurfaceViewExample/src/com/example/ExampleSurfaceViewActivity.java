package com.example;

import android.app.Activity;
import android.os.Bundle;

public class ExampleSurfaceViewActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new ExampleSurfaceView(this));
    }
}