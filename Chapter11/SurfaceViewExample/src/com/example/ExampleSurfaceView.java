package com.example;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class ExampleSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    SurfaceHolder mSurfaceHolder;
    DrawingThread mThread;
    int mRed = 0;
    int mGreen = 0;
    int mBlue = 127;
    
    float[] mVertices = new float[6];
    int[] mColors = {
            0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF,
            0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF};
    Paint mPaint = new Paint(); 
    float mAngle = 0;
    float mCenterX = 0;
    float mCenterY = 0;
    
    public ExampleSurfaceView(Context context) {
        super(context);
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
        mThread = new DrawingThread();
        mPaint.setColor(0xFFFFFFFF);
        mPaint.setStyle(Paint.Style.FILL);
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRGB(mRed, mGreen, mBlue);
        canvas.rotate(mAngle, mCenterX, mCenterY);
        canvas.drawVertices(Canvas.VertexMode.TRIANGLES, 6, mVertices, 0, null, 0, mColors, 0, null, 0, 0, mPaint);
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
        case MotionEvent.ACTION_MOVE:
            synchronized(mSurfaceHolder) {
                mRed = (int) (255*event.getX()/getWidth());
                mGreen = (int) (255*event.getY()/getHeight());
            }
            return true;
        }
        return super.onTouchEvent(event);
    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mVertices[0] = width/2;
        mVertices[1] = height/2;
        mVertices[2] = width/2 + width/5;
        mVertices[3] = height/2 + width/5;
        mVertices[4] = width/2;
        mVertices[5] = height/2 + width/5;
        mCenterX = width/2 + width/10;
        mCenterY = height/2 + width/10;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mThread.keepRunning = true;
        mThread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mThread.keepRunning = false;
        boolean retry = true;
        while (retry) {
            try {
                mThread.join();
                retry = false;
            } catch (InterruptedException e) {}
        }
    }
    
    private class DrawingThread extends Thread {
        boolean keepRunning = true;

        @Override
        public void run() {
            Canvas c;
            while (keepRunning) {
                c = null;
                
                try {
                    c = mSurfaceHolder.lockCanvas();
                    synchronized (mSurfaceHolder) {
                        mAngle += 1;
                        onDraw(c);
                    }
                } finally {
                    if (c != null)
                        mSurfaceHolder.unlockCanvasAndPost(c);
                }

                // Run the draw loop at 50FPS
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {}
            }
        }
    }
}
