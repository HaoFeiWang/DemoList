package com.whf.demolist.bluetooth.ad;

import android.bluetooth.le.BluetoothLeAdvertiser;
import android.os.ParcelUuid;

/**
 * 广播策略接口
 * Created by @author WangHaoFei on 2018/10/29.
 */

public interface StrategyAd {

    ParcelUuid getParcelUuid();

    void startAdvertise(BluetoothLeAdvertiser advertiser);

    void stopAdvertise(BluetoothLeAdvertiser advertiser);

    void parseAdvertise(String address, byte[] content);

    void release();
}
