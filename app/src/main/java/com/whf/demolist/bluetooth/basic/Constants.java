package com.whf.demolist.bluetooth.basic;

import java.util.UUID;

/**
 * Created by @author WangHaoFei on 2018/5/30.
 */

public interface Constants {
    String TAG = "Bluetooth_";

    /**
     * 蓝牙设备
     */
    String REMOTE_BLUETOOTH = "remote_bluetooth";

    UUID UUID_SERVICE = UUID.fromString("00001354-0000-1000-8000-00805f9b34fb");
    UUID UUID_CHARACTERISTIC = UUID.fromString("00001355-0000-1000-8000-00805f9b34fb");
    UUID UUID_DESCRIPTOR = UUID.fromString("00001356-0000-1000-8000-00805f9b34fb");
}
