package com.example;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLSurfaceView;
import android.opengl.GLU;

public class ExampleRenderer implements GLSurfaceView.Renderer {
    private float mRed;
    private float mGreen;
    private float mBlue;
    private float mAngle; 
    private long mLastFrameTime = 0;
    float[] mVertices = {
            -1.0f, -1.0f, 0,
             1.0f, -1.0f, 0,
             0.0f,  1.0f, 0};
    FloatBuffer mVertexBuffer;

    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // Set up the triangle vertices in FloatBuffers as needed by OpenGl
        ByteBuffer vertexByteBuffer = ByteBuffer.allocateDirect(mVertices.length * 4);
        vertexByteBuffer.order(ByteOrder.nativeOrder()); 
        mVertexBuffer = vertexByteBuffer.asFloatBuffer();
        mVertexBuffer.put(mVertices);
        mVertexBuffer.position(0);
    }

    private void updateAngle() {
        long now = System.currentTimeMillis();
        if (mLastFrameTime != 0) {
            mAngle += 10*(now - mLastFrameTime)/1000.0;
        }
        mLastFrameTime = now;
    }
 
    public void onDrawFrame(GL10 gl) {
        gl.glClearColor(mRed, mGreen, mBlue, 1.0f);
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        updateAngle();
        gl.glLoadIdentity();
        gl.glTranslatef(0.0f, 0.0f, -7.0f);
        gl.glRotatef(mAngle, 0.0f, 0.0f, 1.0f);

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glColor4f(255f, 255f, 255f, 0.0f);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVertexBuffer);
        gl.glDrawArrays(GL10.GL_TRIANGLES, 0, 3);
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
    }

    public void onSurfaceChanged(GL10 gl, int width, int height) {
        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL10.GL_PROJECTION);    // Select the projection matrix
        gl.glLoadIdentity();                    // Reset the matrix to default state
        
        // Calculate the aspect ratio of the window
        float ratio = (float) width/height;
        GLU.gluPerspective(gl, 45.0f, ratio, 0.1f, 100.0f);
        
        // Set the GL_MODELVIEW transformation mode
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    public void setColor(float red, float green, float blue) {
        mRed = red;
        mGreen = green;
        mBlue = blue;
    }
}
