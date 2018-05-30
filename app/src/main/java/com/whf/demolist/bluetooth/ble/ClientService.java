package com.whf.demolist.bluetooth.ble;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class ClientService extends Service {

    private static final String TAG = Constants.TAG + ClientService.class;

    public static final String START_TYPE = "start_type";
    public static final int TYPE_SCAN = 1;

    private BluetoothAdapter bluetoothAdapter;
    private ScanCallback scanCallback;
    private BluetoothLeScanner bluetoothLeScanner;
    private BroadcastReceiver bluetoothStateReceiver;
    private BluetoothManager bluetoothManager;

    public ClientService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "create client service!");
        checkVariableNotNull();
    }

    private boolean checkVariableNotNull() {
        if (bluetoothManager == null) {
            bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        }

        if (bluetoothManager != null && bluetoothAdapter == null) {
            bluetoothAdapter = bluetoothManager.getAdapter();
        }

        if (bluetoothAdapter != null && bluetoothLeScanner == null) {
            bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
        }

        return bluetoothAdapter != null && bluetoothLeScanner!=null;
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int startType = intent.getIntExtra(START_TYPE, TYPE_SCAN);
        switch (startType) {
            case TYPE_SCAN:
                scanBluetooth();
                break;
            default:
                break;
        }
        return START_NOT_STICKY;
    }

    private void scanBluetooth() {
        //设置各种过滤条件
        ArrayList<ScanFilter> scanFilterList = new ArrayList<>();
        ScanFilter.Builder filterBuild = new ScanFilter.Builder();
        scanFilterList.add(filterBuild.build());

        //扫描时的设置
        ScanSettings.Builder settingBuild = new ScanSettings.Builder();
        //这种扫描方式占用资源比较高，建议当应用处于前台时使用该模式
        settingBuild.setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY);

        bluetoothLeScanner.startScan(scanFilterList, settingBuild.build(), scanCallback);
    }

    private void initScanCallback() {
        scanCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                BluetoothDevice device = result.getDevice();
                String info = device.getAddress() + "  |  " + device.getName();
                if (bluetoothMap.get(info) == null) {
                    Log.d(TAG, "Scan Result = " + info);
                    bluetoothMap.put(info, device);
                    arrayAdapter.add(info);
                }
            }

            @Override
            public void onBatchScanResults(List<ScanResult> results) {
                super.onBatchScanResults(results);
            }

            @Override
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);
            }
        };
    }


}
