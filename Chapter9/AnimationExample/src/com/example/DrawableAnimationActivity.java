package com.example;

import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

public class DrawableAnimationActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawable_animation_layout);
        ImageView iv = (ImageView) findViewById(R.id.image_view);
        iv.setOnTouchListener(new OnTouchListener() {
            
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ImageView iv = (ImageView) v;
                AnimationDrawable ad = (AnimationDrawable) iv.getDrawable();
                ad.stop();
                ad.start();
                return true;
            }
        });
    }
}
