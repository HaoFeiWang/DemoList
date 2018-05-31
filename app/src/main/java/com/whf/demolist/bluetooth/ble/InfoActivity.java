package com.whf.demolist.bluetooth.ble;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.whf.demolist.R;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class InfoActivity extends AppCompatActivity {

    private static final String TAG = Constants.TAG + InfoActivity.class;

    private TextView tvState;
    private TextView tvName;
    private TextView tvAddress;
    private TextView tvRssi;

    private Button btnConnect;
    private Button btnDisconnect;

    private BluetoothDevice bluetoothDevice;
    private BluetoothGattCallback bluetoothGattCallback;
    private BluetoothGatt bluetoothGatt;

    private Handler handler;
    private ExecutorService executorService;

    private boolean readingRssi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        Intent intent = getIntent();
        bluetoothDevice = intent.getParcelableExtra(Constants.REMOTE_BLUETOOTH);

        handler = new Handler();

        initView();
        initListener();
    }

    private void initView() {
        btnConnect = findViewById(R.id.btn_connect);
        btnDisconnect = findViewById(R.id.btn_disconnect);

        tvName = findViewById(R.id.tv_name);
        tvAddress = findViewById(R.id.tv_address);
        tvState = findViewById(R.id.tv_state);
        tvRssi = findViewById(R.id.tv_rssi);

        tvName.setText("名称：" + bluetoothDevice.getName());
        tvAddress.setText("地址：" + bluetoothDevice.getAddress());
        tvState.setText("状态：未连接");
        tvRssi.setText("信号：未知");

        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connect();
            }
        });

        btnDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disconnect();
            }
        });
    }

    private void initListener() {
        //该监听的回调在非UI线程
        bluetoothGattCallback = new BluetoothGattCallback() {
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                super.onConnectionStateChange(gatt, status, newState);

                if (status != BluetoothGatt.GATT_SUCCESS) {
                    Log.e(TAG, "gatt fail");
                    return;
                }

                executorReadRssiThread();
                switch (newState) {
                    case BluetoothGatt.STATE_CONNECTING:
                        //并未发现此状态
                        Log.d(TAG, "正在连接");
                        updateStateText("状态：正在连接");
                        break;
                    case BluetoothGatt.STATE_CONNECTED:
                        Log.d(TAG, "连接成功");
                        updateStateText("状态：已连接");
                        break;
                    case BluetoothGatt.STATE_DISCONNECTING:
                        //并未发现此状态
                        Log.d(TAG, "正在断开连接");
                        updateStateText("状态：正在断开连接");
                        break;
                    case BluetoothGatt.STATE_DISCONNECTED:
                        Log.d(TAG, "断开连接");
                        updateStateText("状态：未连接");
                        break;
                }
            }

            @Override
            public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
                Log.d(TAG, "read remote rssi = " + rssi);
                updateRssiText(rssi);
            }
        };
    }


    private void connect() {
        //直接连接远端设备 (false) 或 远端设备 becomes available 时自动连接 (true)，
        //不能在下面紧接着执行bluetoothGatt的相关操作，否则无效果，例如紧接着读取rssi值会没有回调
        bluetoothGatt = bluetoothDevice.connectGatt(this,
                false, bluetoothGattCallback);
    }


    private void disconnect() {
        if (bluetoothGatt == null) {
            Log.d(TAG, "not connected!");
            return;
        }
        bluetoothGatt.disconnect();
    }

    private void executorReadRssiThread() {
        if (readingRssi){
            return;
        }
        if (executorService == null) {
            executorService = Executors.newSingleThreadExecutor();
        }
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                readRssi();
            }
        });
    }

    private void readRssi() {
        readingRssi = true;
        while (true) {
            //断开连接后会获取rssi值会没有回调
            bluetoothGatt.readRemoteRssi();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateStateText(String msg) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                tvState.setText(msg);
            }
        });
    }

    private void updateRssiText(int value) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                tvRssi.setText("信号：" + value);
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (executorService != null) {
            executorService.shutdownNow();
        }
        super.onDestroy();
    }
}
