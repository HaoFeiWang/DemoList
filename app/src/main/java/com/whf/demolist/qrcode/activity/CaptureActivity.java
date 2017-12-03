/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.whf.demolist.qrcode.activity;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.LoginFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.whf.demolist.R;
import com.whf.demolist.qrcode.camera.CameraManager;
import com.whf.demolist.qrcode.decode.CaptureActivityHandler;
import com.whf.demolist.qrcode.decode.DecodeFormatManager;
import com.whf.demolist.qrcode.utils.AmbientLightManager;
import com.whf.demolist.qrcode.utils.BeepManager;
import com.whf.demolist.qrcode.utils.InactivityTimer;

import java.io.IOException;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Hashtable;
import java.util.Map;

/**
 * This activity opens the camera and does the actual scanning on a background thread. It draws a
 * viewfinder to help the user place the barcode correctly, shows feedback as the image processing
 * is happening, and then overlays the results when a scan is successful.
 */
public final class CaptureActivity extends Activity implements SurfaceHolder.Callback {

    private static final String TAG = CaptureActivity.class.getSimpleName();

    private CameraManager cameraManager;
    private CaptureActivityHandler handler;
    private InactivityTimer inactivityTimer;
    private BeepManager beepManager;
//    private AmbientLightManager ambientLightManager;

    private SurfaceView scanPreview;
    private RelativeLayout scanContainer;
    private RelativeLayout scanCropView;
    /**
     * 扫描线
     */
    private ImageView scanLine;
    /**
     * 闪光灯开关
     */
    private TextView mSetTorch;
    /**
     * 从本地读取数据
     */
    private TextView mScanLocalPic;

    private Rect mCropRect = null;

    /**
     * 判断是否开启闪光灯
     */
    private boolean isOpenTorch;

    /**
     * 判断Surface是否绘制中
     */
    private boolean hasSurface;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        //保持屏幕常量
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_capture);

        initView();
        initListener();

        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);
        beepManager = new BeepManager(this);
//        ambientLightManager = new AmbientLightManager(this);
    }

    private void initView() {
        scanPreview = findViewById(R.id.capture_preview);
        scanContainer = findViewById(R.id.capture_container);
        scanCropView = findViewById(R.id.capture_crop_view);
        scanLine = findViewById(R.id.capture_scan_line);

        mSetTorch = findViewById(R.id.btn_open_flashlight);
        mScanLocalPic = findViewById(R.id.btn_scan_local_pic);
    }

    private void initListener() {
        mSetTorch.setOnClickListener(v -> {
            if (isOpenTorch) {
                isOpenTorch = false;
                mSetTorch.setText("打开手电筒");
            } else {
                isOpenTorch = true;
                mSetTorch.setText("关闭手电筒");
            }
            cameraManager.setTorch(isOpenTorch);
        });

        mScanLocalPic.setOnClickListener(this::pickPictureFromAblum);
    }

    @Override
    protected void onStart() {
        super.onStart();
        TranslateAnimation animation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f, Animation
                .RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT,
                0.9f);
        animation.setDuration(2000);
        animation.setRepeatCount(-1);
        animation.setRepeatMode(Animation.RESTART);
        scanLine.startAnimation(animation);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //CameraManager必须在这儿初始化，不可以在onCreate()中。这是必须的，因为打开相机驱动程序去测量屏幕尺寸会有一些尺寸不准的bug
        cameraManager = new CameraManager(getApplication());

        setRequestedOrientation(getCurrentOrientation());
        //横屏
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);

        handler = null;

        beepManager.updatePrefs();
