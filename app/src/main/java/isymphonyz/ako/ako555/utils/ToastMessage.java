package isymphonyz.ako.ako555.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastMessage {

	public static void show(Context context, String message) {
		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
	}
	
}
