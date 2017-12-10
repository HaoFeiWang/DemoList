package com.whf.demolist.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.whf.demolist.R;

public class InfoActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvBluetooth;
    private Button btnConnect;

    private BluetoothDevice bluetoothDevice;
    private BluetoothGattCallback bluetoothGattCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        tvBluetooth = findViewById(R.id.tv_bluetooth);
        btnConnect = findViewById(R.id.btn_connect);

        Intent intent = getIntent();
        bluetoothDevice = intent.getParcelableExtra("bluetooth");
        tvBluetooth.setText(bluetoothDevice.getName() + " / " + bluetoothDevice.getAddress());

        btnConnect.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_connect:
                connect();
                break;
        }
    }

    private void connect() {
        bluetoothGattCallback = new BluetoothGattCallback() {
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                super.onConnectionStateChange(gatt, status, newState);

                switch (newState) {
                    case BluetoothGatt.STATE_CONNECTING:
                        Log.i("whf bluetooth", "正在连接");
                        break;
                    case BluetoothGatt.STATE_CONNECTED:
                        Log.i("whf bluetooth", "连接成功");
                        break;
                    case BluetoothGatt.STATE_DISCONNECTING:
                        Log.i("whf bluetooth", "正在断开连接");
                        break;
                    case BluetoothGatt.STATE_DISCONNECTED:
                        Log.i("whf bluetooth", "断开连接");
                        break;
                }
            }
        };

        //直接连接远端设备 (false) 或 远端设备 becomes available 时自动连接 (true).
        bluetoothDevice.connectGatt(this, false, bluetoothGattCallback);
    }
}
