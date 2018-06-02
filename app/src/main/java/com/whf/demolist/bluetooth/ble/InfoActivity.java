package com.whf.demolist.bluetooth.ble;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattService;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.whf.demolist.R;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

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
                Log.d(TAG, "BluetoothGattCallback onConnectionStateChange status = " + status + " newState = " + newState);
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
                        bluetoothGatt.discoverServices();
                        break;
                    case BluetoothGatt.STATE_DISCONNECTING:
                        //并未发现此状态
                        Log.d(TAG, "正在断开连接");
                        updateStateText("状态：正在断开连接");
                        break;
                    case BluetoothGatt.STATE_DISCONNECTED:
                        Log.d(TAG, "断开连接");
                        updateStateText("状态：未连接");
                        bluetoothGatt.close();
                        break;
                }
            }

            @Override
            public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
                Log.d(TAG, "BluetoothGattCallback onReadRemoteRssi status = " + status + " rssi = " + rssi);
                updateRssiText(rssi);
            }

            /**
             * 两个默认服务，所以在自定义的时候应该避开这两个UUID
             * Generic Access（GAP profile）
             * service：00001801-0000-1000-8000-00805f9b34fb
             * characteristic：00002a05-0000-1000-8000-00805f9b34fb
             *
             * Generic Attribute (GATT Profile)
             * service：00001800-0000-1000-8000-00805f9b34fb
             * characteristic：00002a00-0000-1000-8000-00805f9b34fb
             * characteristic：00002a01-0000-1000-8000-00805f9b34fb
             * characteristic：00002aa6-0000-1000-8000-00805f9b34fb
             */
            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                Log.d(TAG, "BluetoothGattCallback onServicesDiscovered status = " + status);
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    //获取所有发现的服务
                    List<BluetoothGattService> bluetoothGattServicesList = gatt.getServices();
                    for (BluetoothGattService bluetoothGattService : bluetoothGattServicesList) {
                        Log.d(TAG, "bluetoothGattService uuid = " + bluetoothGattService.getUuid());
                        //获取该服务中的所有特征
                        List<BluetoothGattCharacteristic> bluetoothGattCharacteristicList = bluetoothGattService.getCharacteristics();
                        for (BluetoothGattCharacteristic bluetoothGattCharacteristic : bluetoothGattCharacteristicList) {
                            Log.d(TAG, "bluetoothGattCharacteristic uuid = " + bluetoothGattCharacteristic.getUuid());
                            //获取该特征中的所有描述
                            List<BluetoothGattDescriptor> bluetoothGattDescriptorList = bluetoothGattCharacteristic.getDescriptors();
                            for (BluetoothGattDescriptor bluetoothGattDescriptor : bluetoothGattDescriptorList) {
                                Log.d(TAG, "bluetoothGattDescriptor uuid = " + bluetoothGattDescriptor.getUuid());
                            }
                        }
                    }
                }
            }

            @Override
            public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                Log.d(TAG, "BluetoothGattCallback onCharacteristicRead status = "
                        + status + " characteristic = " + new String(characteristic.getValue()));
            }

            @Override
            public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                Log.d(TAG, "BluetoothGattCallback onCharacteristicWrite status = "
                        + status + " characteristic = " + new String(characteristic.getValue()));
            }

            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                Log.d(TAG, "BluetoothGattCallback onCharacteristicChanged characteristic = " + new String(characteristic.getValue()));
            }

            @Override
            public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                Log.d(TAG, "BluetoothGattCallback onDescriptorRead status = "
                        + status + " descriptor = " + new String(descriptor.getValue()));
            }

            @Override
            public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                Log.d(TAG, "BluetoothGattCallback onDescriptorWrite status = "
                        + status + " descriptor = " + new String(descriptor.getValue()));
            }

            @Override
            public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
                Log.d(TAG, "BluetoothGattCallback onReliableWriteCompleted status = " + status);
            }

            @Override
            public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
                Log.d(TAG, "BluetoothGattCallback onMtuChanged status = " + status);
            }

            @Override
            public void onPhyUpdate(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
                Log.d(TAG, "BluetoothGattCallback onPhyUpdate status = " + status);
            }

            @Override
            public void onPhyRead(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
                Log.d(TAG, "BluetoothGattCallback onPhyRead status = " + status);
            }
        };
    }


    private void connect() {
        //直接连接远端设备 (false) 或 远端设备 becomes available 时自动连接 (true)，
        //不能在下面紧接着执行bluetoothGatt的相关操作，否则无效果，例如紧接着读取rssi值会没有回调
        //每个设备所能拥有的Gatt连接是有限个数的所以在断开连接时应该关闭释放Gatt连接，否则会出现133错误
        //connect是进行重连，connectGatt是首次连接
        if (bluetoothGatt != null) {
            bluetoothGatt.connect();
        }
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
        if (executorService == null) {
            executorService = Executors.newSingleThreadExecutor();
        }

        //判断当前线程池活动的线程数
        int activeThreadCount = ((ThreadPoolExecutor) executorService).getActiveCount();
        if (activeThreadCount > 0) {
            Log.d(TAG, "read rssi thread is active!");
            return;
        }

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                readRssi();
            }
        });
    }

    private void readRssi() {
        while (bluetoothGatt!=null && bluetoothGatt.getConnectionState(bluetoothDevice) == BluetoothGatt.STATE_CONNECTED) {
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
