package com.example;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class AccessibilityExampleActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.focus_example);
        
        getResources().getString(R.string.hello);
    }
}