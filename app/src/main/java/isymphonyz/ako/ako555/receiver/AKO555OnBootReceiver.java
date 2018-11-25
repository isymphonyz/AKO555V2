package isymphonyz.ako.ako555.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AKO555OnBootReceiver extends BroadcastReceiver {
	
	@Override
    public void onReceive(Context context, Intent intent) {
        //Intent service = new Intent(context, AKO555GPSTrackingService.class);
        //Intent service = new Intent(context, AKO555GPSTrackingServiceV2.class);
        //context.startService(service);
    }
}
