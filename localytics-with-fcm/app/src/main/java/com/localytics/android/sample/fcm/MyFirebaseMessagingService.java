package com.localytics.android.sample.fcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.localytics.android.Localytics;

import java.util.Map;

import static android.R.attr.handle;

public class MyFirebaseMessagingService extends FirebaseMessagingService
{

    /**
     * All pushes received from Localytics will contain an 'll' string extra which can be parsed into
     * a JSON object. This JSON object contains performance tracking information, such as a campaign
     * ID. Any push received containing this 'll' string extra, should be handled by the Localytics
     * SDK. Any other push can be handled as you see fit.
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Map<String, String> data = remoteMessage.getData();
        if (data != null)
        {
            //Use the contents of this method if you are using SDK 4.4 or greater.
            handleNotificationInSDK44(data);

            //Use the contents of this method if you are using SDK 4.3 or lower.
            handleNotificationInSDKLessThan44(data);
        }
    }

    private void handleNotificationInSDK44(final Map<String, String> data) {
        if (!Localytics.handleFirebaseMessage(data)) {
            showNotification(data.get("message"));
        }
    }

    private void handleNotificationInSDKLessThan44(final Map<String, String> data) {
        if (data.containsKey("ll") || data.containsKey("localyticsUninstallTrackingPush")) {
            Localytics.displayPushNotification(convertMap(data));
        } else {
            handleNotificationInSDK44(data);
        }
    }

    private Bundle convertMap(Map<String, String> map)
    {
        Bundle bundle = new Bundle(map != null ? map.size() : 0);
        if (map != null)
        {
            for (Map.Entry<String, String> entry : map.entrySet())
            {
                bundle.putString(entry.getKey(), entry.getValue());
            }
        }

        return bundle;
    }

    private void showNotification(String message) {
        if (!TextUtils.isEmpty(message))
        {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                    PendingIntent.FLAG_ONE_SHOT);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("FCM Message")
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(0, notificationBuilder.build());
        }
    }
}
