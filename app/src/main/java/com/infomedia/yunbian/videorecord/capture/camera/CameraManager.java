package com.infomedia.yunbian.videorecord.capture.camera;


import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.nfc.Tag;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;

import com.infomedia.yunbian.videorecord.utils.L;

import java.io.IOException;
import java.util.List;

/**
 * Created by pc on 2018/1/10.
 */

public class CameraManager {
    private static final String TAG = "CameraManager";

    private Camera mCamera;
    private CameraHandler cameraHandler;
    private Camera.Parameters parameters;

    public CameraManager() {
        HandlerThread handlerThread = new HandlerThread("CameraManagerHandler");
        handlerThread.start();
        cameraHandler = new CameraHandler(handlerThread.getLooper());
    }


    public Camera camera(){

        return  mCamera;
    }

    public void open(int cameraFace) {
        mCamera = Camera.open(cameraFace);
        final Camera.Parameters params = mCamera.getParameters();
        final List<String> focusModes = params.getSupportedFocusModes();
        if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
            params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
        } else if(focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
            params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        }
        // let's try fastest frame rate. You will get near 60fps, but your device become hot.
        final List<int[]> supportedFpsRange = params.getSupportedPreviewFpsRange();
//					final int n = supportedFpsRange != null ? supportedFpsRange.size() : 0;
//					int[] range;
//					for (int i = 0; i < n; i++) {
//						range = supportedFpsRange.get(i);
//						Log.i(TAG, String.format("supportedFpsRange(%d)=(%d,%d)", i, range[0], range[1]));
//					}
        final int[] max_fps = supportedFpsRange.get(supportedFpsRange.size() - 1);
        Log.i(TAG, String.format("fps:%d-%d", max_fps[0], max_fps[1]));
        params.setPreviewFpsRange(max_fps[0], max_fps[1]);
        params.setRecordingHint(true);
        // request closest supported preview size

        params.setPictureSize(1280,720);
        // rotate camera preview according to the device orientation
//					setRotation(params);
        mCamera.setParameters(params);
        // get the actual preview size
        final Camera.Size previewSize = mCamera.getParameters().getPreviewSize();
        Log.i(TAG, String.format("previewSize(%d, %d)", previewSize.width, previewSize.height));
        // adjust view size with keeping the aspect ration of camera preview.
        // here is not a UI thread and we should request parent view to execute.


    }

    public void release() {
        cameraHandler.sendEmptyMessage(1);

    }

    public void reconnect() {

        cameraHandler.sendEmptyMessage(2);

    }

    public void unlock() {

        cameraHandler.sendEmptyMessage(3);

    }

    public void lock() {
        cameraHandler.sendEmptyMessage(4);
    }

    public void a(SurfaceTexture var1) {
        cameraHandler.obtainMessage(5, var1).sendToTarget();
    }

    public void setPreviewTexture(SurfaceTexture var1) {

        cameraHandler.obtainMessage(23, var1).sendToTarget();

    }

    public void setPreviewDisplay(SurfaceHolder var1) {
        cameraHandler.obtainMessage(19, var1).sendToTarget();
    }

    public void b(SurfaceHolder var1) {
        cameraHandler.obtainMessage(21, var1).sendToTarget();
    }

    public void startPreview() {
        cameraHandler.sendEmptyMessage(6);
    }

    public void f() {
        cameraHandler.sendEmptyMessage(22);
    }

    public void stopPreview() {
        cameraHandler.sendEmptyMessage(7);
    }

    public void setPreviewCallbackWithBuffer(Camera.PreviewCallback var1) {

        cameraHandler.obtainMessage(8, var1).sendToTarget();

    }

    public void addCallbackBuffer(byte[] var1) {
        cameraHandler.obtainMessage(9, var1).sendToTarget();
    }

    public void autoFocus(Camera.AutoFocusCallback var1) {
        cameraHandler.obtainMessage(10, var1).sendToTarget();
    }

    public void cancelAutoFocus() {

        cameraHandler.sendEmptyMessage(11);

    }

