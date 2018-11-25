package isymphonyz.ako.ako555.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import isymphonyz.ako.ako555.receiver.AKO555ForegroundReceiver;
import isymphonyz.ako.ako555.utils.AppPreference;

public class AKO555ForegroundService extends Service {

    private static final String TAG = "AKO555Foreground";
    private AKO555ForegroundReceiver active;
    int count = 0;

    String packageName = "isymphonyz.appmonitoring";
    String otherPackageName = "isymphonyz.ako.ako555";

    public AKO555ForegroundService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        //Toast.makeText(this, "My Service Created", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onCreate");
        active = new AKO555ForegroundReceiver();
        Context context = this.getApplicationContext();
        new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                    }


                    //if(!context.getPackageName().equalsIgnoreCase(((ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE)).getRunningTasks(1).get(0).topActivity.getPackageName())) {
                    if(!otherPackageName.equalsIgnoreCase(((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE)).getRunningTasks(1).get(0).topActivity.getPackageName())) {
                        //Log.d("PCConsult", "App is not in the foreground");
                        count++;
                        if(count >= 10800) {//10800
                            AppPreference.getInstance(AKO555ForegroundService.this).setForegroundStatus(false);
                        }
                    } else {
                        //Log.d("PCConsult", "App is in the foreground");
                        count = 0;
                        AppPreference.getInstance(AKO555ForegroundService.this).setForegroundStatus(true);
                    }

                    if(count%3 == 0) {
                        //Log.d("PCConsult", "Service: " + count);
                    }
                }
            }
        }.start();
    }

    @Override
    public void onDestroy() {
        //Toast.makeText(this, "My Service Stopped", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onDestroy");
    }

    @Override
    public void onStart(Intent intent, int startid) {
        //Toast.makeText(this, "My Service Started", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onStart");
    }
}
