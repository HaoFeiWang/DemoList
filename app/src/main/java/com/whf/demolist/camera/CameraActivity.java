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
        setCameraParameters();
        startPreview(holder);
        startAutoFocus();
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
                            Log.d(TAG, "Current Thread = " + Thread.currentThread());
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

    private void setCameraParameters() {
        Camera.Parameters parameters = camera.getParameters();

        setSceneMode(parameters);
        setFocusMode(parameters);

        try {
            camera.setParameters(parameters);
        } catch (Exception e) {
            Log.e(TAG, "Set Camera Parameters Error = " + e.toString());
        }

        Log.d(TAG, "Current Scene Mode = " + parameters.getSceneMode());
        Log.d(TAG, "Current Focus Mode = " + parameters.getFocusMode());

        setCameraOrientation();
    }

    /**
     * 设置场景模式
     */
    private void setSceneMode(Camera.Parameters parameters) {
        List<String> sceneModeList = parameters.getSupportedSceneModes();

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


}
