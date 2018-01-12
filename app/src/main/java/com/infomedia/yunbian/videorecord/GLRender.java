package com.infomedia.yunbian.videorecord;

import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.infomedia.yunbian.videorecord.filter.OesFilter;
import com.infomedia.yunbian.videorecord.utils.Glutil;
import com.infomedia.yunbian.videorecord.utils.L;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by pc on 2017/12/27.
 */

public class GLRender  {


    private RenderListener renderListner;
    private SurfaceTexture texture;
    private int  textureId;
    private OesFilter oesFilter;
    private final float[] mSTMatrix = new float[16];
    public boolean init;
//    private GLDrawer2D glDrawer2D;

    public void init(GLSurfaceView glSurfaceView){
        glSurfaceView.setEGLContextClientVersion(2);
        glSurfaceView.setRenderer(renderer);
        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

//        glDrawer2D = new GLDrawer2D();
        init = true;
    }

    public SurfaceTexture getSurfaceTexture(){
        return texture;
    }


    private static final  String TAG ="GLRender";
    GLSurfaceView.Renderer renderer =new GLSurfaceView.Renderer() {

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            L.e(TAG,"onSurfaceCreated");
            Glutil.checkGlError("glGenTextures");

//            oesFilter.onSurfaceCreate();
//            texture =oesFilter.surfaceTexture;
            textureId=Glutil.createOESTextureObject();
//            textureId = GLDrawer2D.initTex();
            texture = new SurfaceTexture(textureId);
            if(null!=renderListner){
                renderListner.onSurfaceCreated(gl,config);
            }
            oesFilter = new OesFilter();
            oesFilter.onSurfaceCreate();
//            glDrawer2D = new GLDrawer2D();

        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            L.e(TAG,"onSurfaceChanged");
            GLES20.glViewport(0,0,width,height);

//            oesFilter.onSurFaceChanged(width,height);
//				GLES20.glViewport(0, 0, width, height);
				GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
            if(null!=renderListner){
                renderListner.onSurfaceChanged(gl,width,height);
            }
        }

        @Override







        public void onDrawFrame(GL10 gl) {
            L.e(TAG,"onDrawFrame");
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
            texture.updateTexImage();
            texture.getTransformMatrix(mSTMatrix);
            oesFilter.draw(textureId,mSTMatrix);
//            glDrawer2D.draw(textureId, mSTMatrix);
            if(null!=renderListner){
                renderListner.onDrawFrame(gl);
            }
        }
    };



    public interface RenderListener{
        void onSurfaceCreated(GL10 gl, EGLConfig config);
        void onSurfaceChanged(GL10 gl, int width, int height);
        void onDrawFrame(GL10 gl);
    }

    public void setRenderListener(RenderListener listener){

        this.renderListner = listener;
    }

}
