package com.example.otphind;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class SMSBackgroundService extends Service {

    private static final String CHANNEL_ID = "ForegroundServiceChannel";

    private final BroadcastReceiver smsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Handle incoming SMS messages here
            // For simplicity, we'll just broadcast an intent that MainActivity can receive
            Intent broadcastIntent = new Intent("android.provider.Telephony.SMS_RECEIVED");
            LocalBroadcastManager.getInstance(context).sendBroadcast(broadcastIntent);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        registerReceiver(smsReceiver, new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));

        createNotificationChannel();
        Notification notification = new Notification.Builder(this, CHANNEL_ID)
                .setContentTitle("SMS Listener Service")
                .setContentText("Service is running in the background")
                .setSmallIcon(R.drawable.ic_notification)
                .build();

        startForeground(1, notification);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(smsReceiver);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        NotificationChannel serviceChannel = new NotificationChannel(
                CHANNEL_ID,
                "Foreground Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
        );
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(serviceChannel);
    }
}
