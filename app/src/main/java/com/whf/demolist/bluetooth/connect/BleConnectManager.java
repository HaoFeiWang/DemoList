package com.whf.demolist.bluetooth.connect;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
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
import android.support.v4.util.ArraySet;
import android.support.v4.util.ArrayMap;
import android.util.Log;

import com.whf.demolist.bluetooth.ad.BleManager;

import java.util.ArrayList;

/**
 * Ble一对多连接管理
 * Created by @author WangHaoFei on 2018/11/1.
 */

@SuppressWarnings("WeakerAccess")
public class BleConnectManager {
    private static final String TAG = "BLE_TEST_" + BleManager.class.getSimpleName();

    private static final int ADVERTISING_TIME = 120 * 1000;
    private static final int MANUFACTURE_ID = 26;
    private static final byte[] MANUFACTURE_DATA = new byte[]{31};

    private static final ParcelUuid UUID = ParcelUuid.fromString("00001373-0000-1000-8000-00805F9B34FB");

    private static final int IDLE = 0x0000;
    private static final int PENDING_SCAN = 0x0001;
    private static final int SCANNING = 0x0002;
    private static final int PENDING_ADVERTISE = 0x0010;
    private static final int ADVERTISING = 0x0020;
    private static final int PENDING_CONNECT = 0x0100;

    private static BleConnectManager instance;

    private int state = IDLE;

    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;

    private BluetoothLeScanner scanner;
    private ScanSettings scanSetting;
    private ScanCallback scanCallback;
    private ArrayList<ScanFilter> scanFilter;

    private AdvertiseData adData;
    private AdvertiseSettings adSetting;
    private AdvertiseCallback adCallback;
    private BluetoothLeAdvertiser advertiser;

    private ArrayMap<String, BluetoothDevice> pendingConnectDevice;
    private ArrayMap<String, BluetoothGatt> connectedDevice;

    private BleConnectManager.BleBroadCastReceiver bleBroadCastReceiver;

    public static synchronized BleConnectManager getInstance(Context context) {
        if (instance == null) {
            instance = new BleConnectManager(context);
        }
        instance.registerBroadCast(context);
        return instance;
    }

    private BleConnectManager(Context context) {
        this.bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        this.pendingConnectDevice = new ArrayMap<>();
        this.connectedDevice = new ArrayMap<>();
    }

    public void startAdvertise() {
        if ((state | ADVERTISING) == ADVERTISING) {
            return;
        }

        state |= PENDING_ADVERTISE;
        if (checkBluetoothDisable(true)) {
            return;
        }

        initAdvertiser();
        initAdSetting();
        initAdData();
        initAdCallback();
        state |= ADVERTISING;
        Log.d(TAG, "start advertise!");
        advertiser.startAdvertising(adSetting, adData, adCallback);
    }

    private void initAdvertiser() {
        if (advertiser == null) {
            advertiser = bluetoothAdapter.getBluetoothLeAdvertiser();
        }
    }

    private void initAdSetting() {
        if (adSetting != null) {
            return;
        }

        adSetting = new AdvertiseSettings.Builder()
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED)
                .setConnectable(true)
                .setTimeout(ADVERTISING_TIME)
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM)
                .build();
    }

    private void initAdData() {
        if (adData != null) {
            return;
        }

        adData = new AdvertiseData.Builder()
                .addManufacturerData(MANUFACTURE_ID, MANUFACTURE_DATA)
                .setIncludeTxPowerLevel(true)
                .setIncludeDeviceName(true)
                .build();
    }

    private void initAdCallback() {
        if (adCallback != null) {
            return;
        }

        adCallback = new AdvertiseCallback() {
            @Override
            public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                super.onStartSuccess(settingsInEffect);
            }

            @Override
            public void onStartFailure(int errorCode) {
                super.onStartFailure(errorCode);
            }
        };
    }

    public void stopAdvertise() {
        if (checkBluetoothDisable(false)) {
            return;
        }

        if (advertiser != null && adCallback != null) {
            state |= ~(PENDING_ADVERTISE | ADVERTISING);
            advertiser.stopAdvertising(adCallback);
        }
    }

    public void startScan() {
        if ((state & SCANNING) == SCANNING) {
            return;
        }

        state |= PENDING_SCAN;
        if (checkBluetoothDisable(true)) {
            return;
        }

        initScanner();
        iniScanFilter();
        initScanCallback();
        initScanSetting();
        state |= SCANNING;
        Log.d(TAG, "start scan!");
        scanner.startScan(scanFilter, scanSetting, scanCallback);
    }

    private void initScanner() {
        if (scanner == null) {
            scanner = bluetoothAdapter.getBluetoothLeScanner();
        }
    }

    private void iniScanFilter() {
        if (scanFilter != null) {
            return;
        }

        ArrayList<ScanFilter> scanFilterList = new ArrayList<>();
        ScanFilter.Builder filterBuild = new ScanFilter.Builder()
                .setManufacturerData(MANUFACTURE_ID, MANUFACTURE_DATA);
        scanFilterList.add(filterBuild.build());
        scanFilter = scanFilterList;
    }

    private void initScanSetting() {
        if (scanSetting != null) {
            return;
        }
        scanSetting = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_BALANCED)
                .build();
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
                //1：重复开启扫描
                Log.d(TAG, "scan failed = " + errorCode);
            }
        };
    }

    public void stopScan() {
        if (checkBluetoothDisable(false)) {
            return;
        }

        if (scanner != null && scanCallback != null) {
            state |= ~(PENDING_SCAN | SCANNING);
            scanner.stopScan(scanCallback);
        }
    }

    private boolean checkBluetoothDisable(boolean autoOpen) {
        return !checkBluetoothDeviceEnable() || !checkBluetoothOpen(autoOpen);
    }

    private void scanResult(ScanResult result) {
        BluetoothDevice device = result.getDevice();
        String address = device.getAddress();
        if (!connectedDevice.containsKey(address)) {
            pendingConnectDevice.put(address, device);
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
            bleBroadCastReceiver = new BleConnectManager.BleBroadCastReceiver();
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
        }
        if (state == PENDING_ADVERTISE) {
            startAdvertise();
        }
    }

    private void bluetoothStateOff() {
        Log.d(TAG, "bluetooth state off");
        state = IDLE;
    }

    public void release(Context context) {
        stopScan();
        stopAdvertise();
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
