package com.whf.demolist.video

import android.graphics.Bitmap

/**
 * Created by whf on 2018/7/8.
 */
class VideoMetadata() {
    var previewImage: Bitmap? = null
    var duration: String? = null
    var mineType: String? = null
    var videoHigh: String? = null
    var videoWidth: String? = null

    override fun toString(): String {
        return "duration=$duration, " +
                "mineType=$mineType, " +
                "videoHigh=$videoHigh, " +
                "videoWidth=$videoWidth)"
    }


}