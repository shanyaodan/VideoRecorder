package com.infomedia.yunbian.videorecord.filter;

import android.opengl.GLES20;

/**
 * Created by pc on 2018/1/9.
 */

public abstract class BaseFilter {

    public int surfaceWidth,surfaceHeight;
    protected int program;
    public int textureId;
    public abstract void init();

    public void onSurfaceCreate(){

    }

    public void onSurFaceChanged(int width,int height){
        surfaceWidth= width;
        surfaceHeight = height;
//        GLES20.glClearColor(1.0f, 1.0f, 0.0f, 1.0f);
//        GLES20.glClear(  GLES20.GL_COLOR_BUFFER_BIT);
//        GLES20.glViewport(0, 0, surfaceWidth, surfaceHeight);
    }

    public void onDraw(float[]data){
//        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);


    }
    public void draw(int textid,float[]data){
//        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);


    }
    public void ondestory(){

    }




}
