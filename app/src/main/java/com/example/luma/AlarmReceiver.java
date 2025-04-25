package com.example.luma;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
public class AlarmReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "note_alarm_channel";

    /*@Override
    public void onReceive(Context context, Intent intent) {
        String noteTitle = intent.getStringExtra("NOTE_TITLE");

        // إنشاء إشعار
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Note Reminder")
                .setContentText("Reminder for note: " + noteTitle)
                .setPriority(Notification.PRIORITY_HIGH)
                .setAutoCancel(true);

        if (notificationManager != null) {
            notificationManager.notify(1, builder.build());
        }
    }*/





   /* @Override
    public void onReceive(Context context, Intent intent) {
        String noteTitle = intent.getStringExtra("NOTE_TITLE");

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Note Alarm Channel",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Channel for note alarms");
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        Notification.Builder builder = new Notification.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Note Reminder")
                .setContentText("Reminder for note: " + noteTitle)
                .setPriority(Notification.PRIORITY_HIGH)
                .setAutoCancel(true);

        if (notificationManager != null) {
            notificationManager.notify(1, builder.build());
        }
    }}
*/

        @Override
        public void onReceive(Context context, Intent intent) {
            String noteTitle = intent.getStringExtra("NOTE_TITLE");

            // إنشاء قناة الإشعارات (إذا لم تكن موجودة)
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(
                        CHANNEL_ID,
                        "Note Alarm Channel",
                        NotificationManager.IMPORTANCE_HIGH
                );
                channel.setDescription("Channel for note alarms");
                if (notificationManager != null) {
                    notificationManager.createNotificationChannel(channel);
                }
            }

            // إنشاء الإشعار
            Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(android.R.drawable.ic_dialog_info)
                    .setContentTitle("Note Reminder")
                    .setContentText("Reminder for note: " + noteTitle)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true)
                    .build();

            if (notificationManager != null) {
                notificationManager.notify(1, notification);
            }
        }
    }