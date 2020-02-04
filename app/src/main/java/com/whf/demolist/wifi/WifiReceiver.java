package com.whf.demolist.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by @author WangHaoFei on 2018/8/13.
 */

public class WifiReceiver extends BroadcastReceiver {

    private static final String TAG = "WIFI_TEST_" + WifiReceiver.class.getSimpleName();

    private WifiCallback wifiCallback;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() == null) {
            Log.e(TAG, "wifi receiver action is null!");
            return;
        }
        if (wifiCallback == null) {
            Log.e(TAG, "wifi callback is null!");
            return;
        }

        switch (intent.getAction()) {
            case WifiManager.WIFI_STATE_CHANGED_ACTION:
                wifiCallback.onStateChanged();
                break;
            case WifiManager.NETWORK_STATE_CHANGED_ACTION:
                NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                NetworkInfo.DetailedState state = info.getDetailedState();
                wifiCallback.onNetworkStateChanged(state);
                break;
            case WifiManager.SUPPLICANT_STATE_CHANGED_ACTION:
                wifiCallback.onSupplicantStateChanged();
                break;
            case WifiManager.SCAN_RESULTS_AVAILABLE_ACTION:
                wifiCallback.onScanResult();
                break;
        }
    }

    public void setWifiCallback(WifiCallback wifiCallback) {
        this.wifiCallback = wifiCallback;
    }

    public interface WifiCallback {
        void onStateChanged();

        void onScanResult();

        void onNetworkStateChanged(NetworkInfo.DetailedState state);

        void onSupplicantStateChanged();
    }
}
