package com.whf.demolist.sensor;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.ParcelUuid;
import android.util.Log;

import java.util.ArrayList;
import java.util.UUID;

/**
 * 蓝牙管理
 * Created by @author WangHaoFei on 2018/10/25.
 */

public class BleManager {

    private static final String TAG = "BLE_TEST_" + BleManager.class.getSimpleName();

    private int state;
    private static final int IDLE = 0x00000000;
    private static final int PENDING_SCAN = 0x00000001;
    private static final int PENDING_BROADCAST = 0x00000010;

    private static final UUID UUID_SERVICE = UUID.fromString("00001354-0000-1000-8000-00805f9b34fb");
    private static final UUID UUID_CHARACTERISTIC = UUID.fromString("00001355-0000-1000-8000-00805f9b34fb");
    private static final UUID UUID_DESCRIPTOR = UUID.fromString("00001356-0000-1000-8000-00805f9b34fb");


    private static BleManager instance;

    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner scanner;

    private ScanCallback scanCallback;
    private AdvertiseCallback advertiseCallback;
    private BluetoothLeAdvertiser advertiser;
    private BleBroadCastReceiver bleBroadCastReceiver;

    public static synchronized BleManager getInstance(Context context) {
        if (instance == null) {
            instance = new BleManager(context);
        }
        return instance;
    }

    private BleManager(Context context) {
        bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
    }

    public void startAdvertising(Context context) {
        if (checkBluetoothDisable(context)) {
            return;
        }

        initAdvertiseCallback();
        initAdvertiser();
        advertiser.startAdvertising(buildAdvertiseSettings(), buildAdvertiseData(), advertiseCallback);
    }

    public void startScan(Context context) {
        if (checkBluetoothDisable(context)) {
            return;
        }

        initScanCallback();
        initScanner();
        scanner.startScan(createScanFilterList(), createScanSetting(), scanCallback);
    }

    private boolean checkBluetoothDisable(Context context) {
        return !checkBluetoothDeviceEnable() || !checkBluetoothOpen(context);
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
                .setScanMode(ScanSettings.SCAN_MODE_BALANCED)
                .build();
    }

    private ArrayList<ScanFilter> createScanFilterList() {
        ArrayList<ScanFilter> scanFilterList = new ArrayList<>();
        ScanFilter.Builder filterBuild = new ScanFilter.Builder()
                .setManufacturerData(3469, new byte[]{56});
        scanFilterList.add(filterBuild.build());
        return scanFilterList;
    }

    private void initAdvertiseCallback() {
        if (advertiseCallback != null) {
            return;
        }

        advertiseCallback = new AdvertiseCallback() {
            @Override
            public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                Log.d(TAG, "advertise success!");
            }

            @Override
            public void onStartFailure(int errorCode) {
                Log.d(TAG, "advertise fail!");
            }
        };
    }

    private void initScanCallback() {
        if (scanCallback != null) {
            return;
        }

        scanCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                Log.d(TAG, "call type = " + callbackType + " result = " + result.toString());
            }

            @Override
            public void onScanFailed(int errorCode) {
                Log.d(TAG, "on scan failed = " + errorCode);
            }
        };
    }

    private boolean checkBluetoothOpen(Context context) {
        if (bluetoothAdapter.isEnabled()) {
            return true;
        } else {
            registerBroadCast(context);
            bluetoothAdapter.enable();
            state |= PENDING_SCAN;
            return false;
        }
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

    private AdvertiseSettings buildAdvertiseSettings() {
        AdvertiseSettings.Builder builder = new AdvertiseSettings.Builder()
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED)
                .setConnectable(false)
                .setTimeout(180 * 1000)
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM);

        return builder.build();
    }

    private AdvertiseData buildAdvertiseData() {
        AdvertiseData.Builder dataBuilder = new AdvertiseData.Builder()
                .addManufacturerData(3469, new byte[]{56}) //制造商数据
                .setIncludeTxPowerLevel(true)
                .setIncludeDeviceName(true)
                .addServiceUuid(new ParcelUuid(UUID_SERVICE))
                .addServiceData(new ParcelUuid(UUID_SERVICE), new byte[]{33, 33, 33, 33});

        return dataBuilder.build();
    }

    private void registerBroadCast(Context context) {
        if (bleBroadCastReceiver == null) {
            bleBroadCastReceiver = new BleBroadCastReceiver();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
            context.getApplicationContext().registerReceiver(bleBroadCastReceiver, intentFilter);
        }
    }

    private void unrigisterBroadCast(Context context) {
        if (bleBroadCastReceiver != null) {
            context.getApplicationContext().unregisterReceiver(bleBroadCastReceiver);
            bleBroadCastReceiver = null;
        }
    }

    class BleBroadCastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {

            }
        }
    }
}
