package com.whf.demolist.bluetooth.connect;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.whf.demolist.R;

public class BleConnectActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble_connect);

        findViewById(R.id.btn_ad).setOnClickListener(this);
        findViewById(R.id.btn_scan).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ad:
                BleConnectManager.getInstance(this).startAdvertise();
                break;
            case R.id.btn_scan:
                BleConnectManager.getInstance(this).startScan();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BleConnectManager.getInstance(this).release();
    }
}
