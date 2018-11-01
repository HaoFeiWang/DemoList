package com.whf.demolist.bluetooth.ad;


import android.support.v4.util.ArrayMap;
import android.util.Log;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 摇动设备管理
 * Created by @author WangHaoFei on 2018/10/31.
 */

@SuppressWarnings("WeakerAccess")
public class DeviceManager {

    private static final String TAG = "BLE_TEST_" + DeviceManager.class.getSimpleName();

    private static DeviceManager instance;

    private ConcurrentHashMap<String, ShakeDevice> staticDeviceMap;
    private ConcurrentHashMap<String, ShakeDevice> shakingDeviceMap;
    private ArrayMap<String, ShakingListener> shakingListenerMap;

    private DeviceManager() {
        shakingDeviceMap = new ConcurrentHashMap<>();
        staticDeviceMap = new ConcurrentHashMap<>();
        shakingListenerMap = new ArrayMap<>();
    }

    public synchronized static DeviceManager getInstance() {
        if (instance == null) {
            instance = new DeviceManager();
        }
        return instance;
    }

    public void listenerShaking(String address) {
        ShakingListener shakingListener = shakingListenerMap.get(address);
        if (shakingListener == null) {
            shakingListener = new ShakingListener(address);
            shakingListenerMap.put(address, shakingListener);
            shakingListener.start();
        }
        if (shakingListener.isAlive()) {
            shakingListener.interrupt();
        }
        shakingListener.startShaking();
    }

    public void addShakingDevice(String address, String content) {
        ShakeDevice shakeDevice = shakingDeviceMap.get(address) == null ?
                staticDeviceMap.get(address) : null;

        if (shakeDevice == null) {
            shakeDevice = new ShakeDevice();
        }

        //TODO  更新数据
        staticDeviceMap.remove(address);
        shakingDeviceMap.put(address, shakeDevice);
    }

    public void addStaticDevice(String address, String content) {
        ShakeDevice shakeDevice = shakingDeviceMap.get(address) == null ?
                staticDeviceMap.get(address) : null;

        if (shakeDevice == null) {
            shakeDevice = new ShakeDevice();
        }

        //TODO 更新数据
        shakingDeviceMap.remove(address);
        staticDeviceMap.put(address, shakeDevice);
    }

    public void stopShakingDevice(String address) {
        ShakingListener stopShakeListener = shakingListenerMap.get(address);
        if (stopShakeListener != null) {
            stopShakeListener.stopShaking();
        }
    }

    private void shakingDeviceStopped(String address) {
        ShakeDevice device = shakingDeviceMap.get(address);
        if (device != null) {
            shakingDeviceMap.remove(address);
            staticDeviceMap.put(address, device);
        }
    }

    public int getShakingDeviceCount() {
        return shakingDeviceMap.size();
    }

    public void release() {
        for (String address : shakingListenerMap.keySet()) {
            ShakingListener shakingListener = shakingListenerMap.get(address);
            shakingListener.release();
            shakingListener.interrupt();
        }

        staticDeviceMap.clear();
        shakingDeviceMap.clear();
        shakingListenerMap.clear();
    }

    public static class ShakingListener extends Thread {
        private String address;

        private volatile boolean released;
        private volatile boolean shakingStopped;

        public ShakingListener(String address) {
            this.address = address;
        }

        @Override
        public void run() {
            timer();
            Log.d(TAG, "release listener!");
        }

        private synchronized void timer() {
            while (!released) {
                try {
                    TimeUnit.SECONDS.sleep(5);
                    if (!shakingStopped) {
                        DeviceManager.getInstance().shakingDeviceStopped(address);
                    }
                    wait();
                } catch (InterruptedException e) {
                    //no-op
                }
            }
        }

        public void stopShaking() {
            this.shakingStopped = true;
        }

        public void startShaking() {
            this.shakingStopped = false;
        }

        public void release() {
            released = true;
        }
    }
}