//        ambientLightManager.start(cameraManager);
        inactivityTimer.onResume();
        SurfaceHolder surfaceHolder = scanPreview.getHolder();
        if (hasSurface) {
            //The activity was paused but not stopped, so the surface still exists. Therefore
            //surfaceCreated() won't be called, so init the camera here.
            initCamera(surfaceHolder);
        } else {
            //Install the callback and wait for surfaceCreated() to init the camera.
            surfaceHolder.addCallback(this);
        }
    }

    private int getCurrentOrientation() {
        //0、1、2、3 依次左转90度
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        Log.i("QrCode", "rotation = " + rotation);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            switch (rotation) {
                case Surface.ROTATION_0:
                case Surface.ROTATION_90:
                    return ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                default:
                    return ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
            }
        } else {
            switch (rotation) {
                case Surface.ROTATION_0:
                case Surface.ROTATION_270:
                    return ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                default:
                    return ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
            }
        }
    }

    @Override
    protected void onPause() {
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        inactivityTimer.onPause();
//        ambientLightManager.stop();
        beepManager.close();
        cameraManager.closeDriver();
        if (!hasSurface) {
            scanPreview.getHolder().removeCallback(this);
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (holder == null) {
            Log.e(TAG, "*** WARNING *** surfaceCreated() gave us a null surface!");
        }
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // do nothing
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        if (surfaceHolder == null) {
            throw new IllegalStateException("No SurfaceHolder provided");
        }
        if (cameraManager.isOpen()) {
            Log.w(TAG, "initCamera() while already open -- late SurfaceView callback?");
            return;
        }
        try {
            cameraManager.openDriver(surfaceHolder);
            // Creating the handler starts the preview, which can also throw a RuntimeException.
            if (handler == null) {
                handler = new CaptureActivityHandler(this, null, null, cameraManager);
            }
            initCrop();
        } catch (IOException ioe) {
            Log.w(TAG, ioe);
            displayFrameworkBugMessageAndExit();
        } catch (RuntimeException e) {
            // Barcode Scanner has seen crashes in the wild of this variety:
            // java.?lang.?RuntimeException: Fail to connect to camera service
            Log.w(TAG, "Unexpected error initializing camera", e);
            displayFrameworkBugMessageAndExit();
        }
    }

    /**
     * 初始化截取的矩形区域
     */
    private void initCrop() {
        int cameraHeight = cameraManager.getBestPreviewSize().x;
        int cameraWidth = cameraManager.getBestPreviewSize().y;

        // 获取布局中扫描框的位置和宽高
        int[] location = new int[2];
        scanCropView.getLocationInWindow(location);

        int cropLeft = location[0];
        int cropTop = location[1] - getStatusBarHeight();

        int cropWidth = scanCropView.getWidth();
        int cropHeight = scanCropView.getHeight();

        // 获取布局容器的宽高
        int containerWidth = scanContainer.getWidth();
        int containerHeight = scanContainer.getHeight();

        //计算最终截取的矩形的左上角顶点x坐标
        int x = cropLeft * cameraWidth / containerWidth;
        //计算最终截取的矩形的左上角顶点y坐标
        int y = cropTop * cameraHeight / containerHeight;

        //计算最终截取的矩形的宽度
        int width = cropWidth * cameraWidth / containerWidth;
        //计算最终截取的矩形的高度
        int height = cropHeight * cameraHeight / containerHeight;

        //生成最终的截取的矩形
        mCropRect = new Rect(x, y, width + x, height + y);
    }

    public Rect getCropRect() {
        return mCropRect;
    }

    private int getStatusBarHeight() {
        try {
            Rect frame = new Rect();
            getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
            return frame.top;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }


    /**
     * 扫描本地图片上的二维码
     *
     * @param v
     */
    public void pickPictureFromAblum(View v) {
        Intent mIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(mIntent, 1);
    }

    /*
     * @see android.app.Activity#onActivityResult(int, int,
     * android.content.Intent) 对相册获取的结果进行分析
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1:
                    //Uri: content://media/external/images/media/371773
                    Uri selectedImage = data.getData();
                    Log.i("QrCode","select image uri = "+selectedImage);
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContentResolver().query(selectedImage,
                            filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    cursor.close();

                    Result resultString = scanImageQR(picturePath);
                    if (resultString == null) {
                        Toast.makeText(getApplicationContext(), "扫描失败", Toast.LENGTH_LONG).show();
                    } else {
                        //扫描成功
                        handleDecode(resultString);
                    }
                    break;
                default:
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 解析QR图内容
     */
    private Result scanImageQR(String picturePath) {

        if (TextUtils.isEmpty(picturePath)) {
            return null;
        }

        Collection<BarcodeFormat> decodeFormats = EnumSet.noneOf(BarcodeFormat.class);
        decodeFormats.addAll(DecodeFormatManager.PRODUCT_FORMATS);
        decodeFormats.addAll(DecodeFormatManager.INDUSTRIAL_FORMATS);
        decodeFormats.addAll(DecodeFormatManager.QR_CODE_FORMATS);
        decodeFormats.addAll(DecodeFormatManager.DATA_MATRIX_FORMATS);
        decodeFormats.addAll(DecodeFormatManager.AZTEC_FORMATS);
        decodeFormats.addAll(DecodeFormatManager.PDF417_FORMATS);

        Map<DecodeHintType, Object> hints = new Hashtable<DecodeHintType, Object>();
        hints.put(DecodeHintType.CHARACTER_SET, "utf-8");
        hints.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats);

        // 获得待解析的图片
        Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        //核心
        RGBLuminanceSource source = new RGBLuminanceSource(width, height, pixels);
        BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
        QRCodeReader reader = new QRCodeReader();
        Result result;

        try {
            result = reader.decode(bitmap1, hints);
            return result;
        } catch (NotFoundException | FormatException | ChecksumException e) {
            Toast.makeText(CaptureActivity.this, "扫描失败",
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        return null;
    }


    /**
     * A valid barcode has been found, so give an indication of success and show the results.
     * 处理解码
     */
    public void handleDecode(Result rawResult) {
        inactivityTimer.onActivity();
        beepManager.playBeepSoundAndVibrate();
        Bundle bundle = new Bundle();
        Intent resultIntent = new Intent();
        bundle.putInt("width", mCropRect.width());
        bundle.putInt("height", mCropRect.height());
        bundle.putString("result", rawResult.getText());
        Log.i("QrCode", "result = " + rawResult.getText());
        resultIntent.putExtras(bundle);
        this.setResult(RESULT_OK, resultIntent);
        CaptureActivity.this.finish();
    }


    private void displayFrameworkBugMessageAndExit() {
        // camera error
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.app_name));
        builder.setMessage("Camera error");
        builder.setPositiveButton("OK", (dialog, which) -> finish());
        builder.setOnCancelListener(dialog -> finish());
        builder.show();
    }

    public Handler getHandler() {
        return handler;
    }

    public CameraManager getCameraManager() {
        return cameraManager;
    }

}
