package com.whf.demolist.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.whf.demolist.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 低功耗Ble蓝牙
 */
public class BluetoothActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnIsSupportBle;
    private Button btnStartBluetooth;
    private Button btnScanBinding;
    private Button btnScan;

    private ListView lvBtInfo;
    private Map<String, BluetoothDevice> bluetoothMap;
    private ArrayAdapter<String> arrayAdapter;

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner bluetoothLeScanner;
    private ScanCallback scanCallback;
    private BluetoothAdapter.LeScanCallback leScanCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        initView();
        initCallBack();
    }

    private void initView() {
        btnIsSupportBle = findViewById(R.id.btn_is_support_ble);
        btnStartBluetooth = findViewById(R.id.btn_start_bluetooth);
        btnScanBinding = findViewById(R.id.btn_scan_binding);
        btnScan = findViewById(R.id.btn_scan);

        btnIsSupportBle.setOnClickListener(this);
        btnStartBluetooth.setOnClickListener(this);
        btnScanBinding.setOnClickListener(this);
        btnScan.setOnClickListener(this);


        lvBtInfo = findViewById(R.id.lv_bluetooth_info);
        bluetoothMap = new HashMap<>();
        arrayAdapter = new ArrayAdapter<>(this, R.layout.item_bluetooth, new ArrayList<>());
        lvBtInfo.setAdapter(arrayAdapter);
        lvBtInfo.setOnItemClickListener((parent, view, position, id) -> {
            stopScap();
            String info = (String) ((TextView) view).getText();
            Intent intent = new Intent(BluetoothActivity.this, InfoActivity.class);
            BluetoothDevice bluetoothDevice = bluetoothMap.get(info);
            intent.putExtra("bluetooth", bluetoothDevice);
            startActivity(intent);
        });
    }

    /**
     * 扫描结果回调
     */
    private void initCallBack() {
        scanCallback = new ScanCallback() {

            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                super.onScanResult(callbackType, result);
                BluetoothDevice device = result.getDevice();
                String info = device.getName() + " / " + device.getAddress();
                Log.i("whf bluetooth", info);

                if (bluetoothMap.get(info) == null) {
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

        //5.0以下 旧版API
        leScanCallback = (device, rssi, scanRecord) -> {

        };
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_is_support_ble:
                isSupportBle();
                break;
            case R.id.btn_start_bluetooth:
                startBluetooth();
                break;
            case R.id.btn_scan_binding:
                scanBinding();
                break;
            case R.id.btn_scan:
                scan();
                break;
            default:
                break;
        }
    }

    /**
     * 判断设备是否支持低功耗Ble蓝牙
     */
    private void isSupportBle() {
        //设备大于Android 4.3才支持低功耗Ble蓝牙
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                Toast.makeText(this, "该设备支持低功耗蓝牙", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "该设备不支持低功耗蓝牙", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "该设备不支持低功耗蓝牙", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 开启蓝牙
     */
    private void startBluetooth() {
//        BluetoothManager bluetoothManager =
//                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
//        bluetoothAdapter = bluetoothManager.getAdapter();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "该设备不支持蓝牙", Toast.LENGTH_SHORT).show();
            return;
        }

        if (bluetoothAdapter.isEnabled()) {
            Toast.makeText(this, "蓝牙已启动", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "准备开启蓝牙", Toast.LENGTH_SHORT).show();
            bluetoothAdapter.enable();
        }
    }

    /**
     * 扫描已绑定的设备
     */
    private void scanBinding() {
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "蓝牙未启动", Toast.LENGTH_SHORT).show();
            return;
        }
        StringBuilder info = new StringBuilder();
        Set<BluetoothDevice> deviceSet = bluetoothAdapter.getBondedDevices();
        for (BluetoothDevice bluetoothDevice : deviceSet) {
            info.append("name = " + bluetoothDevice.getName() + "; ");
            info.append("address = " + bluetoothDevice.getAddress() + "\n");
            Log.i("whf bluetooth", "name = " + bluetoothDevice.getName() + "; address = " + bluetoothDevice.getAddress());
        }
    }

    /**
     * 扫描周围的蓝牙设备，该操作为异步操作的，但是会消耗大量资源，一般扫描时长为12秒，建议找到需要的设备后，执行取消扫描
     */
    private void scan() {
        Log.i("whf bluetooth", "开始扫描");
        //5.0以上Ble蓝牙的全新API
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();

            //设置各种过滤条件
            ArrayList<ScanFilter> scanFilterList = new ArrayList<>();
            ScanFilter.Builder filterBuild = new ScanFilter.Builder();
            scanFilterList.add(filterBuild.build());

            //扫描时的设置
            ScanSettings.Builder settingBuild = new ScanSettings.Builder();
            //这种扫描方式占用资源比较高，建议当应用处于前台时使用该模式
            settingBuild.setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY);

            bluetoothLeScanner.startScan(scanFilterList, settingBuild.build(), scanCallback);
        } else {
            //5.0以下旧版API
            bluetoothAdapter.startLeScan(leScanCallback);
        }

        new Handler().postDelayed(this::stopScap, 45000);
    }

    /**
     * 结束扫描
     */
    private void stopScap() {
        Log.i("whf bluetooth", "停止扫描");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            bluetoothLeScanner.stopScan(scanCallback);
        } else {
            bluetoothAdapter.stopLeScan(leScanCallback);
        }
    }


}
