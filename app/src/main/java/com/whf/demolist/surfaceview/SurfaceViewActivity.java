package com.whf.demolist.surfaceview;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Button;

import com.whf.demolist.R;

import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_MOVE;
import static android.view.MotionEvent.ACTION_UP;

public class SurfaceViewActivity extends AppCompatActivity implements SurfaceHolder.Callback, Runnable {

    private static final String TAG = SurfaceViewActivity.class.getSimpleName();
    private SurfaceView drawingBoard;
    private Button btnClean;
    private SurfaceHolder surfaceHolder;
    private Canvas canvas;
    private boolean isDrawing;
    private Paint paint;
    private float lastX;
    private float lastY;
    private Path path;
    private Thread drawingThread;
    private int actionBarHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surface_view);
        initView();
    }

    private void initView() {
        drawingBoard = findViewById(R.id.drawing_board);
        btnClean = findViewById(R.id.btn_clean);

        btnClean.setOnClickListener(v -> {
            Log.i(TAG, "clean surface view");
            path.reset();
            canvas = surfaceHolder.lockCanvas();
            canvas.drawColor(Color.parseColor("#ffffff"));
            surfaceHolder.unlockCanvasAndPost(canvas);
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            int[] location = new int[2];
            drawingBoard.getLocationOnScreen(location);
            actionBarHeight = location[1];
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        surfaceHolder = drawingBoard.getHolder();
        surfaceHolder.addCallback(this);

        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.RED);
        paint.setAntiAlias(true);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(10);
        path = new Path();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }


    @Override
    protected void onPause() {
        Log.i(TAG, "activity pause");
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "activity destroy");
        super.onDestroy();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        canvas = surfaceHolder.lockCanvas();
        canvas.drawColor(Color.parseColor("#ffffff"));
        surfaceHolder.unlockCanvasAndPost(canvas);

        drawingThread = new Thread(this);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.i(TAG, "surface destroy");
        isDrawing = false;
    }

    @Override
    public void run() {
        while (isDrawing) {
            //获得画布
            canvas = surfaceHolder.lockCanvas();
            //绘制
            drawing();
            //提交画布中的内容
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    private void drawing() {
        canvas.drawColor(Color.parseColor("#ffffff"));
        canvas.drawPath(path, paint);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY() - actionBarHeight;
        switch (event.getAction()) {
            case ACTION_DOWN:
                Log.i(TAG, "drawing thread state = " + drawingThread.getState());
                if (!drawingThread.isAlive()) {
                    isDrawing = true;
                    drawingThread.start();
                }
                lastX = x;
                lastY = y;
                path.moveTo(x, y);
                break;
            case ACTION_MOVE:
                float dx = Math.abs(x - lastX);
                float dy = Math.abs(y - lastY);
                if (dx > 3 || dy > 3) {
                    path.quadTo(lastX, lastY, (lastX + x) / 2, (lastY + y) / 2);
                }
                lastX = x;
                lastY = y;
                break;
            case ACTION_UP:
                isDrawing = false;
                break;
        }
        return super.onTouchEvent(event);
    }


}
