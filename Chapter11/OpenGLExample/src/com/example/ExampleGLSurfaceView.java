package com.example;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

public class ExampleGLSurfaceView extends GLSurfaceView {
    public ExampleRenderer mRenderer;

    public ExampleGLSurfaceView(Context context) {
        super(context);
        mRenderer = new ExampleRenderer();
        setRenderer(mRenderer);
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
        case MotionEvent.ACTION_MOVE:
            final float x = event.getX()/getWidth();
            final float y = event.getY()/getHeight();
            queueEvent(new Runnable() {
                @Override
                public void run() {
                    mRenderer.setColor(x, y, 0.5f);
                }
            });
            return true;
        }
        return super.onTouchEvent(event);
    }
}
