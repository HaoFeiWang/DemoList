package com.whf.demolist.bluetooth.ad.control;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.whf.demolist.R;
import com.whf.demolist.bluetooth.ad.BleManager;
import com.whf.demolist.bluetooth.ad.DeviceManager;
import com.whf.demolist.bluetooth.ad.StrategyAdShake;
import com.whf.demolist.bluetooth.ad.StrategyAdStatic;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class SensorActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "BLE_TEST_" + SensorActivity.class.getSimpleName();

    private static final int PERMISSION_CODE = 60;

    private TextView tvContent;
    private ShakeManager shakeManager;
    private BleManager bleManager;
    private Disposable disposable;
    private DeviceManager deviceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);
        checkPermission();
    }

    public void init() {
        tvContent = findViewById(R.id.tv_content);
        findViewById(R.id.btn_start_shake).setOnClickListener(this);
        findViewById(R.id.btn_stop_shake).setOnClickListener(this);
        findViewById(R.id.btn_secondary).setOnClickListener(this);

        shakeManager = ShakeManager.getInstance(this);
        shakeManager.setShakeListener(new ShakeManager.ShakeListener() {
            @Override
            public void onStart() {
                Toast.makeText(SensorActivity.this, "摇动了", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStop() {
                Toast.makeText(SensorActivity.this, "停止了", Toast.LENGTH_SHORT).show();
            }
        });

        bleManager = BleManager.getInstance(this);
        deviceManager = DeviceManager.getInstance();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (shakeManager != null) {
            shakeManager.release();
        }
        if (bleManager != null) {
            bleManager.release(this);
        }
        if (deviceManager != null) {
            deviceManager.release();
        }
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start_shake:
                bleManager.startAdvertise(StrategyAdShake.getInstance());
                break;
            case R.id.btn_stop_shake:
                bleManager.startAdvertise(StrategyAdStatic.getInstance());
                break;
            case R.id.btn_response:
                break;
            case R.id.btn_secondary:
                startScan();
                break;
        }
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.d(TAG, "check self permission");
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_CODE);
            } else {
                init();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                init();
            } else {
                finish();
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void startScan() {
        bleManager.startScan();
        if (disposable == null) {
            updateProgress();
        }
    }

    private void updateProgress() {
        disposable = Observable.interval(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(time -> tvContent.setText(getText()),
                        throwable -> updateProgress());
    }

    private String getText() {
        int count = deviceManager.getShakingDeviceCount();
        Log.d(TAG, "shaking device count = " + count);
        return "设备数：" + count;
    }
}
