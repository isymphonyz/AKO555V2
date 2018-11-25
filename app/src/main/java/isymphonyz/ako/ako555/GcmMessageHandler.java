package isymphonyz.ako.ako555;

/**
 * Created by Dooplus on 9/27/15 AD.
 */

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

public class GcmMessageHandler extends GcmListenerService {
    public static final int MESSAGE_NOTIFICATION_ID = 435346;

    @Override
    public void onMessageReceived(String from, Bundle data) {
        //Toast.makeText(getApplicationContext(), data.toString(), Toast.LENGTH_LONG).show();
        String message = data.getString("message");
        String title = data.getString("title");
        Log.d("AISLiveTV", "Data: " + data.toString());
        Log.d("AISLiveTV", "Message: " + message);

        createNotification(title, message);
    }

    // Creates notification based on title and body received
    private void createNotification(String title, String body) {
        // First let's define the intent to trigger when notification is selected
        // Start out by creating a normal intent (in this case to open an activity)
        Intent intent = new Intent(this, AKO555LandingPage.class);

        // Next, let's turn this into a PendingIntent using
        //   public static PendingIntent getActivity(Context context, int requestCode,
        //       Intent intent, int flags)
        int requestID = (int) System.currentTimeMillis(); //unique requestID to differentiate between various notification with same NotifId
        int flags = PendingIntent.FLAG_CANCEL_CURRENT; // cancel old intent and create new one
        PendingIntent pIntent = PendingIntent.getActivity(this, requestID, intent, flags);

        Context context = getBaseContext();
        //Notification noti = new NotificationCompat.Builder(context)
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher).setContentTitle(title)
                .setContentText(body)
                .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 })
                .setLights(Color.RED, 3000, 3000)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setContentIntent(pIntent);

        // Hide the notification after its selected
        //noti.setAutoCancel(true);
        mBuilder.setAutoCancel(true);

        NotificationManager mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(MESSAGE_NOTIFICATION_ID, mBuilder.build());
        //mNotificationManager.notify(0, noti);
    }

    // Handle push notification by invoking activity directly
    private void launchSomeActivity(Context context, String datavalue) {
        Intent pupInt = new Intent(context, AKO555LandingPage.class);
        pupInt.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //pupInt.putExtra("customdata", datavalue);
        context.getApplicationContext().startActivity(pupInt);
    }

}
