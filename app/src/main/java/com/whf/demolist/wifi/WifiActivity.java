package com.whf.demolist.wifi;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.whf.demolist.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WifiActivity extends AppCompatActivity implements View.OnClickListener, WifiReceiver.WifiCallback {

    private static final String TAG = "WIFI_TEST_" + WifiActivity.class.getSimpleName();

    private WifiManager wifiManager;
    private TextView tvChangeWifi;

    private RecyclerView rvWifi;
    private WifiAdapter wifiAdapter;

    private AlertDialog alertDialog;
    private WifiReceiver wifiReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi);

        initView();
        initData();
    }

    private void initView() {
        tvChangeWifi = findViewById(R.id.tv_change_wifi);
        tvChangeWifi.setOnClickListener(this);
        findViewById(R.id.tv_scan_wifi).setOnClickListener(this);
        findViewById(R.id.tv_come_chat).setOnClickListener(this);

        rvWifi = findViewById(R.id.rv_wifi);
        rvWifi.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        wifiAdapter = new WifiAdapter(this, new ArrayList<>());
        wifiAdapter.setOnItemClickListener(new WifiAdapter.OnItemClickListener() {
            @Override
            public void onItemClickListener(String ssid, String type) {
                connectWifi(ssid, type);
            }

            @Override
            public void onRemoveClickListener() {

            }
        });

        rvWifi.setAdapter(wifiAdapter);
    }

    private void connectWifi(String ssid, String type) {
        List<WifiConfiguration> configurationList = wifiManager.getConfiguredNetworks();
        for (int i = 0; i < configurationList.size(); i++) {
            WifiConfiguration wifiConfiguration = configurationList.get(i);
            if (wifiConfiguration.SSID.equals("\"" + ssid + "\"")) {
                //必须断开已连接的Wifi
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                if (wifiInfo != null) {
                    Log.d(TAG, "current connected wifi = " + wifiInfo.getSSID());
                    if (wifiInfo.getSSID().equals("\"" + ssid + "\"")) {
                        return;
                    }
                    wifiManager.disableNetwork(wifiInfo.getNetworkId());
                }

                boolean enableResult = wifiManager.enableNetwork(wifiConfiguration.networkId, true);
                wifiManager.reconnect();
                Log.d(TAG, "wifi configuration contains " + ssid + " enable result = " + enableResult);
                return;
            }
        }

        if (type.equals("wep") || type.equals("wpa")) {
            View view = LayoutInflater.from(this).inflate(R.layout.layout_wifi_dialog, null);
            EditText tvPassword = view.findViewById(R.id.tv_password);
            view.findViewById(R.id.tv_ok).setOnClickListener(v -> {
                String password = tvPassword.getText().toString();
                if (alertDialog != null) {
                    alertDialog.cancel();
                }

                connect(ssid, type, password);
            });

            alertDialog = new AlertDialog.Builder(this)
                    .setView(view)
                    .show();
        } else {
            connect(ssid, type, "");
        }
    }

    private void connect(String ssid, String type, String password) {
        WifiConfiguration wifiConfiguration = createConfiguration(ssid, type, password);
        int networkId = wifiManager.addNetwork(wifiConfiguration);
        Log.d(TAG, "connect network id = " + networkId);
        if (networkId > 0) {
            wifiManager.enableNetwork(networkId, true);
        }
    }

    private void initData() {
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        intentFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        wifiReceiver = new WifiReceiver();
        wifiReceiver.setWifiCallback(this);
        registerReceiver(wifiReceiver, intentFilter);
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
            case R.id.tv_come_chat:
                startActivity(new Intent(this, WifiChatActivity.class));
                finish();
                break;
        }
    }

    private void changeWifi() {
        int state = wifiManager.getWifiState();
        logWifiState("current ", state);

        if (state == WifiManager.WIFI_STATE_ENABLED) {
            Log.d(TAG, "close wifi!");
            wifiManager.setWifiEnabled(false);
        } else if (state == WifiManager.WIFI_STATE_DISABLED) {
            Log.d(TAG, "open wifi!");
            wifiManager.setWifiEnabled(true);
        } else if (state == WifiManager.WIFI_STATE_DISABLING) {
            Log.d(TAG, "wifi is disabling!");
        } else if (state == WifiManager.WIFI_STATE_ENABLING) {
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

    @Override
    public void onStateChanged() {
        int wifiState = wifiManager.getWifiState();
        logWifiState("receiver ", wifiState);
        switch (wifiState) {
            case WifiManager.WIFI_STATE_DISABLED:
                tvChangeWifi.setText("打开WIFI");
                break;
            case WifiManager.WIFI_STATE_DISABLING:
                tvChangeWifi.setText("正在关闭");
                break;
            case WifiManager.WIFI_STATE_ENABLED:
                tvChangeWifi.setText("关闭WIFI");
                break;
            case WifiManager.WIFI_STATE_ENABLING:
                tvChangeWifi.setText("正在打开");
                break;
            case WifiManager.WIFI_STATE_UNKNOWN:
                tvChangeWifi.setText("状态异常");
                break;
        }
    }

    @Override
    public void onScanResult() {
        Log.d(TAG, "receiver scan result!");
        List<ScanResult> scanResultList = wifiManager.getScanResults();
        wifiAdapter.setWifiList(filterScanResult(scanResultList));
    }

    @Override
    public void onNetworkStateChanged() {
        Log.d(TAG, "on network state changed !");
    }

    @Override
    public void onSupplicantStateChanged() {
        Log.d(TAG, "on supplicant state changed!");
    }

    private void logWifiState(String prefix, int state) {
        switch (state) {
            case WifiManager.WIFI_STATE_DISABLED:
                Log.d(TAG, prefix + "wifi state disabled!");
                break;
            case WifiManager.WIFI_STATE_DISABLING:
                Log.d(TAG, prefix + "wifi state disabling!");
                break;
            case WifiManager.WIFI_STATE_ENABLED:
                Log.d(TAG, prefix + "wifi state enabled!");
                break;
            case WifiManager.WIFI_STATE_ENABLING:
                Log.d(TAG, prefix + "wifi state enabling!");
                break;
            case WifiManager.WIFI_STATE_UNKNOWN:
                Log.d(TAG, prefix + "wifi state unknown!");
                break;
        }
    }

    /**
     * 过滤、排序
     * 同SSID的wifi，滤除掉信号弱的，最后进行排序
     */
    public List<ScanResult> filterScanResult(List<ScanResult> oldResultList) {
        List<ScanResult> newResultList = new ArrayList<>();

        OLD:
        for (int i = 0; i < oldResultList.size(); i++) {
            ScanResult oldScanResult = oldResultList.get(i);

            if (i == 0) {
                newResultList.add(oldScanResult);
                continue;
            }

            for (int j = 0; j < newResultList.size(); j++) {
                ScanResult newScanResult = newResultList.get(j);

                if (newScanResult.SSID.equals(oldScanResult.SSID)) {
                    if (oldScanResult.level > newScanResult.level) {
                        newResultList.set(j, oldScanResult);
                    }
                    continue OLD;
                }

                if (j == (newResultList.size() - 1)) {
                    newResultList.add(oldScanResult);
                    continue OLD;
                }
            }
        }

        Collections.sort(newResultList, (o1, o2) -> {
            if (o1.level < o2.level) {
                return 1;
            } else if (o1.level == o2.level) {
                return 0;
            } else {
                return -1;
            }
        });

        return newResultList;
    }

    /**
     * 创建WifiConfiguration，使用它可以获取到networkId
     */
    public WifiConfiguration createConfiguration(String ssid, String type, String password) {
        WifiConfiguration config = new WifiConfiguration();
        config.SSID = "\"" + ssid + "\"";
        if (type.contains("wep")) {
            //wep加密方式，special handling according to password length is a must for wep
            int i = password.length();
            if (((i == 10 || (i == 26) || (i == 58))) && (password.matches("[0-9A-Fa-f]*"))) {
                config.wepKeys[0] = password;
            } else {
                config.wepKeys[0] = "\"" + password + "\"";
            }
            config.allowedAuthAlgorithms
                    .set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        } else if (type.contains("wpa")) {
            //wpa加密方式
            config.preSharedKey = "\"" + password + "\"";
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        } else {
            //无加密方式
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        }
        return config;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (wifiReceiver != null) {
            unregisterReceiver(wifiReceiver);
        }
    }
}
