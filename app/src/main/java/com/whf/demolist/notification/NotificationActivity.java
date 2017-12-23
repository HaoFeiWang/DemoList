package com.whf.demolist.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RemoteViews;

import com.whf.demolist.R;

public class NotificationActivity extends AppCompatActivity {

    private static final String NORMAL_CHANNEL_ID = "com.whf.demolist.normal";

    public static final String NOTIFICATION_ACTION = "com.whf.cancel.notification";

    private Button btnNotification;
    private Button btnCustomNotification;
    private Button btnActionNotification;
    private Button btnUndateNotification;

    private NotificationCompat.Builder builder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        btnNotification = findViewById(R.id.btn_notification);
        btnCustomNotification = findViewById(R.id.btn_custom_notification);
        btnActionNotification = findViewById(R.id.btn_action_notification);
        btnUndateNotification = findViewById(R.id.btn_update_notification);

        builder = new NotificationCompat.Builder(this, NORMAL_CHANNEL_ID);

        btnNotification.setOnClickListener(v -> {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_notification_large);
            Notification notification = builder.setSmallIcon(R.drawable.ic_notification_small) //会显示在状态栏
                    .setLargeIcon(bitmap)
                    .setContentTitle("快递到了") //通知标题
                    .setContentText("京东快递祝您生活愉快！") //通知的详细内容
//                    .setWhen(System.currentTimeMillis()) //时间，默认为当前时间
                    .setAutoCancel(true) // 点击后清除
                    .setTicker("买了一本Thinking Java") //在状态栏显示的内容
                    .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS|Notification.FLAG_SHOW_LIGHTS) //设置通知方式
                    .build();
            if (notificationManager != null) {
                notificationManager.notify(1, notification);
            }
        });

        //更新通知只需要发送相同ID的通知
        btnUndateNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_notification_large);
                Notification notification = builder.setSmallIcon(R.drawable.ic_notification_small)
                        .setLargeIcon(bitmap)
                        .setContentTitle("快递到了~") //通知标题
                        .setContentText("申通快递祝您生活愉快！") //通知的详细内容
                        .setWhen(System.currentTimeMillis() - 5000000) //时间，默认为当前时间
                        .setTicker("买了一本Android开发艺术探索")
                        .build();
                if (notificationManager != null) {
                    notificationManager.notify(1, notification);
                }
            }
        });

        btnActionNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NotificationActivity.this, NotificationActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(NotificationActivity.this, 200, intent, PendingIntent.FLAG_ONE_SHOT);
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_notification_large);
                Notification notification = builder.setSmallIcon(R.drawable.ic_notification_small)
                        .setLargeIcon(bitmap)
                        .setContentTitle("发送通知") //通知标题
                        .setContentText("发送了一个可点击跳转的通知") //通知的详细内容
                        .setWhen(System.currentTimeMillis()) //时间，默认为当前时间
                        .setTicker("该通知的ID是2")
                        .setContentIntent(pendingIntent)
                        .build();
                if (notificationManager != null) {
                    notificationManager.notify(2, notification);
                }
            }
        });


        btnCustomNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NotificationActivity.this, NotificationActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(NotificationActivity.this, 200, intent, PendingIntent.FLAG_ONE_SHOT);

                Intent cancelIntent = new Intent(NOTIFICATION_ACTION);
                PendingIntent cancelPendingIntent = PendingIntent.getBroadcast(NotificationActivity.this, 200, cancelIntent, PendingIntent.FLAG_ONE_SHOT);
                RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.layout_notification);
                remoteViews.setOnClickPendingIntent(R.id.iv_cancel_notification, cancelPendingIntent);

                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_notification_large);
                Notification notification = builder.setSmallIcon(R.drawable.ic_notification_small)
                        .setLargeIcon(bitmap)
                        .setContentTitle("发送通知") //通知标题
                        .setContentText("发送了一个自定义的通知") //通知的详细内容
                        .setWhen(System.currentTimeMillis()) //时间，默认为当前时间
                        .setTicker("点击最后的按钮可删除")
                        .setContentIntent(pendingIntent)
                        .setCustomContentView(remoteViews)
                        .build();


                if (notificationManager != null) {
                    notificationManager.notify(3, notification);
                }
            }
        });
    }
}
