package isymphonyz.ako.ako555.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;

/**
 * Created by Dooplus on 3/12/16 AD.
 */
public class AKO555ForegroundReceiver extends BroadcastReceiver {

    private static final String TAG = "AKO555";

    String packageName = "isymphonyz.appmonitoring";
    String otherPackageName = "com.google.android.gm";

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d(TAG, "Receiver On");

        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "YOUR TAG");
        //Acquire the lock
        wl.acquire();

        //Release the lock
        wl.release();
    }
}
