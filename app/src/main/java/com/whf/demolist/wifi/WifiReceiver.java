package com.whf.demolist.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.util.Log;

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
            Log.e(TAG,"wifi callback is null!");
            return;
        }

        switch (intent.getAction()) {
            case WifiManager.WIFI_STATE_CHANGED_ACTION:
                wifiCallback.onStateChanged();
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
    }
}
