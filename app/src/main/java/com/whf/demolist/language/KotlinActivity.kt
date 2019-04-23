package com.whf.demolist.language

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.whf.demolist.R

//不需要 findViewById 的导包
import kotlinx.android.synthetic.main.activity_kotlin.*

class KotlinActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kotlin)

        tv_thread.setOnClickListener{}
    }
}

