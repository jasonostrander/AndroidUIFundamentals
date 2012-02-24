package com.example;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class ViewAnimationActivity extends Activity {
    int i = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_animation_layout);
        ImageView iv = (ImageView) findViewById(R.id.image_view);
        iv.setOnTouchListener(new OnTouchListener() {
            
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ImageView iv = (ImageView) v;
                Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.simple_set);
                iv.startAnimation(anim);
                return true;
            }
        });
    }
}