package com.whf.demolist;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.whf.demolist.anim.AnimActivity;
import com.whf.demolist.bluetooth.BluetoothActivity;
import com.whf.demolist.camera.CameraActivity;
import com.whf.demolist.qrcode.QrCodeActivity;
import com.whf.demolist.surfaceview.SurfaceViewActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnAnim;
    private Button btnQrcode;
    private Button btnCamera;
    private Button btnSurface;
    private Button btnBluetooth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initListener();
    }

    private void initView() {
        btnAnim = findViewById(R.id.btn_anim);
        btnQrcode = findViewById(R.id.btn_qrcode);
        btnCamera = findViewById(R.id.btn_camera);
        btnSurface = findViewById(R.id.btn_surface);
        btnBluetooth = findViewById(R.id.btn_bluetooth);
    }

    private void initListener() {
        btnAnim.setOnClickListener(this);
        btnQrcode.setOnClickListener(this);
        btnCamera.setOnClickListener(this);
        btnSurface.setOnClickListener(this);
        btnBluetooth.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_anim:
                startActivity(new Intent(this, AnimActivity.class));
                break;
            case R.id.btn_qrcode:
                startActivity(new Intent(this, QrCodeActivity.class));
                break;
            case R.id.btn_camera:
                startActivity(new Intent(this, CameraActivity.class));
                break;
            case R.id.btn_surface:
                startActivity(new Intent(this, SurfaceViewActivity.class));
                break;
            case R.id.btn_bluetooth:
                startActivity(new Intent(this, BluetoothActivity.class));
                break;
            default:
                break;
        }
    }
}
