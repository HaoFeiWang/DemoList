package com.whf.demolist.sensor;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.whf.demolist.R;

public class SensorActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "SENSOR_TAG_" + SensorActivity.class.getSimpleName();

    private ShakeManager shakeManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);

        findViewById(R.id.btn_main).setOnClickListener(this);
        findViewById(R.id.btn_secondary).setOnClickListener(this);

        shakeManager = ShakeManager.getInstance(this);
        shakeManager.register();
        shakeManager.setShakeListener(new ShakeManager.ShakeListener() {
            @Override
            public void onStart() {
                Toast.makeText(SensorActivity.this, "摇动了", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStop() {
                Toast.makeText(SensorActivity.this, "停止了", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (shakeManager != null) {
            shakeManager.release();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_main:
                startAdvertising();
                break;
            case R.id.btn_secondary:
                startScan();
                break;
        }
    }

    private void startAdvertising() {

    }

    private void startScan() {

    }


}
