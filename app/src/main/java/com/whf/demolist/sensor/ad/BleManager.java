package com.whf.demolist.sensor.ad;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.ParcelUuid;
import android.util.Log;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

/**
 * 蓝牙管理
 * Created by @author WangHaoFei on 2018/10/25.
 */

public class BleManager {

    private static final String TAG = "BLE_TEST_" + BleManager.class.getSimpleName();

    private static final int MANUFACTURE_ID = 28;
    private static final byte[] MANUFACTURE_DATA = new byte[]{35};

    private static final int IDLE = 0x0000;
    private static final int PENDING_SCAN = 0x0001;
    private static final int SCANNING = 0x0002;
    private static final int PENDING_ADVERTISE = 0x0010;
    private static final int ADVERTISING = 0x0020;

    private static BleManager instance;

    private int state = IDLE;
    private StrategyAd strategy;

    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner scanner;

    private ScanCallback scanCallback;
    private BluetoothLeAdvertiser advertiser;
    private BleBroadCastReceiver bleBroadCastReceiver;

    @SuppressWarnings("WeakerAccess")
    public static synchronized BleManager getInstance(Context context) {
        if (instance == null) {
            instance = new BleManager(context);
        }
        return instance;
    }

    private BleManager(Context context) {
        this.bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        registerBroadCast(context);
    }

    @SuppressWarnings("WeakerAccess")
    public void startAdvertise(StrategyAd strategyAd) {
        if (strategyAd == null) {
            return;
        }

        Log.d(TAG, "state = " + state + " pending strategy = " + strategyAd + " current strategy = " + strategy);
        if ((state & ADVERTISING) == ADVERTISING) {
            if (!strategyAd.equals(strategy)) {
                state &= ~ADVERTISING;
                stopAdvertise(strategy);
            } else {
                return;
            }
        }

        strategy = strategyAd;
        state |= PENDING_ADVERTISE;
        if (checkBluetoothDisable(true)) {
            return;
        }

        initAdvertiser();
        state |= ADVERTISING;
        strategyAd.startAdvertise(advertiser);
    }

    public void stopAdvertise(StrategyAd strategy) {
        if (checkBluetoothDisable(false)) {
            return;
        }

        Log.d(TAG, "advertiser = " + advertiser + " strategy = " + strategy);
        if (advertiser != null && strategy != null) {
            strategy.stopAdvertise(advertiser);
        }
    }

    @SuppressWarnings("WeakerAccess")
    public void startScan() {
        if ((state & SCANNING) == SCANNING) {
            return;
        }

        state |= PENDING_SCAN;
        if (checkBluetoothDisable(true)) {
            return;
        }

        initScanCallback();
        initScanner();
        state |= SCANNING;
        Log.d(TAG, "start scanning!");
        scanner.startScan(createScanFilterList(), createScanSetting(), scanCallback);
    }

    public void stopScan() {
        if (checkBluetoothDisable(false)) {
            return;
        }

        if (scanner != null && scanCallback != null) {
            scanner.stopScan(scanCallback);
        }
    }

    private boolean checkBluetoothDisable(boolean autoOpen) {
        return !checkBluetoothDeviceEnable() || !checkBluetoothOpen(autoOpen);
    }

    private void initAdvertiser() {
        if (advertiser == null) {
            advertiser = bluetoothAdapter.getBluetoothLeAdvertiser();
        }
    }

    private void initScanner() {
        if (scanner == null) {
            scanner = bluetoothAdapter.getBluetoothLeScanner();
        }
    }

    private ScanSettings createScanSetting() {
        return new ScanSettings.Builder()
                //balance：500ms   lowLatency：尽可能快
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build();
    }

    private ArrayList<ScanFilter> createScanFilterList() {
        ArrayList<ScanFilter> scanFilterList = new ArrayList<>();
        ScanFilter.Builder filterBuild = new ScanFilter.Builder()
                .setManufacturerData(MANUFACTURE_ID, MANUFACTURE_DATA);
        scanFilterList.add(filterBuild.build());
        return scanFilterList;
    }

    private void initScanCallback() {
        if (scanCallback != null) {
            return;
        }

        scanCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                Log.d(TAG, "scan success call type = " + callbackType + " result = " + result.toString());
                scanResult(result);
            }

            @Override
            public void onScanFailed(int errorCode) {
                //3：重复开启扫描
                Log.d(TAG, "scan failed = " + errorCode);
            }
        };
    }

    private void scanResult(ScanResult result) {
        ScanRecord scanRecord = result.getScanRecord();
        if (scanRecord == null) {
            return;
        }

        String address = result.getDevice().getAddress();
        Map<ParcelUuid, byte[]> serviceData = scanRecord.getServiceData();
        Set<ParcelUuid> parcelUuidSet = serviceData.keySet();
        for (ParcelUuid uuid : parcelUuidSet) {
            StrategyAd strategyAd = AdStrategyClassify.checkStrategy(uuid);
            if (strategyAd != null) {
                strategyAd.parseAdvertise(address, serviceData.get(uuid));
            }
        }
    }

    private boolean checkBluetoothOpen(boolean autoOpen) {
        if (bluetoothAdapter.isEnabled()) {
            return true;
        } else if (autoOpen) {
            Log.d(TAG, "open bluetooth!");
            bluetoothAdapter.enable();
        }

        return false;
    }

    private boolean checkBluetoothDeviceEnable() {
        if (bluetoothManager == null) {
            return false;
        }
        if (bluetoothAdapter == null) {
            bluetoothAdapter = bluetoothManager.getAdapter();
        }

        return bluetoothAdapter != null;
    }

    private void registerBroadCast(Context context) {
        if (bleBroadCastReceiver == null) {
            bleBroadCastReceiver = new BleBroadCastReceiver();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
            context.getApplicationContext().registerReceiver(bleBroadCastReceiver, intentFilter);
        }
    }

    private void unregisterBroadCast(Context context) {
        if (bleBroadCastReceiver != null) {
            context.getApplicationContext().unregisterReceiver(bleBroadCastReceiver);
            bleBroadCastReceiver = null;
        }
    }

    private void bluetoothStateChange(Intent intent) {
        switch (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0)) {
            case BluetoothAdapter.STATE_ON:
                bluetoothStateOn();
                break;
            case BluetoothAdapter.STATE_OFF:
                bluetoothStateOff();
                break;
            case BluetoothAdapter.STATE_TURNING_ON:
                break;
            case BluetoothAdapter.STATE_TURNING_OFF:
                break;
        }
    }

    private void bluetoothStateOn() {
        Log.d(TAG, "bluetooth state on!");
        if (state == PENDING_SCAN) {
            startScan();
        } else if (state == PENDING_ADVERTISE) {
            startAdvertise(strategy);
        }
    }

    private void bluetoothStateOff() {
        Log.d(TAG, "bluetooth state off");
        state = IDLE;
    }

    public void release(Context context) {
        stopScan();
        stopAdvertise(strategy);
        unregisterBroadCast(context);
        state = IDLE;
    }

    class BleBroadCastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                bluetoothStateChange(intent);
            }
        }
    }
}
