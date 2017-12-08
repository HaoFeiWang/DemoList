package com.whf.demolist.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.whf.demolist.R;

import java.util.Iterator;
import java.util.Set;

public class BluetoothActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_ENABLE_BT = 1;

    private Button btnStartBluetooth;
    private Button btnScanBinding;
    private TextView tvBluetoothSetInfo;

    private BluetoothAdapter bluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        initView();
    }

    private void initView() {
        btnStartBluetooth = findViewById(R.id.btn_start_bluetooth);
        btnScanBinding = findViewById(R.id.btn_scan_binding);
        tvBluetoothSetInfo = findViewById(R.id.tv_bluetooth_info);

        btnStartBluetooth.setOnClickListener(this);
        btnScanBinding.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start_bluetooth:
                startBluetooth();
                break;
            case R.id.btn_scan_binding:
                scanBinding();
                break;
            default:
                break;
        }
    }

    private void startBluetooth() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "该设备不支持蓝牙", Toast.LENGTH_SHORT).show();
            return;
        }

        if (bluetoothAdapter.isEnabled()) {
            Toast.makeText(this, "蓝牙已启动", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "蓝牙未启动", Toast.LENGTH_SHORT).show();
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    private void scanBinding() {
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "蓝牙未启动", Toast.LENGTH_SHORT).show();
            return;
        }
        StringBuilder info = new StringBuilder();
        Set<BluetoothDevice> deviceSet = bluetoothAdapter.getBondedDevices();
        Iterator<BluetoothDevice> iterator = deviceSet.iterator();
        while (iterator.hasNext()) {
            BluetoothDevice bluetoothDevice = iterator.next();
            info.append("name = " + bluetoothDevice.getName() + "; ");
            info.append("address = " + bluetoothDevice.getAddress() + "\n");
            Log.i("whf", "name = " + bluetoothDevice.getName() + "; address = " + bluetoothDevice.getAddress());
        }
        tvBluetoothSetInfo.setText(info.toString());
    }
}