//    public void a(final Camera.ShutterCallback var1, final Camera.PictureCallback var2, final Camera.PictureCallback var3, final Camera.PictureCallback var4) {
//
//        cameraHandler.post(new Runnable() {
//            public void run() {
//                b.this.F.takePicture(var1, var2, var3, var4);
//                ;
//            }
//        });
//
//    }

    public void setDisplayOrientation(int var1) {

        cameraHandler.obtainMessage(12, var1, 0).sendToTarget();

    }

    public void setZoomChangeListener(Camera.OnZoomChangeListener var1) {

        cameraHandler.obtainMessage(13, var1).sendToTarget();

    }

    public void setErrorCallback(Camera.ErrorCallback var1) {

        cameraHandler.obtainMessage(14, var1).sendToTarget();

    }

    public void a(Camera.Parameters var1) {

        cameraHandler.obtainMessage(15, var1).sendToTarget();

    }

    public boolean b(Camera.Parameters var1) {
        try {
            this.a(var1);
            return true;
        } catch (RuntimeException var3) {
            L.e("CameraManager", "Camera set parameters failed");
            return false;
        }
    }

    public void c(Camera.Parameters var1) {
        cameraHandler.removeMessages(17);
        cameraHandler.obtainMessage(17, var1).sendToTarget();
    }

    public Camera.Parameters getParameters() {

        cameraHandler.sendEmptyMessage(16);

        Camera.Parameters var1 = parameters;
        cameraHandler = null;
        return var1;
    }


    private class CameraHandler extends Handler {

        public CameraHandler(Looper looper) {
            super(looper);
        }
        @Override
        public void handleMessage(Message var1) {
            L.e(TAG, "handleMessage");
            if (var1 != null && mCamera != null) {
                try {
                    switch (var1.what) {
                        case 1:
                            mCamera.release();
                            mCamera = null;
                            mCamera = null;
                            break;
                        case 2:
                            try {
                                mCamera.reconnect();
                            } catch (IOException var9) {
//                                b.this.f = var9;
                            }
                            break;
                        case 3:
                            mCamera.unlock();
                            break;
                        case 4:
                            mCamera.lock();
                            break;
                        case 5:
//                            this.a(var1.obj);
                            return;
                        case 6:
                            mCamera.startPreview();
                            L.e(TAG,"startPreview");
                            return;
                        case 7:
                            mCamera.stopPreview();
                            break;
                        case 8:
                            mCamera.setPreviewCallbackWithBuffer((Camera.PreviewCallback) var1.obj);
                            break;
                        case 9:
                            mCamera.addCallbackBuffer((byte[]) ((byte[]) var1.obj));
                            break;
                        case 10:
                            mCamera.autoFocus((Camera.AutoFocusCallback) var1.obj);
                            break;
                        case 11:
                            mCamera.cancelAutoFocus();
                            break;
                        case 12:
                            mCamera.setDisplayOrientation(var1.arg1);
                            break;
                        case 13:
                            mCamera.setZoomChangeListener((Camera.OnZoomChangeListener) var1.obj);
                            break;
                        case 14:
                            mCamera.setErrorCallback((Camera.ErrorCallback) var1.obj);
                            break;
                        case 15:
//                            b.this.e = null;
                            try {
                                mCamera.setParameters((Camera.Parameters) var1.obj);
                            } catch (RuntimeException var7) {
                                var7.printStackTrace();
                            }
                            break;
                        case 16:
                            parameters = mCamera.getParameters();
                            break;
                        case 17:
                            try {
                                mCamera.setParameters((Camera.Parameters) var1.obj);
                            } catch (RuntimeException var5) {
                                L.e(TAG, "Camera set parameters failed");
                            }

                            return;
                        case 18:
                            break;
                        case 19:
                            try {
                                L.e(TAG,"startPreview");
                                mCamera.setPreviewDisplay((SurfaceHolder) var1.obj);
                                return;
                            } catch (IOException var6) {
                                throw new RuntimeException(var6);
                            }
                        case 20:
                            mCamera.setPreviewCallback((Camera.PreviewCallback) var1.obj);
                            break;
                        case 21:
                            try {
                                mCamera.setPreviewDisplay((SurfaceHolder) var1.obj);
                                break;
                            } catch (IOException var8) {
                                throw new RuntimeException(var8);
                            }
                        case 22:
                            mCamera.startPreview();
                            break;
                        case 23:
                            try {
                                L.e(TAG,"setPreviewTexture");
                                mCamera.setPreviewTexture((SurfaceTexture) var1.obj);
                            } catch (IOException var3) {
                                throw new RuntimeException(var3);
                            }
                            break;
                        default:
                            throw new RuntimeException("Invalid CameraProxy message=" + var1.what);
                    }
                } catch (RuntimeException var10) {
                    if (var1.what != 1 && mCamera != null) {
                        try {
                            mCamera.release();
                        } catch (Exception var4) {
                            Log.e("CameraManager", "Fail to release the camera.");
                        }

                        mCamera = null;

                    }
                }
            }else {
                L.e(TAG, "handleMessage  null");
            }
        }
    }

    ;


}
