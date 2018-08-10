package com.whf.demolist.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.whf.demolist.R;

import java.util.List;

public class WIFIActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "WIFI_TEST_" + WIFIActivity.class.getSimpleName();

    private WifiManager wifiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi);

        initView();
        initData();
    }

    private void initView() {
        findViewById(R.id.tv_change_wifi).setOnClickListener(this);
        findViewById(R.id.tv_scan_wifi).setOnClickListener(this);
    }

    private void initData() {
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        registerReceiver(new WifiReceiver(), new IntentFilter());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_change_wifi:
                changeWifi();
                break;
            case R.id.tv_scan_wifi:
                scanWifi();
                break;
        }
    }

    private void changeWifi() {
        int state = wifiManager.getWifiState();
        logWifiState(state);

        if (state == WifiManager.WIFI_STATE_ENABLED) {
            Log.d(TAG, "close wifi!");
            wifiManager.setWifiEnabled(false);
        } else if (state == WifiManager.WIFI_STATE_DISABLED){
            Log.d(TAG, "open wifi!");
            wifiManager.setWifiEnabled(true);
        } else if (state == WifiManager.WIFI_STATE_DISABLING){
            Log.d(TAG, "wifi is disabling!");
        } else if (state == WifiManager.WIFI_STATE_ENABLING){
            Log.d(TAG, "wifi is enabling!");
        }
    }

    private void scanWifi() {
        if (wifiManager.isWifiEnabled()) {
            Log.d(TAG, "start scan!");
            wifiManager.startScan();
        } else {
            Log.d(TAG, "open wifi!");
            wifiManager.setWifiEnabled(true);
        }
    }

    private void logWifiState(int state) {
        switch (state) {
            case WifiManager.WIFI_STATE_DISABLED:
                Log.d(TAG, "wifi state disabled!");
                break;
            case WifiManager.WIFI_STATE_DISABLING:
                Log.d(TAG, "wifi state disabling!");
                break;
            case WifiManager.WIFI_STATE_ENABLED:
                Log.d(TAG, "wifi state enabled!");
                break;
            case WifiManager.WIFI_STATE_ENABLING:
                Log.d(TAG, "wifi state enabling!");
                break;
            case WifiManager.WIFI_STATE_UNKNOWN:
                Log.d(TAG, "wifi state unknown!");
                break;
        }
    }

    private void showScanResult() {
        List<ScanResult> scanResultList = wifiManager.getScanResults();
        for (int i = 0; i < scanResultList.size(); i++) {
            ScanResult scanResult = scanResultList.get(i);
            Log.d(TAG, "Scan Result = " + scanResult);
        }
    }

    class WifiReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == null) {
                Log.d(TAG, "wifi receiver action is null!");
                return;
            }

            switch (intent.getAction()) {
                case WifiManager.WIFI_STATE_CHANGED_ACTION:
                    Log.d(TAG, "wifi state change = " + String.valueOf(wifiManager.getWifiState()));
                    break;
                case WifiManager.SCAN_RESULTS_AVAILABLE_ACTION:
                    showScanResult();
                    break;
            }
        }
    }
}
