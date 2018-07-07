package com.whf.demolist.video

import android.content.Context
import android.content.pm.ActivityInfo
import android.media.MediaPlayer
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.RelativeLayout
import android.widget.TextView
import com.whf.demolist.R

class VideoActivity : AppCompatActivity(), SurfaceHolder.Callback {

    private val TAG = "Video_Log"
    private val DATA_SOURCE = arrayOf(
            "http://baobab.kaiyanapp.com/api/v1/playUrl?vid=110661&resourceType=video&editionType=default&source=aliyun",
            "http://baobab.kaiyanapp.com/api/v1/playUrl?vid=111871&resourceType=video&editionType=default&source=aliyun"
    )

    private lateinit var surfaceView: SurfaceView
    private lateinit var tvLoading: TextView

    private lateinit var surfaceHolder: SurfaceHolder
    private lateinit var mediaPlayer: MediaPlayer

    private var curPlayingUrlIndex = 0

    private var isPause = false
    private var isSurfaceCreated = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContentView(R.layout.activity_video)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        initView()
        initData()
    }

    private fun initView() {
        surfaceView = findViewById(R.id.sf_video_content)
        tvLoading = findViewById(R.id.tv_loading)
    }

    private fun initData() {
        mediaPlayer = MediaPlayer()
        mediaPlayer.setDataSource(DATA_SOURCE[curPlayingUrlIndex])
        mediaPlayer.setOnPreparedListener {
            Log.d(TAG, "media player prepared!")
            tvLoading.visibility = View.GONE
            it.start()
        }

        mediaPlayer.setOnVideoSizeChangedListener { mp, width, height ->
            if (width != 0 && height != 0) {
                Log.d(TAG, "video width = $width height = $height")
                val windowManager = this.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                val displayMetrics = DisplayMetrics()
                windowManager.defaultDisplay.getMetrics(displayMetrics)
                val screenWidth = displayMetrics.widthPixels
                val screenHight = displayMetrics.heightPixels
                Log.d(TAG, "screen width = $width height = $height")

                val layoutParams = surfaceView.layoutParams as RelativeLayout.LayoutParams
                if (resources.configuration.orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                    val scale = width.toFloat() / height
                    val videoHeight = screenWidth / scale
                    layoutParams.width = screenWidth
                    layoutParams.height = videoHeight.toInt()
                } else {
                    layoutParams.width = screenWidth
                    layoutParams.height = screenHight
                }
                surfaceView.layoutParams = layoutParams

            }
        }
    }

    private fun initSurfaceHolder() {
        surfaceHolder = surfaceView.holder
        surfaceHolder.addCallback(this)
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
        Log.d(TAG, "surface changed!")
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        Log.d(TAG, "surface destroy!")
        isSurfaceCreated = false
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        Log.d(TAG, "surface created!")
        isSurfaceCreated = true
        mediaPlayer.setDisplay(surfaceHolder)

        if (isPause) {
            resumePlayVideo()
        } else {
            mediaPlayer.prepareAsync()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }

    override fun onPause() {
        super.onPause()
        if(mediaPlayer.isPlaying){
            isPause = true
            mediaPlayer.pause()
            Log.d(TAG, "pause media player!")
        }
    }

    /**
     * 页面从前台到后台会执行 onPause ->onStop 此时Surface会被销毁，
     * 再一次从后台 到前台时需要 重新创建Surface
     */
    override fun onStart() {
        super.onStart()
        if (!isSurfaceCreated) {
            initSurfaceHolder()
        }
    }

    private fun resumePlayVideo() {
        isPause = false
        mediaPlayer.start()
    }

}
