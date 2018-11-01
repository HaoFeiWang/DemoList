package com.whf.demolist.bluetooth.ad.control;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

/**
 * 摇一摇
 * Created by @author WangHaoFei on 2018/10/25.
 */
@SuppressWarnings("WeakerAccess")
public class ShakeManager {

    private static final String TAG = "BLE_TEST_" + ShakeManager.class.getSimpleName();

    private static ShakeManager shakeManager;

    private Sensor sensor;
    private ShakeListener shakeListener;
    private SensorManager sensorManager;
    private SensorEventListener sensorEventListener;

    private float preX = 0f;
    private float preY = 0f;
    private float preZ = 0f;
    private int shakeCount = 0;
    private int stackCount = 0;
    private boolean isShaking = false;

    public static synchronized ShakeManager getInstance(Context context) {
        if (shakeManager == null) {
            shakeManager = new ShakeManager(context);
        }

        shakeManager.register();
        return shakeManager;
    }

    private ShakeManager(Context context) {
        initShakeListener();
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
    }

    public void setShakeListener(ShakeListener shakeListener) {
        this.shakeListener = shakeListener;
    }

    private void register() {
        if (sensorManager != null && sensor != null && sensorEventListener != null) {
            sensorManager.registerListener(sensorEventListener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    public void release() {
        if (sensorManager != null) {
            sensorManager.unregisterListener(sensorEventListener);
        }
    }

    private void initShakeListener() {
        sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                    checkShake(event);
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                //no-op
            }
        };
    }

    private void checkShake(SensorEvent event) {

        //三个参数分别为x、y、z轴的加速度，手表静止就可以看出重力加速度的值
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        float sum = Math.abs(x + y + z - (preX + preY + preZ));

        if (sum >= 5) {
            stackCount = 0;
            shakeCount++;
        } else {
            shakeCount = 0;
            stackCount++;
        }

        if (shakeCount >= 5 && !isShaking) {
            Log.d(TAG, "start shake!");
            isShaking = true;
            if (shakeListener != null) {
                shakeListener.onStart();
            }
        }

        if (stackCount >= 15 && isShaking) {
            Log.d(TAG, "stop shake!");
            isShaking = false;
            if (shakeListener != null) {
                shakeListener.onStop();
            }
        }

        preX = x;
        preY = y;
        preZ = z;
    }

    interface ShakeListener {
        void onStart();

        void onStop();
    }

}
