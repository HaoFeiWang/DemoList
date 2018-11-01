package com.whf.demolist.bluetooth.ad;


import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.os.ParcelUuid;
import android.support.v4.util.ArrayMap;
import android.util.Log;

import java.util.List;

/**
 * 静止状态广播策略
 * Created by @author WangHaoFei on 2018/10/31.
 */

public class StrategyAdStatic implements StrategyAd {

    private static final String TAG = "BLE_TEST_" + StrategyAdStatic.class.getSimpleName();
    private static final int ADVERTISING_TIME = 60 * 1000;

    private static StrategyAdStatic instance;

    private AdvertiseSettings adSetting;
    private ArrayMap<String, ParseAdData> achieveAdData;
    private ArrayMap<AdvertiseData, AdvertiseCallback> sourceAdData;

    private StrategyAdStatic() {
        sourceAdData = new ArrayMap<>();
        achieveAdData = new ArrayMap<>();
    }

    public synchronized static StrategyAdStatic getInstance() {
        if (instance == null) {
            instance = new StrategyAdStatic();
        }
        return instance;
    }

    @Override
    public ParcelUuid getParcelUuid() {
        return AdStrategyClassify.UUID_STOP;
    }

    @Override
    public void startAdvertise(BluetoothLeAdvertiser advertiser) {
        initAdSetting();
        initAdData();
        Log.d(TAG, "static strategy start advertise!");
        emitAdvertise(advertiser);
    }

    @Override
    public void stopAdvertise(BluetoothLeAdvertiser advertiser) {
        for (int i = 0; i < sourceAdData.size(); i++) {
            AdvertiseData adData = sourceAdData.keyAt(i);
            advertiser.stopAdvertising(sourceAdData.get(adData));
        }
    }

    @Override
    public void parseAdvertise(String address, byte[] content) {
        AdCodec.decode(address, content, achieveAdData);
        ParseAdData parseAdData = achieveAdData.get(address);
        if (parseAdData != null) {
            DeviceManager.getInstance().stopShakingDevice(address);
            DeviceManager.getInstance().addStaticDevice(address, parseAdData.getContent());
        }
    }

    @Override
    public void release() {
        sourceAdData.clear();
        achieveAdData.clear();
    }

    private void initAdSetting() {
        if (adSetting != null) {
            return;
        }

        adSetting = new AdvertiseSettings.Builder()
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED)
                .setConnectable(false)
                .setTimeout(ADVERTISING_TIME)
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM)
                .build();
    }

    private void initAdData() {
        if (sourceAdData.size() != 0) {
            return;
        }

        String content = "Hello Java! Hello Word! Hello XTC!";
        List<byte[]> contentByte = AdCodec.encode(content);
        if (contentByte != null) {
            for (int i = 0; i < contentByte.size(); i++) {
                AdvertiseData adData = AdCodec.createAdData(getParcelUuid(), contentByte.get(i));
                sourceAdData.put(adData, new StaticAdvertiseCallback(i));
            }
        }
    }

    private void emitAdvertise(BluetoothLeAdvertiser advertiser) {
        for (int i = 0; i < sourceAdData.size(); i++) {
            AdvertiseData adData = sourceAdData.keyAt(i);
            advertiser.startAdvertising(adSetting, adData, sourceAdData.get(adData));
        }
    }

    static class StaticAdvertiseCallback extends AdvertiseCallback {
        private int segment;

        StaticAdvertiseCallback(int segment) {
            this.segment = segment;
        }

        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            Log.d(TAG, "static strategy advertise " + segment + " success!");
        }

        @Override
        public void onStartFailure(int errorCode) {
            Log.d(TAG, "static strategy advertise " + segment + " fail = " + errorCode);
        }
    }
}
