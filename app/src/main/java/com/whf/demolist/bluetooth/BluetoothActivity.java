package com.whf.demolist.bluetooth;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.whf.demolist.R;
import com.whf.demolist.bluetooth.ad.control.SensorActivity;
import com.whf.demolist.bluetooth.basic.BleClientActivity;
import com.whf.demolist.bluetooth.connect.BleConnectActivity;

public class BluetoothActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        initView();
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
