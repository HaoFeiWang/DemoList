package com.whf.demolist.sensor.ad;


import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.os.ParcelUuid;
import android.support.v4.util.ArrayMap;
import android.util.Log;


import java.util.List;

/**
 * 摇动状态广播策略
 * Created by @author WangHaoFei on 2018/10/31.
 */

public class StrategyAdShake implements StrategyAd {

    private static final String TAG = "BLE_TEST_" + StrategyAdShake.class.getSimpleName();
    private static final int ADVERTISING_TIME = 180 * 1000;

    private static StrategyAdShake instance;

    private AdvertiseSettings adSetting;
    private ArrayMap<String, ParseAdData> achieveAdData;
    private ArrayMap<AdvertiseData, AdvertiseCallback> sourceAdData;

    private StrategyAdShake() {
        sourceAdData = new ArrayMap<>();
        achieveAdData = new ArrayMap<>();
    }

    public synchronized static StrategyAdShake getInstance() {
        if (instance == null) {
            instance = new StrategyAdShake();
        }
        return instance;
    }

    @Override
    public ParcelUuid getParcelUuid() {
        return AdStrategyClassify.UUID_START;
    }

    @Override
    public void startAdvertise(BluetoothLeAdvertiser advertiser) {
        initAdSetting();
        initAdData();
        Log.d(TAG, "shake strategy start advertise!");
        emitAdvertise(advertiser);
    }

    @Override
    public void stopAdvertise(BluetoothLeAdvertiser advertiser) {
        Log.d(TAG,"stop advertise!");
        for (int i = 0; i < sourceAdData.size(); i++) {
            AdvertiseData adData = sourceAdData.keyAt(i);
            Log.d(TAG, "stop ad = " + i);
            advertiser.stopAdvertising(sourceAdData.get(adData));
        }
    }

    @Override
    public void parseAdvertise(String address, byte[] content) {
        DeviceManager.getInstance().listenerShaking(address);
        AdCodec.decode(address, content, achieveAdData);
        ParseAdData parseAdData = achieveAdData.get(address);
        if (parseAdData != null) {
            DeviceManager.getInstance().addShakingDevice(address, parseAdData.getContent());
        }
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
                sourceAdData.put(adData, new ShakeAdvertiseCallback(i));
            }
        }
    }

    private void emitAdvertise(BluetoothLeAdvertiser advertiser) {
        for (int i = 0; i < sourceAdData.size(); i++) {
            AdvertiseData adData = sourceAdData.keyAt(i);
            advertiser.startAdvertising(adSetting, adData, sourceAdData.get(adData));
        }
    }

    static class ShakeAdvertiseCallback extends AdvertiseCallback {
        private int segment;

        ShakeAdvertiseCallback(int segment) {
            this.segment = segment;
        }

        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            Log.d(TAG, "shake strategy advertise " + segment + " success!");
        }

        @Override
        public void onStartFailure(int errorCode) {
            Log.d(TAG, "shake strategy advertise " + segment + " fail = " + errorCode);
        }
    }
}
