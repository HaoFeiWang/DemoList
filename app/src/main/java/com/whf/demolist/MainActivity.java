package com.whf.demolist;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.whf.demolist.unittest.UnitTestActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnUnitTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initListener();
    }

    private void initView() {
        btnUnitTest = findViewById(R.id.btn_unit_test);
    }

    private void initListener() {
        btnUnitTest.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_unit_test:
                startActivity(new Intent(this, UnitTestActivity.class));
                break;
            default:
                break;
        }
    }
}
