package com.whf.demolist.video

import android.content.Intent
import android.media.MediaMetadataRetriever
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import com.whf.demolist.R
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class VideoInfoActivity : AppCompatActivity() {

    private val TAG = "Video_Log"
    private val START_TIME = (0.1*1000*1000L).toLong()

    private val DATA_SOURCE = arrayOf(
            "http://baobab.kaiyanapp.com/api/v1/playUrl?vid=110661&resourceType=video&editionType=default&source=aliyun",
            "http://baobab.kaiyanapp.com/api/v1/playUrl?vid=111871&resourceType=video&editionType=default&source=aliyun"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_info)

        val ivVideoPreviewOne = findViewById<ImageView>(R.id.iv_video_one)
        val tvVideoInfoOne = findViewById<TextView>(R.id.tv_video_info_one)

        val ivVideoPreviewTwo = findViewById<ImageView>(R.id.iv_video_two)
        val tvVideoInfoTwo = findViewById<TextView>(R.id.tv_video_info_two)

        syncVideoMetaData(DATA_SOURCE[0]).subscribe {
            ivVideoPreviewOne.setImageBitmap(it.previewImage)
            tvVideoInfoOne.text = it.toString()
        }

        syncVideoMetaData(DATA_SOURCE[1]).subscribe{
            ivVideoPreviewTwo.setImageBitmap(it.previewImage)
            tvVideoInfoTwo.text = it.toString()
        }

        ivVideoPreviewOne.setOnClickListener {
            val intent = Intent(this,VideoActivity::class.java)
            intent.putExtra(INTENT_URL,DATA_SOURCE[0])
            startActivity(intent)
        }

        ivVideoPreviewTwo.setOnClickListener {
            val intent = Intent(this,VideoActivity::class.java)
            intent.putExtra(INTENT_URL,DATA_SOURCE[1])
            startActivity(intent)
        }
    }

    private fun syncVideoMetaData(dataSource: String):Observable<VideoMetadata> {
        return Observable
                .create<VideoMetadata> {
                    it.onNext(getVideoMetaData(dataSource))
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    private fun getVideoMetaData(dataSource: String): VideoMetadata {
        Log.d(TAG, "current thread = ${Thread.currentThread()}")
        var mmr: MediaMetadataRetriever? = null
        val videoMetadata = VideoMetadata()
        try {
            mmr = MediaMetadataRetriever()
            mmr.setDataSource(dataSource, HashMap<String, String>())
            videoMetadata.previewImage = mmr.getFrameAtTime(START_TIME,MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
            val durationMill = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION).toInt()
            videoMetadata.duration = "${durationMill / 1000 / 60}分${durationMill / 1000 % 60}"
            videoMetadata.mineType = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE)
            videoMetadata.videoWidth = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)
            videoMetadata.videoHigh = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)
        } catch (e: Exception) {
            Log.d(TAG, "get video preview image error = $e")
        } finally {
            mmr?.release()
        }
        return videoMetadata
    }
}
