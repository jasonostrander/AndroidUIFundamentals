package com.example;

import android.app.Activity;
import android.os.Bundle;

public class GestureExampleActivity extends Activity  {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TouchExample view = new TouchExample(this);
        setContentView(view);
    }
}