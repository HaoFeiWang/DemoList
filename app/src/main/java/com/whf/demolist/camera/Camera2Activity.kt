package com.whf.demolist.camera

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.media.ImageReader
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Surface
import android.view.TextureView
import com.whf.demolist.R
import kotlinx.android.synthetic.main.activity_camera2.*
import java.util.*


class Camera2Activity : AppCompatActivity() {

    val tag = Camera2Activity::class.java.simpleName

    var cameraDevice: CameraDevice? = null
    var surfaceTexture: SurfaceTexture? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera2)

        initSurface()
        initCamera()
    }

    private fun initSurface() {
        //获取SurfaceHolder,并为其设置回调
        surface_view.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
            override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture?, width: Int, height: Int) {
                Log.i(tag, "onSurfaceTextureSizeChanged width = $width height = $height")
            }

            override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {
                Log.i(tag, "onSurfaceTextureUpdated")
            }

            override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean {
                Log.i(tag, "onSurfaceTextureDestroyed")
                return true
            }

            override fun onSurfaceTextureAvailable(surface: SurfaceTexture?, width: Int, height: Int) {
                Log.i(tag, "onSurfaceTextureAvailable width = $width height = $height")
                surfaceTexture = surface
                //设置预览尺寸
                surfaceTexture?.setDefaultBufferSize(1280, 640)
                startPreview()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun initCamera() {
        //获取摄像头数量(一般0为后置摄像头，1为前置摄像头)
        val cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        for (cameraId in cameraManager.cameraIdList) {
            Log.d(tag, "camera id = $cameraId")
        }

        //获取摄像头的特征值
        val characteristics = cameraManager.getCameraCharacteristics("0")
        //获取StreamConfigurationMap，它是管理摄像头支持的所有输出格式和尺寸
        val streamConfigurationMap = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)


        //handler为StateCallback执行的线程，如果为空在当前线程执行
        cameraManager.openCamera("0", object : CameraDevice.StateCallback() {
            override fun onOpened(camera: CameraDevice) {
                Log.d(tag, "camera onOpened")
                cameraDevice = camera
                startPreview()
            }

            override fun onClosed(camera: CameraDevice) {
                Log.d(tag, "camera onClosed")
            }

            override fun onDisconnected(camera: CameraDevice) {
                Log.d(tag, "camera onDisconnected")
            }

            override fun onError(camera: CameraDevice, error: Int) {
                Log.d(tag, "camera onError")
            }

        }, null)
    }

    private fun startPreview() {
        val localSurfaceTexture = surfaceTexture
        val localCameraDevice = cameraDevice

        if (localSurfaceTexture == null || localCameraDevice == null) {
            return
        }

        //创建预览Surface
        val previewSurface = Surface(localSurfaceTexture)

        //预览帧获取接口
        val imageReader = ImageReader.newInstance(
                1280, 640, ImageFormat.YUV_420_888, 2)
        imageReader.setOnImageAvailableListener(object : ImageReader.OnImageAvailableListener {
            override fun onImageAvailable(reader: ImageReader?) {
                reader?.let {
                    val image = it.acquireLatestImage()

                    //可以将这帧数据转成字节数组，类似于Camera1的PreviewCallback回调的预览帧数据
                    val buffer = image.planes[0].buffer
                    val data = ByteArray(buffer.remaining())

                    buffer.get(data)
                    image.close()
                }
            }
        }, null)

        //预览的Surface
        val captureRequestBuilder = localCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
        captureRequestBuilder.addTarget(previewSurface)

        //所有输出的Surface
        val surfaceList: MutableList<Surface> = ArrayList()
        surfaceList.add(previewSurface)
        surfaceList.add(imageReader.surface)

        //建立会话
        localCameraDevice.createCaptureSession(surfaceList, object : CameraCaptureSession.StateCallback() {
            override fun onConfigureFailed(session: CameraCaptureSession) {
                Log.d(tag, "onConfigureFailed")
            }

            override fun onConfigured(session: CameraCaptureSession) {
                Log.d(tag, "onConfigured")
                //设置反复捕获数据的请求，这样预览界面就会一直有数据显示
                session.setRepeatingRequest(captureRequestBuilder.build(), null, null)

            }

        }, null)
    }
}
