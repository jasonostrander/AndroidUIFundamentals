package com.example;

import android.app.Activity;
import android.os.Bundle;

public class RenderScriptExampleActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new ExampleView(this));
    }
}