package com.whf.demolist.video

import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.MediaController
import android.widget.VideoView
import com.whf.demolist.R

class VideoViewActivity : AppCompatActivity() {

    private val TAG = "Video_Log"

    private lateinit var videoView: VideoView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_view)

        initView()
        initData()
    }

    private fun initView() {
        videoView = findViewById(R.id.video_view)
    }

    private fun initData() {
        val controller = MediaController(this)
        videoView.setMediaController(controller)

        val url = intent.getStringExtra(INTENT_URL)
        videoView.setVideoURI(Uri.parse(url))
        videoView.start()
    }
}
