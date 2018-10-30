package com.whf.demolist.sensor;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.whf.demolist.R;

public class SensorActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "SENSOR_TAG_" + SensorActivity.class.getSimpleName();

    private static final int PERMISSION_CODE = 60;

    private ShakeManager shakeManager;
    private BleManager bleManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);
        checkPermission();
    }

    public void init() {
        findViewById(R.id.btn_start_shake).setOnClickListener(this);
        findViewById(R.id.btn_stop_shake).setOnClickListener(this);
        findViewById(R.id.btn_secondary).setOnClickListener(this);

        shakeManager = ShakeManager.getInstance(this);
        shakeManager.register();
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (shakeManager != null) {
            shakeManager.release();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start_shake:
                bleManager.startAdvertise(BleManager.TYPE_START);
                break;
            case R.id.btn_stop_shake:
                bleManager.startAdvertise(BleManager.TYPE_STOP);
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
        if (requestCode == PERMISSION_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                init();
            } else {
                finish();
            }
        }
    }

    private void startScan() {
        bleManager.startScan();
    }
}
