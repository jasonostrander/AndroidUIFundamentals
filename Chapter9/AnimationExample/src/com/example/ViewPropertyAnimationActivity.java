package com.example;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.TextView;

public class ViewPropertyAnimationActivity extends Activity {
    boolean toggle = true;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_property_layout);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            final TextView tv = (TextView) findViewById(R.id.text1);
            float new_y = 3f * tv.getHeight();
            
            if (toggle) {
                tv.animate().yBy(new_y).alpha(0f).setDuration(250);
            } else {
                tv.animate().yBy(-new_y).alpha(1f).setDuration(250);
            }
            toggle = !toggle;
            return true;
        }
        return super.onTouchEvent(event);
    }

}
