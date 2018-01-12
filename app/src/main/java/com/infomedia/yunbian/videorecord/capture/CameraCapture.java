package com.infomedia.yunbian.videorecord.capture;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLSurfaceView;

import com.infomedia.yunbian.videorecord.GLDrawer2D;
import com.infomedia.yunbian.videorecord.GLRender;
import com.infomedia.yunbian.videorecord.capture.camera.CameraManager;
import com.infomedia.yunbian.videorecord.utils.L;

import java.io.IOException;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by pc on 2018/1/10.
 */

public class CameraCapture implements GLRender.RenderListener, SurfaceTexture.OnFrameAvailableListener {
    private static final String TAG = "CameraCapture";

    /**
     * 相机实体
     */
//    private CameraManager mCameraManager;
    private GLRender glRender;
    private GLSurfaceView glSurfaceView;

    public CameraCapture() {
//        mCameraManager = new CameraManager();
        glRender = new GLRender();
        glRender.setRenderListener(this);
    }


    public void start(int cameraFace) {
//        mCameraManager.open(cameraFace);
//        mCameraManager.setPreviewTexture(glRender.getSurfaceTexture());
//        mCameraManager.startPreview();
    }


    public void setDisplayPreview(GLSurfaceView displayPreview) {
        glSurfaceView = displayPreview;
        glRender.init(displayPreview);


    }


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        L.e(TAG, "onSurfaceCreated");
//        mCameraManager.setPreviewTexture(glRender.getSurfaceTexture());
        glRender.getSurfaceTexture().setOnFrameAvailableListener(this);
//        mCameraManager.startPreview();
//        mCameraManager.open(0);
//        mCameraManager.setPreviewTexture(glRender.getSurfaceTexture());
//        mCameraManager.startPreview();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        L.e(TAG, "onSurfaceChanged");

        Camera camera = Camera.open();
        Camera.Parameters parameters = camera.getParameters();
        parameters.setPreviewSize(1280, 720);
        parameters.setPictureSize(1280, 720);
        camera.setParameters(parameters);
        try {
            camera.setPreviewTexture(glRender.getSurfaceTexture());
        } catch (Exception e) {
            e.printStackTrace();
            L.e(TAG,"onSurfaceCreated fail@@@@@@@@@@@@@@@@@@@@@@@@@");
        }
//        glRender.getSurfaceTexture().setOnFrameAvailableListener(this);
        camera.startPreview();

    }

    @Override
    public void onDrawFrame(GL10 gl) {
        L.e(TAG, "onDrawFrame");
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        L.e(TAG, "onFrameAvailable");
        refresh();
    }

    public void refresh() {
        L.e(TAG, "refresh");
        glSurfaceView.requestRender();
    }

}
