package com.whf.demolist.camera;

import android.content.res.Configuration;
import android.hardware.Camera;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.whf.demolist.R;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("deprecation")
public class CameraActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    private static final String TAG = CameraActivity.class.getSimpleName();

    private SurfaceView surfaceView;
    private Camera camera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        surfaceView = findViewById(R.id.surface_view);

        //获取SurfaceHolder,并为其设置回调
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        openCamera();
    }

    /**
     * 开始对焦
     */
    private void startAutoFocus() {
        camera.autoFocus(new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                if (success) {
                    Log.d(TAG, "Auto Focus Success!");
                    camera.setPreviewCallback(new Camera.PreviewCallback() {
                        @Override
                        public void onPreviewFrame(byte[] data, Camera camera) {
                            //Log.d(TAG, "Current Thread = " + Thread.currentThread());
                        }
                    });
                } else {
                    startAutoFocus();
                }
            }
        });
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d(TAG, "surface width = " + width + " height = " + height);
        Camera.Parameters parameters = camera.getParameters();

        Camera.Size previewSize = parameters.getPreviewSize();
        Log.i(TAG, "preview size = " + previewSize.width + "," + previewSize.height);

        List<Camera.Size> previewSizeList = parameters.getSupportedPreviewSizes();
        for (Camera.Size size : previewSizeList) {
            Log.i(TAG, "support preview size = " + size.width + "," + size.height);
        }

        //反转List,从小到大排列
        Collections.reverse(previewSizeList);

        //设置相机预览尺寸比例和surface的尺寸比例相同
        int finalWidth = 0;
        int finalHeight = 0;
        float surfaceProportion = height / (float) width;
        float difference = Integer.MAX_VALUE;
        for (Camera.Size size : previewSizeList) {
            float proportion = size.width / (float) size.height;
            float curDiff = Math.abs(proportion - surfaceProportion);

            if (Math.abs(proportion - surfaceProportion) < difference) {
                difference = curDiff;
                finalWidth = size.width;
                finalHeight = size.height;
            }
        }

        Log.d(TAG, "final width = " + finalWidth + " height = " + finalHeight);
        parameters.setPreviewSize(finalWidth, finalHeight);

        setSceneMode(parameters);
        setFocusMode(parameters);
        Log.d(TAG, "Current Scene Mode = " + parameters.getSceneMode());
        Log.d(TAG, "Current Focus Mode = " + parameters.getFocusMode());

        //预览尺寸必须在开启预览前设置否则会崩溃
        camera.setParameters(parameters);

        setCameraOrientation();
        startPreview(holder);
        startAutoFocus();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    private void openCamera() {
        //获取摄像头的个数
        int number = Camera.getNumberOfCameras();
        Log.d(TAG, "Camera Number = " + number);

        //打开摄像头(一般0为后置摄像头，1为前置摄像头)
        camera = Camera.open();
    }

    private void startPreview(SurfaceHolder holder) {
        //开启预览
        if (camera != null) {
            try {
                camera.setPreviewDisplay(holder);
                camera.startPreview();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 设置摄像头方向，默认情况下摄像头的方向为手机左侧
     */
    private void setCameraOrientation() {
        //获取屏幕方向
        int orientation = this.getResources().getConfiguration().orientation;

        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            // 竖屏
            camera.setDisplayOrientation(90);
        } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // 横屏
            camera.setDisplayOrientation(180);
        }
    }

    /**
     * 设置场景模式
     */
    private void setSceneMode(Camera.Parameters parameters) {
        List<String> sceneModeList = parameters.getSupportedSceneModes();
        if (sceneModeList == null) {
            parameters.setSceneMode(Camera.Parameters.SCENE_MODE_AUTO);
            return;
        }

        for (String sceneMode : sceneModeList) {
            Log.d(TAG, "Support Scene Mode = " + sceneMode);
            if (Camera.Parameters.SCENE_MODE_BARCODE.equals(sceneMode)) {
                parameters.setSceneMode(Camera.Parameters.SCENE_MODE_BARCODE);
                break;
            }
        }
    }

    /**
     * 设置对焦模式
     */
    private void setFocusMode(Camera.Parameters parameters) {
        List<String> focusModeList = parameters.getSupportedFocusModes();

        for (String focusMode : focusModeList) {
            Log.d(TAG, "Support Focus Mode = " + focusMode);
            if (Camera.Parameters.FOCUS_MODE_AUTO.equals(focusMode)) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                break;
            }
        }
    }

    //设置预览帧监听，用于图像识别等，方案三选一
    private void setPreviewCallback(){
        //方式一：使用此方法注册预览回调接口，onPreviewFrame()方法会一直被调用，直到camera preview销毁
        camera.setPreviewCallback(new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] data, Camera camera) {
            }
        });

        //方式二：使用此方法注册预览回调接口时，会将下一帧数据回调给onPreviewFrame()方法，
        //调用完成后这个回调接口将被销毁。也就是只会回调一次预览帧数据。
        camera.setOneShotPreviewCallback(new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] data, Camera camera) {

            }
        });

        //它跟setPreviewCallback的工作方式一样，但是要求指定一个字节数组作为缓冲区，用于预览帧数据，
        //这样能够更好的管理预览帧数据时使用的内存。它一般搭配addCallbackBuffer方法使用。
        //首先分配一块内存作为缓冲区，size的计算方式见第四点中
        byte[] mPreBuffer = new byte[2048];
        camera.addCallbackBuffer(mPreBuffer);
        camera.setPreviewCallbackWithBuffer(new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] data, Camera camera) {

            }
        });
    }

}
