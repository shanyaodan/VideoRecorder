package com.dyc.testbmp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        GLSurfaceView glSurfaceView = findViewById(R.id.glview);
        glSurfaceView.setEGLContextClientVersion(2);


//        Render render = new Render(getResources());
        MyRender render = new MyRender(this);
        glSurfaceView.setRenderer(render);
        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

    }
}
