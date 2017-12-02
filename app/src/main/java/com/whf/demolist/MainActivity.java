package com.whf.demolist;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.whf.demolist.anim.AnimActivity;
import com.whf.demolist.qrcode.QrCodeActivity;
import com.whf.demolist.unittest.UnitTestActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnUnitTest;
    private Button btnAnim;
    private Button btnQrcode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initListener();
    }

    private void initView() {
        btnUnitTest = findViewById(R.id.btn_unit_test);
        btnAnim = findViewById(R.id.btn_anim);
        btnQrcode = findViewById(R.id.btn_qrcode);
    }

    private void initListener() {
        btnUnitTest.setOnClickListener(this);
        btnAnim.setOnClickListener(this);
        btnQrcode.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_unit_test:
                startActivity(new Intent(this, UnitTestActivity.class));
                break;
            case R.id.btn_anim:
                startActivity(new Intent(this, AnimActivity.class));
                break;
            case R.id.btn_qrcode:
                startActivity(new Intent(this, QrCodeActivity.class));
                break;
            default:
                break;
        }
    }
}
