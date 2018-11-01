package com.whf.demolist.bluetooth.connect;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;

/**
 * Created by @author WangHaoFei on 2018/11/1.
 */

public class ConnectDevice {

    private BluetoothDevice device;
    private BluetoothGatt gatt;
    private BluetoothGattCallback callback;

    public BluetoothDevice getDevice() {
        return device;
    }

    public void setDevice(BluetoothDevice device) {
        this.device = device;
    }

    public BluetoothGatt getGatt() {
        return gatt;
    }

    public void setGatt(BluetoothGatt gatt) {
        this.gatt = gatt;
    }

    public BluetoothGattCallback getCallback() {
        return callback;
    }

    public void setCallback(BluetoothGattCallback callback) {
        this.callback = callback;
    }

    @Override
    public String toString() {
        return "ConnectDevice{" +
                "device=" + device +
                ", gatt=" + gatt +
                ", callback=" + callback +
                '}';
    }
}
