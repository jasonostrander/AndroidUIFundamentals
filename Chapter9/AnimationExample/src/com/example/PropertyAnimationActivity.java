package com.example;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class PropertyAnimationActivity extends Activity {
    int i = 0;
    boolean toggle = true;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.property_animation_layout);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            final TextView tv = (TextView) findViewById(R.id.text1);
            float new_y = 0.25f * tv.getHeight();
            
            AnimatorSet set = new AnimatorSet();

            if (toggle) {
                // Use XML resource to define the animation
                AnimatorSet test = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.set);
                test.setTarget(tv);
                test.start();
            } else {
                // Define the animation in code
                ObjectAnimator scale = ObjectAnimator.ofFloat(tv, View.SCALE_X, 1f);
                ObjectAnimator alpha = ObjectAnimator.ofFloat(tv, View.ALPHA, 1f);
                ObjectAnimator slide_up = ObjectAnimator.ofFloat(tv, View.TRANSLATION_Y, -new_y);
                set.play(alpha).with(slide_up).before(scale);
            }
            set.setDuration(1000);
            set.start();
            toggle = !toggle;
            return true;
        }
        return super.onTouchEvent(event);
    }
}
