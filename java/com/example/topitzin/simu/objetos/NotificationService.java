package com.example.topitzin.simu.objetos;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.topitzin.simu.LogIn;
import com.example.topitzin.simu.MainActivity;
import com.example.topitzin.simu.NotificationLayout;
import com.example.topitzin.simu.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

/**
 * Created by topitzin on 04/04/2017.
 */

public class NotificationService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    PendingIntent resultPendingIntent;
    NotificationManager mNotificationManager;
    TaskStackBuilder stackBuilder;
    Intent resultIntent;
    String action_click;
    String titulo;
    String info;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Message data payload: " + remoteMessage.getData());
            try {

                JSONObject data = new JSONObject(remoteMessage.getData());
                info = data.getString("x");
                Log.e(TAG, "data x: " + info);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        titulo = remoteMessage.getNotification().getTitle();
        action_click = remoteMessage.getNotification().getClickAction();

        notificacion(remoteMessage.getNotification().getBody(), info);
        //setAlarm();
    }

    public void notificacion(String Msg, String info) {

        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_menu_send)
                        .setContentTitle(titulo)
                        .setContentText(Msg)
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                        .setVibrate(new long[]{1, 1, 1})
                        .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setSound(sound)
                        .setWhen(System.currentTimeMillis())
                        .setAutoCancel(true);
        //notification
        if (action_click.equals("NotificationLayout")) {
            resultIntent = new Intent(this, NotificationLayout.class);
        }
        else {
            resultIntent = new Intent(this, MainActivity.class);
        }
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        resultIntent.putExtra("msg", info);
//
        //stackBuilder = TaskStackBuilder.create(this);
        //stackBuilder.addParentStack(NotificationLayout.class);
        //stackBuilder.addNextIntent(resultIntent);

        PendingIntent fullIntent =
                PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//
        /*PendingIntent fullIntent = PendingIntent.getActivity(
                this,
                0,
                resultIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);*/
        mBuilder.setContentIntent(fullIntent);
        //mBuilder.setFullScreenIntent(fullIntent, true);

        mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(1, mBuilder.build());
    }
}
