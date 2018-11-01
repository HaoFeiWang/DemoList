package com.whf.demolist.bluetooth.connect;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
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
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Ble一对多连接管理
 * Created by @author WangHaoFei on 2018/11/1.
 */

@SuppressWarnings("WeakerAccess")
public class BleConnectManager {
    private static final String TAG = "BLE_TEST_" + BleConnectManager.class.getSimpleName();

    private static final int ADVERTISING_TIME = 120 * 1000;
    private static final int MANUFACTURE_ID = 26;
    private static final byte[] MANUFACTURE_DATA = new byte[]{31};

    @SuppressWarnings("unused")
    private static final ParcelUuid UUID = ParcelUuid.fromString("00001373-0000-1000-8000-00805F9B34FB");

    private static final int IDLE = 0x0000;
    private static final int PENDING_SCAN = 0x0001;
    private static final int SCANNING = 0x0002;
    private static final int PENDING_ADVERTISE = 0x0010;
    private static final int ADVERTISING = 0x0020;

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

    private Context context;
    private ConnectConsumer connectConsumer;
    private ConcurrentHashMap<String, ConnectDevice> pendingConnectDevice;
    private ConcurrentHashMap<String, ConnectDevice> connectedDevice;

    private BleConnectManager.BleBroadCastReceiver bleBroadCastReceiver;

    public static synchronized BleConnectManager getInstance(Context context) {
        if (instance == null) {
            instance = new BleConnectManager(context);
        }
        instance.registerBroadCast(context);
        return instance;
    }

    private BleConnectManager(Context context) {
        this.context = context.getApplicationContext();
        this.bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        this.pendingConnectDevice = new ConcurrentHashMap<>();
        this.connectedDevice = new ConcurrentHashMap<>();
    }

    public void startAdvertise() {
        if ((state & ADVERTISING) == ADVERTISING) {
            Log.d(TAG, "advertising!");
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
                Log.d(TAG, "start ad success!");
            }

            @Override
            public void onStartFailure(int errorCode) {
                Log.d(TAG, "start ad fail, error code = " + errorCode);
            }
        };
    }

    public void stopAdvertise() {
        if (checkBluetoothDisable(false)) {
            return;
        }

        if (advertiser != null && adCallback != null) {
            Log.d(TAG, "stop advertise!");
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
        initScanFilter();
        initScanCallback();
        initScanSetting();
        initConnectConsumer();
        state |= SCANNING;
        Log.d(TAG, "start scan!");
        scanner.startScan(scanFilter, scanSetting, scanCallback);
    }


    private void initScanner() {
        if (scanner == null) {
            scanner = bluetoothAdapter.getBluetoothLeScanner();
        }
    }

    private void initScanFilter() {
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
                //Log.d(TAG, "scan result = " + result.toString());
                connectScanResult(result);
            }

            @Override
            public void onScanFailed(int errorCode) {
                //1：重复开启扫描
                Log.d(TAG, "scan failed = " + errorCode);
            }
        };
    }

    private void initConnectConsumer() {
        if (connectConsumer != null) {
            return;
        }
        connectConsumer = new ConnectConsumer();
        connectConsumer.start();
    }

    public void stopScan() {
        if (checkBluetoothDisable(false)) {
            return;
        }

        if (scanner != null && scanCallback != null) {
            Log.d(TAG, "stop scan!");
            state |= ~(PENDING_SCAN | SCANNING);
            scanner.stopScan(scanCallback);
        }
    }

    private boolean checkBluetoothDisable(boolean autoOpen) {
        return !checkBluetoothDeviceEnable() || !checkBluetoothOpen(autoOpen);
    }

    private void connectScanResult(ScanResult result) {
        BluetoothDevice device = result.getDevice();
        String address = device.getAddress();
        if (!connectedDevice.containsKey(address) && !pendingConnectDevice.containsKey(address)) {
            ConnectDevice connectDevice = new ConnectDevice();
            connectDevice.setDevice(device);
            Log.d(TAG, "put new device = " + device.getAddress());
            pendingConnectDevice.put(address, connectDevice);
            connectConsumer.notifyConnect();
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
            Log.d(TAG, "bluetooth manager is null!");
            return false;
        }
        if (bluetoothAdapter == null) {
            bluetoothAdapter = bluetoothManager.getAdapter();
        }

        boolean enable = bluetoothAdapter != null;
        if (!enable) {
            Log.d(TAG, "bluetooth adapter is null!");
        }

        return enable;
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
        release();
    }

    public void release() {
        stopScan();
        stopAdvertise();
        state = IDLE;

        if (connectConsumer != null) {
            connectConsumer.release();
        }

        for (Map.Entry<String, ConnectDevice> entry : connectedDevice.entrySet()) {
            Log.d(TAG, "release gatt = " + entry.getKey());
            BluetoothGatt gatt = entry.getValue().getGatt();
            gatt.disconnect();
            gatt.close();
        }

        connectedDevice.clear();
        pendingConnectDevice.clear();
        unregisterBroadCast(context);
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

    class ConnectConsumer extends Thread {

        private volatile boolean released;

        @Override
        public void run() {
            while (!released) {
                connect();
            }
        }

        private synchronized void connect() {
            if (pendingConnectDevice.size() > 0) {

                ConnectDevice connectDevice = null;
                //不能使用keySet，因为keySet是1.8才加入的
                Iterator<Map.Entry<String, ConnectDevice>> entryIterator = pendingConnectDevice.entrySet().iterator();
                if (entryIterator.hasNext()) {
                    connectDevice = pendingConnectDevice.remove(entryIterator.next().getKey());
                }

                if (connectDevice == null) {
                    return;
                }
                BluetoothDevice device = connectDevice.getDevice();
                if (device == null) {
                    return;
                }

                if (connectDevice.getCallback() == null && connectDevice.getGatt() == null) {
                    BleGattCallback gattCallback = new BleGattCallback();
                    Log.d(TAG, "start connect device = " + device.getAddress());
                    BluetoothGatt bluetoothGatt = device.connectGatt(context, false, gattCallback);
                    if (bluetoothGatt != null) {
                        connectDevice.setGatt(bluetoothGatt);
                        connectDevice.setCallback(gattCallback);
                        connectedDevice.put(device.getAddress(), connectDevice);
                    }
                } else {
                    Log.d(TAG, "retry connect device = " + device.getAddress());
                    connectDevice.getGatt().connect();
                }
            } else {
                try {
                    wait();
                } catch (InterruptedException e) {
                    //no-op
                }
            }
        }

        public void release() {
            Log.d(TAG, "release consumer!");
            released = true;
            notifyConnect();
        }

        public synchronized void notifyConnect() {
            notifyAll();
        }
    }

    class BleGattCallback extends BluetoothGattCallback {

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            //status
            //GATT_SUCCESS : 0：

            //newState
            //STATE_DISCONNECTED-0；STATE_CONNECTING-1；STATE_CONNECTED-2；STATE_DISCONNECTING-3

            //被连接的蓝牙设备关闭后，会回调status = 19；newState = 0
            //被连接的蓝牙设备关闭后，再调用connectGatt会回调status = 133；newState = 0
            BluetoothDevice device = gatt.getDevice();
            Log.d(TAG, device.getAddress() + " connect state = " + status + " new state = " + newState);
            if (newState == 0) {
                ConnectDevice connectDevice = connectedDevice.remove(device.getAddress());
                if (status == 19 || status == 133) {
                    gatt.disconnect();
                    gatt.close();
                } else {
                    pendingConnectDevice.put(device.getAddress(), connectDevice);
                    connectConsumer.notifyConnect();
                }
            }
        }
    }
}
