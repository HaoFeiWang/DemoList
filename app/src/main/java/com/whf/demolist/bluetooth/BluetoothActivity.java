package com.whf.demolist.bluetooth;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.whf.demolist.R;
import com.whf.demolist.bluetooth.ad.control.SensorActivity;
import com.whf.demolist.bluetooth.basic.BleClientActivity;
import com.whf.demolist.bluetooth.connect.BleConnectActivity;

public class BluetoothActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int PERMISSION_CODE = 60;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        checkPermission();
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_CODE);
            } else {
                initView();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initView();
            } else {
                finish();
            }
        }
    }

    private void initView() {
        findViewById(R.id.btn_basic).setOnClickListener(this);
        findViewById(R.id.btn_ad).setOnClickListener(this);
        findViewById(R.id.btn_connect).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_basic:
                startActivity(new Intent(this, BleClientActivity.class));
                break;
            case R.id.btn_ad:
                startActivity(new Intent(this, SensorActivity.class));
                break;
            case R.id.btn_connect:
                startActivity(new Intent(this, BleConnectActivity.class));
                break;
        }
    }
}
