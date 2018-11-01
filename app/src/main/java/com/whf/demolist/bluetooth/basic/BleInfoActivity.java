package com.whf.demolist.bluetooth.basic;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.whf.demolist.R;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BleInfoActivity extends AppCompatActivity {

    private static final String TAG = Constants.TAG + BleInfoActivity.class;

    private TextView tvState;
    private TextView tvName;
    private TextView tvAddress;
    private TextView tvRssi;

    private Button btnConnect;
    private Button btnDisconnect;
    private Button btnWrite;
    private Button btnRead;

    private EditText edtContent;

    private BluetoothManager bluetoothManager;
    private BluetoothDevice bluetoothDevice;
    private BluetoothGattCallback bluetoothGattCallback;
    private BluetoothGatt bluetoothGatt;

    private BluetoothGattService bluetoothGattService;
    private BluetoothGattCharacteristic bluetoothGattCharacteristic;
    private BluetoothGattDescriptor bluetoothGattDescriptor;

    private Handler handler;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        Intent intent = getIntent();
        bluetoothDevice = intent.getParcelableExtra(Constants.REMOTE_BLUETOOTH);
        bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);

        handler = new Handler();

        initView();
        initListener();
    }

    private void initView() {
        btnConnect = findViewById(R.id.btn_connect);
        btnDisconnect = findViewById(R.id.btn_disconnect);
        btnWrite = findViewById(R.id.btn_write);
        btnRead = findViewById(R.id.btn_read);

        tvName = findViewById(R.id.tv_name);
        tvAddress = findViewById(R.id.tv_address);
        tvState = findViewById(R.id.tv_state);
        tvRssi = findViewById(R.id.tv_rssi);

        edtContent = findViewById(R.id.edt_content);

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

        btnWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeData();
            }
        });

        btnRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readData();
            }
        });
    }

    private void initListener() {
        //该监听的回调在非UI线程
        bluetoothGattCallback = new BluetoothGattCallback() {
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                Log.d(TAG, "BluetoothGattCallback onConnectionStateChange status = " + status + " newState = " + newState);
                if (status == 133) {
                    bluetoothGatt.close();
                    bluetoothGatt = null;
                    return;
                }

//                executorReadRssiThread();
                switch (newState) {
                    case BluetoothGatt.STATE_CONNECTING:
                        //并未发现此状态
                        Log.d(TAG, "正在连接");
                        updateStateText("状态：正在连接");
                        break;
                    case BluetoothGatt.STATE_CONNECTED:
                        Log.d(TAG, "连接成功");
                        updateStateText("状态：已连接");
                        executorReadRssiThread();
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
             * Generic Access（Generic Attribute Profile 通用属性规范GATT）
             * service：00001801-0000-1000-8000-00805f9b34fb
             * characteristic：00002a05-0000-1000-8000-00805f9b34fb
             *
             * Generic Attribute (Generic Access Profile 通用接入规范GAP)
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
                        UUID bluetoothGattServiceUuid = bluetoothGattService.getUuid();
                        Log.d(TAG, "bluetoothGattService uuid = " + bluetoothGattService.getUuid());
                        if (Constants.UUID_SERVICE.equals(bluetoothGattServiceUuid)) {
                            BleInfoActivity.this.bluetoothGattService = bluetoothGattService;
                        }

                        //获取该服务中的所有特征
                        List<BluetoothGattCharacteristic> bluetoothGattCharacteristicList = bluetoothGattService.getCharacteristics();
                        for (BluetoothGattCharacteristic bluetoothGattCharacteristic : bluetoothGattCharacteristicList) {
                            UUID bluetoothGattCharacteristicUuid = bluetoothGattCharacteristic.getUuid();
                            Log.d(TAG, "bluetoothGattCharacteristic uuid = " + bluetoothGattCharacteristicUuid);
                            if (Constants.UUID_CHARACTERISTIC.equals(bluetoothGattCharacteristicUuid)) {
                                BleInfoActivity.this.bluetoothGattCharacteristic = bluetoothGattCharacteristic;
                            }

                            //获取该特征中的所有描述
                            List<BluetoothGattDescriptor> bluetoothGattDescriptorList = bluetoothGattCharacteristic.getDescriptors();
                            for (BluetoothGattDescriptor bluetoothGattDescriptor : bluetoothGattDescriptorList) {
                                UUID bluetoothGattDescriptorUuid = bluetoothGattDescriptor.getUuid();
                                Log.d(TAG, "bluetoothGattDescriptor uuid = " + bluetoothGattDescriptorUuid);
                                if (Constants.UUID_DESCRIPTOR.equals(bluetoothGattDescriptorUuid)) {
                                    BleInfoActivity.this.bluetoothGattDescriptor = bluetoothGattDescriptor;
                                }
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
            int state = bluetoothManager.getConnectionState(bluetoothDevice, BluetoothGatt.GATT);
            Log.d(TAG, "bluetooth gatt state = " + state);
            if (state == BluetoothGatt.STATE_DISCONNECTED) {
                bluetoothGatt.connect();
            }
        } else {
            bluetoothGatt = bluetoothDevice.connectGatt(this,
                    false, bluetoothGattCallback);
        }
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

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                readRssi();
            }
        });
    }

    private void readRssi() {
        while (bluetoothManager.getConnectionState(bluetoothDevice, BluetoothGatt.GATT) == BluetoothGatt.STATE_CONNECTED) {
            //断开连接后会获取rssi值会没有回调
            bluetoothGatt.readRemoteRssi();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void readData() {

    }

    private void writeData() {
        String writeContent = edtContent.getText().toString();
        Log.d(TAG, "write data = " + writeContent);
        if (bluetoothGatt != null && bluetoothGattCharacteristic != null) {
            bluetoothGatt.setCharacteristicNotification(bluetoothGattCharacteristic, true);
            bluetoothGattCharacteristic.setValue(writeContent);
            //设置回复形式
            bluetoothGattCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
            bluetoothGatt.writeCharacteristic(bluetoothGattCharacteristic);
        }

//        bluetoothGattDescriptor.setValue(writeContent.getBytes());
//        bluetoothGatt.writeDescriptor(bluetoothGattDescriptor);
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
