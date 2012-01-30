package com.example;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

public class ExampleOpenGLActivity extends Activity {
    GLSurfaceView mGLView;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGLView = new ExampleGLSurfaceView(this);
        setContentView(mGLView);
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        mGLView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGLView.onResume();
    }
}