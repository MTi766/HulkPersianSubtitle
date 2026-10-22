
package com.hulk.persiansubtitle;

import android.app.Service;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

public class SpeechKeepAliveService extends Service {

    private static final String CHANNEL_ID = "speech_service";

    @Override
    public void onCreate() {
        super.onCreate();

        NotificationManager manager =
                (NotificationManager) getSystemService(
                        NOTIFICATION_SERVICE
                );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel =
                    new NotificationChannel(
                            CHANNEL_ID,
                            "تشخیص گفتار",
                            NotificationManager.IMPORTANCE_LOW
                    );

            manager.createNotificationChannel(channel);
        }

        Notification notification;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification = new Notification.Builder(
                    this, CHANNEL_ID
            )
                    .setContentTitle("Hulk Persian Subtitle")
                    .setContentText("تشخیص گفتار فعال است")
                    .setSmallIcon(android.R.drawable.ic_btn_speak_now)
                    .build();
        } else {
            notification = new Notification.Builder(this)
                    .setContentTitle("Hulk Persian Subtitle")
                    .setContentText("تشخیص گفتار فعال است")
                    .setSmallIcon(android.R.drawable.ic_btn_speak_now)
                    .build();
        }

        startForeground(1001, notification);
    }

    @Override
    public int onStartCommand(
            Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
