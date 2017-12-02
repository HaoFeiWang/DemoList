package com.whf.demolist.qrcode;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.whf.demolist.R;

public class QrCodeActivity extends AppCompatActivity {

    private EditText edtContent;
    private Button btnCreate;
    private Button btnScan;

    private ImageView ivQrcode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code);

        edtContent = findViewById(R.id.edt_content_qrcode);
        btnCreate = findViewById(R.id.btn_create_qrcode);
        btnScan = findViewById(R.id.btn_scan_qrcode);
        ivQrcode = findViewById(R.id.iv_qrcode);

        btnCreate.setOnClickListener(v -> {
            String content = edtContent.getText().toString();
            Bitmap bitmap = QrCodeUtil.createQrCode(content, 150, 150, Environment.getExternalStorageDirectory().toString());
            ivQrcode.setImageBitmap(bitmap);
        });

    }
}
