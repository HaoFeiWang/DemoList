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
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.ParcelUuid;
import android.support.v4.util.ArrayMap;
import android.support.v4.util.ArraySet;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

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

    public static final int TYPE_NONE = 0;
    public static final int TYPE_START = 1;
    public static final int TYPE_STOP = 2;
    public static final int TYPE_RESPONSE = 3;

    private static final UUID UUID_START = UUID.fromString("00002363-0000-1000-8000-00805F9B34FB");
    private static final UUID UUID_STOP = UUID.fromString("00002365-0000-1000-8000-00805F9B34FB");
    private static final UUID UUID_RESPONSE = UUID.fromString("00002366-0000-1000-8000-00805F9B34FB");

    private static final int ADVERTISING_TIME = 180 * 1000;

    private static final int AD_COUNT = 9;
    private static final int AD_DATA_LENGTH = 20;
    private static final int AD_HEAD_LENGTH = 2;

    private static BleManager instance;

    private int state = IDLE;
    private int actionType = TYPE_NONE;

    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner scanner;

    private ScanCallback scanCallback;
    private BluetoothLeAdvertiser advertiser;
    private BleBroadCastReceiver bleBroadCastReceiver;

    private AdvertiseSettings adSetting;
    private ArrayMap<AdvertiseData, AdvertiseCallback> startAdMap;
    private ArrayMap<AdvertiseData, AdvertiseCallback> stopAdMap;

    private Thread adListener;

    private ArrayMap<String, byte[]> achieveAdData;
    private ArrayMap<String, String> achieveAdContent;
    private ArrayMap<String, ArraySet<Integer>> achieveAdSegment;

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
        startAdMap = new ArrayMap<>();
        stopAdMap = new ArrayMap<>();
        achieveAdSegment = new ArrayMap<>();
        achieveAdContent = new ArrayMap<>();
        achieveAdData = new ArrayMap<>();
    }

    @SuppressWarnings("WeakerAccess")
    public void startAdvertise(int type) {
        if ((state & ADVERTISING) == ADVERTISING && actionType != type) {
            state &= ~ADVERTISING;
            stopAdvertise(actionType);
        }

        actionType = type;
        state |= PENDING_ADVERTISE;
        if (checkBluetoothDisable(true)) {
            return;
        }

        Log.d(TAG, "start advertise!");
        initAdvertiser();
        initAdSetting();
        initAdData(type);
        emitAdvertise(type);
    }

    private void initAdData(int type) {
        if (type == TYPE_START) {
            if (startAdMap.size() > 0) {
                return;
            }
            buildStartAdData();
            return;
        }

        if (type == TYPE_STOP) {
            stopAdMap.put(buildAdData3(), createAdCallback3());
            return;
        }

        if (type == TYPE_RESPONSE) {
        }
    }

    private void buildStartAdData() {
        // 最大只能有180字节
        String str = "Hello Java! Hello Word! Hello XTC!";
        byte[] content = str.getBytes();
        int length = content.length;
        byte lengthByte = Integer.valueOf(length).byteValue();
        Log.d(TAG, "content " + Arrays.toString(content) + " length = " + length + " byte = " + lengthByte);
        if (content.length > AD_COUNT * AD_DATA_LENGTH) {
            Log.d(TAG, "content length too large!");
            return;
        }
        for (int i = 0, k = 0; i < content.length; i += AD_DATA_LENGTH, k++) {
            byte[] section = new byte[AD_DATA_LENGTH + AD_HEAD_LENGTH];
            section[0] = lengthByte;
            section[1] = Integer.valueOf(k).byteValue();
            System.arraycopy(content, i, section, AD_HEAD_LENGTH, Math.min(content.length - i, AD_DATA_LENGTH));
            Log.d(TAG, "section " + i + " = " + Arrays.toString(section));
            startAdMap.put(buildAdData(section), createAdCallback(k));
        }
    }

    private void emitAdvertise(int type) {
        state |= ADVERTISING;
        if (type == TYPE_START) {
            for (int i = 0; i < startAdMap.size(); i++) {
                AdvertiseData adData = startAdMap.keyAt(i);
                advertiser.startAdvertising(adSetting, adData, startAdMap.get(adData));
            }
            return;
        }
        if (type == TYPE_STOP) {
            for (int i = 0; i < stopAdMap.size(); i++) {
                AdvertiseData adData = stopAdMap.keyAt(i);
                advertiser.startAdvertising(adSetting, adData, stopAdMap.get(adData));
            }
            return;
        }

        if (type == TYPE_RESPONSE) {

        }
    }

    private AdvertiseCallback createAdCallback3() {
        return new AdvertiseCallback() {
            @Override
            public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                Log.d(TAG, "advertise3 success!");
            }

            @Override
            public void onStartFailure(int errorCode) {
                //1：广播的数据太大（超过31个字节）  3：正在广播中
                Log.d(TAG, "advertise3 fail = " + errorCode);
            }
        };
    }

    private AdvertiseCallback createAdCallback2() {
        return new AdvertiseCallback() {
            @Override
            public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                Log.d(TAG, "advertise2 success!");
            }

            @Override
            public void onStartFailure(int errorCode) {
                //1：广播的数据太大（超过31个字节）  3：正在广播中
                Log.d(TAG, "advertise2 fail = " + errorCode);
            }
        };
    }

    private AdvertiseCallback createAdCallback(int i) {
        return new AdvertiseCallback() {
            @Override
            public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                Log.d(TAG, "advertise " + i + " success!");
            }

            @Override
            public void onStartFailure(int errorCode) {
                //1：广播的数据太大（超过31个字节） 2: 超过广播最大数量（最大可以广播9个）  3：正在广播中
                Log.d(TAG, "advertise " + i + " fail = " + errorCode);
            }
        };
    }

    private AdvertiseData buildAdData3() {
        AdvertiseData.Builder dataBuilder = createAdDataBuilder()
                .addServiceData(new ParcelUuid(UUID_STOP), new byte[]{
                        55, 55, 55, 55, 55, 55, 55, 55, 55});

        return dataBuilder.build();
    }

    private AdvertiseData buildAdData(byte[] section) {
        AdvertiseData.Builder dataBuilder = createAdDataBuilder()
                .addServiceData(new ParcelUuid(UUID_START), section);
        return dataBuilder.build();
    }

    private AdvertiseData.Builder createAdDataBuilder() {
        return new AdvertiseData.Builder()
                .addManufacturerData(MANUFACTURE_ID, MANUFACTURE_DATA)
                .setIncludeTxPowerLevel(false)//可以减少一个字节长度
                .setIncludeDeviceName(false);//可以减少一个字节+设备名称的长度
    }

    public void stopAdvertise(int type) {
        if (checkBluetoothDisable(false)) {
            return;
        }

        initAdvertiser();
        if (type == TYPE_START) {
            for (int i = 0; i < startAdMap.size(); i++) {
                AdvertiseData adData = startAdMap.keyAt(i);
                advertiser.stopAdvertising(startAdMap.get(adData));
            }
            return;
        }

        if (type == TYPE_STOP) {
            return;
        }

        if (type == TYPE_RESPONSE) {

        }
    }

    @SuppressWarnings("WeakerAccess")
    public void startScan() {
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

        initScanCallback();
        initScanner();
        scanner.stopScan(scanCallback);
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

        String deviceAddress = result.getDevice().getAddress();
        Map<ParcelUuid, byte[]> serviceData = scanRecord.getServiceData();
        if (serviceData.containsKey(new ParcelUuid(UUID_START))) {
            byte[] serviceDataValue = serviceData.get(new ParcelUuid(UUID_START));
            int length = serviceDataValue[0];
            int position = serviceDataValue[1];

            if (adListener == null) {
                initAdStopListener();
                adListener.start();
            } else {
                adListener.interrupt();
            }

            if (achieveAdData.get(deviceAddress) == null) {
                achieveAdData.put(deviceAddress, new byte[length]);
                achieveAdSegment.put(deviceAddress, new ArraySet<>());
            }

            byte[] adData = achieveAdData.get(deviceAddress);
            ArraySet<Integer> adSegment = achieveAdSegment.get(deviceAddress);

            if (!adSegment.contains(position)) {
                adSegment.add(position);
                int destPos = position * AD_DATA_LENGTH;
                int copyLen = Math.min(adData.length - destPos, AD_DATA_LENGTH);
                System.arraycopy(serviceDataValue, AD_HEAD_LENGTH, adData, destPos, copyLen);
            }

            double segmentNum = Math.ceil(length / (float) AD_DATA_LENGTH);
            if (adSegment.size() == segmentNum && !achieveAdContent.containsKey(deviceAddress)) {
                String content = new String(adData);
                achieveAdContent.put(deviceAddress, content);
            }

        } else if (serviceData.containsKey(new ParcelUuid(UUID_STOP))) {
            Log.d(TAG, "scan result：stop!");
        }
    }

    private void initAdStopListener() {
        adListener = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    TimeUnit.SECONDS.sleep(5);
                    Log.d(TAG, "ad listener stop !");
                } catch (InterruptedException e) {
                    Log.d(TAG, "ad listener interrupted !");
                    run();
                }
            }
        });
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

    private void initAdSetting() {
        if (adSetting != null) {
            return;
        }

        AdvertiseSettings.Builder builder = new AdvertiseSettings.Builder()
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED)
                .setConnectable(false) //可以减少三个字节长度
                .setTimeout(ADVERTISING_TIME)
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM);

        adSetting = builder.build();
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

    private void bluetoothStateChange(Intent intent) {
        switch (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0)) {
            case BluetoothAdapter.STATE_ON:
                bluetoothStateOn();
                break;
            case BluetoothAdapter.STATE_OFF:
                break;
            case BluetoothAdapter.STATE_TURNING_ON:
                break;
            case BluetoothAdapter.STATE_TURNING_OFF:
                break;
        }
    }

    private void bluetoothStateOn() {
        Log.d(TAG, "bluetooth state on, state = " + state);
        if (state == PENDING_SCAN) {
            startScan();
        } else if (state == PENDING_ADVERTISE) {
            startAdvertise(actionType);
        }
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
