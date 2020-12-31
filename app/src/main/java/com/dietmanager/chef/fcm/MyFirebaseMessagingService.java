package com.dietmanager.chef.fcm;


import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import android.util.Log;

import com.dietmanager.chef.Application;
import com.dietmanager.chef.R;
import com.dietmanager.chef.activities.Home;
import com.dietmanager.chef.activities.Splash;
import com.dietmanager.chef.model.PushCustomObject;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.util.List;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    public static Ringtone mRingtone;
    private String page ="main";
    private int orderId =0;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if (remoteMessage.getData() != null) {
            Log.d(TAG, "From: " + remoteMessage.getFrom());
            Log.d(TAG, "Notification Message Body: " + remoteMessage.getData());
            //Calling method to generate notification
            if(remoteMessage.getData().get("custom")!=null&&remoteMessage.getData().get("custom").contains("page")){
                Gson gson = new Gson();
                try {
                    PushCustomObject pushCustomObject = gson.fromJson(remoteMessage.getData().get("custom"), PushCustomObject.class);
                    page=pushCustomObject.getPage();
                    orderId=pushCustomObject.getOrderId();
                } catch (JsonSyntaxException e) {
                }
            }
            sendNotification(remoteMessage.getData().get("message"));
        } else {
            Log.d(TAG, "FCM Notification failed");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void sendNotification(String messageBody) {
        Log.d(TAG, "messageBody " + messageBody);

        Intent intent = new Intent(getApplicationContext(), Splash.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("Notification", messageBody);
        intent.putExtra("page", page);
        if(orderId!=0)
            intent.putExtra("order_id", orderId);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "PUSH");
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.addLine(messageBody);

      /*  if (messageBody != null)
            if (messageBody.equalsIgnoreCase("New order request") ||
                    messageBody.equalsIgnoreCase("You Have One Incoming Request")) {
                playNotificationSound();
            }*/

        if ((messageBody.equalsIgnoreCase("New order request") || messageBody.equalsIgnoreCase("You Have One Incoming Request"))
                && isBackground(getApplicationContext())
                && !isLocked(getApplicationContext())
                && !isCallActive(getApplicationContext())) restartApp();

        long when = System.currentTimeMillis();         // notification time

        String CHANNEL_ID = "my_channel_01";    // The id of the channel.
        CharSequence name = "Channel human readable title";// The user-visible name of the channel.
        int importance = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N)
            importance = NotificationManager.IMPORTANCE_HIGH;

        Notification notification;
        notification = mBuilder.setWhen(when)
                //                .setAutoCancel(true)
                .setContentTitle(getString(R.string.app_name))
                .setContentIntent(pendingIntent)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setStyle(inboxStyle)
                .setAutoCancel(true)
                .setWhen(when)
                .setSmallIcon(getNotificationIcon(mBuilder))
                .setContentText(messageBody)
                .setChannelId(CHANNEL_ID)
                .setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS)
                .build();

        NotificationManager notificationManager = (NotificationManager)
                getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            android.app.NotificationChannel mChannel = new android.app.NotificationChannel(CHANNEL_ID, name, importance);
            notificationManager.createNotificationChannel(mChannel);
        }

        notificationManager.notify(0, notification);
    }

    /*private int getNotificationIcon(NotificationCompat.Builder notificationBuilder) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
            return R.drawable.ic_stat_push;
        } else return R.drawable.ic_stat_push;
    }*/

    public void playNotificationSound() {
        try {
            Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + Application.getInstance().getApplicationContext().getPackageName() + "/raw/alert_tone");
            mRingtone = RingtoneManager.getRingtone(Application.getInstance().getApplicationContext(), alarmSound);
            mRingtone.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getNotificationIcon(NotificationCompat.Builder notificationBuilder) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
            return R.drawable.ic_push;
        } else {
//            notificationBuilder.setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
            return R.drawable.ic_push;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private boolean isBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses)
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND)
                    for (String activeProcess : processInfo.pkgList)
                        if (activeProcess.equals(context.getPackageName())) isInBackground = false;
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName()))
                isInBackground = false;
        }

        return isInBackground;
    }

    public boolean isCallActive(Context context) {
        AudioManager manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        return manager.getMode() == AudioManager.MODE_IN_CALL;
    }

    public boolean isLocked(Context context) {
        KeyguardManager myKM = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        return myKM.isKeyguardLocked();
    }

    private void restartApp() {
        startActivity(new Intent(this, Home.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }


}
