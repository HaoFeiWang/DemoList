package com.whf.demolist.bluetooth.ble;

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
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.whf.demolist.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 低功耗Ble蓝牙
 */
public class ClientActivity extends AppCompatActivity {

    private static final String TAG = Constants.TAG + ClientActivity.class;

    private Button btnScan;
    private ListView lvBtInfo;

    private Map<String, BluetoothDevice> bluetoothMap;
    private ArrayAdapter<String> arrayAdapter;

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner bluetoothLeScanner;
    private ScanCallback scanCallback;
    private BroadcastReceiver bluetoothStateReceiver;
    private BluetoothManager bluetoothManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        initView();
        initData();
        initReceiver();
    }

    private void initView() {
        btnScan = findViewById(R.id.btn_scan);
        lvBtInfo = findViewById(R.id.lv_bluetooth_info);

        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startScanService();
            }
        });

        lvBtInfo.setOnItemClickListener((parent, view, position, id) -> {
            stopScan();
            String info = (String) ((TextView) view).getText();
            Intent intent = new Intent(ClientActivity.this, InfoActivity.class);
            BluetoothDevice bluetoothDevice = bluetoothMap.get(info);
            intent.putExtra(Constants.REMOTE_BLUETOOTH, bluetoothDevice);
            startActivity(intent);
        });
    }

    private void initData() {
        bluetoothMap = new HashMap<>();
        arrayAdapter = new ArrayAdapter<>(this, R.layout.item_bluetooth, new ArrayList<>());
        lvBtInfo.setAdapter(arrayAdapter);

        bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        if (bluetoothManager != null) {
            bluetoothAdapter = bluetoothManager.getAdapter();
        }

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

    private void initReceiver() {
        bluetoothStateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF);
                bluetoothStateChange(state);
            }
        };

        IntentFilter intentFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        this.registerReceiver(bluetoothStateReceiver,intentFilter);
    }

    private void bluetoothStateChange(int state) {
        switch (state) {
            case BluetoothAdapter.STATE_ON:
                scanBluetooth();
                break;
        }
    }

    private void scanBluetooth() {
        if (bluetoothAdapter == null){
            Log.e(TAG,"bluetooth not enable!");
            return;
        }

        if (bluetoothLeScanner == null){
            bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
        }

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

    /**
     * 扫描周围的蓝牙设备，该操作为异步操作的，但是会消耗大量资源，一般扫描时长为12秒，建议找到需要的设备后，执行取消扫描
     */
    private void startScanService() {
        if (bluetoothManager == null) {
            bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        }

        if (bluetoothManager != null && bluetoothAdapter == null) {
            bluetoothAdapter = bluetoothManager.getAdapter();
        }

        if (bluetoothAdapter != null && bluetoothLeScanner == null) {
            bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
        }
    }

    /**
     * 结束扫描
     */
    private void stopScan() {
        Log.i(TAG, "停止扫描");
        bluetoothLeScanner.stopScan(scanCallback);
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
            Log.i(TAG, "name = " + bluetoothDevice.getName() + "; address = " + bluetoothDevice.getAddress());
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

}
