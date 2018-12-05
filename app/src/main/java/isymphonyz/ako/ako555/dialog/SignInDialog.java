package isymphonyz.ako.ako555.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Protocol;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;

import isymphonyz.ako.ako555.R;
import isymphonyz.ako.ako555.utils.AppPreference;
import isymphonyz.ako.ako555.utils.MyConfiguration;

public class SignInDialog {

	Activity activity;
	Dialog dialog;
	ProgressBar progressBar;
	TextView txtUsername;
	TextView txtPassword;
	TextView txtMobileDeviceNo;
	EditText inputUsername;
	EditText inputPassword;
	EditText inputMobileDeviceNo;
	Button btnSubmit;
	
	AppPreference appPreference;
	private String urlSignIn = MyConfiguration.DOMAIN + MyConfiguration.VERSION + MyConfiguration.SIGNIN_URL_NEW;
	String username = "";
	String password = "";
	String mobileDeviceNo = "";
	String mobileDeviceID = "";
	String appVersion = "";

	PackageInfo pInfo;
	
	public SignInDialog(Activity activity) {
		this.activity = activity;
	}
	
	public void setProgressBar(ProgressBar progressBar) {
		this.progressBar = progressBar;
	}
	
	public void setMobileDeviceID(String mobileDeviceID) {
		this.mobileDeviceID = mobileDeviceID;
	}
	
	public void showSignInDialog() {

		try {
			pInfo = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0);
			appVersion = pInfo.versionName;
		} catch (PackageManager.NameNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// custom dialog
		dialog = new Dialog(activity);
		dialog.setContentView(R.layout.signin_dialog);
		dialog.setCancelable(true);
		dialog.setTitle(activity.getText(R.string.app_name).toString());

		appPreference = new AppPreference(activity);
		
		TelephonyManager telemngr = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
		mobileDeviceID = telemngr.getDeviceId();
		
        Typeface tf = Typeface.createFromAsset(activity.getAssets(), "fonts/rsu-light.ttf");
		
		// set the custom dialog components - text, image and button
		progressBar = (ProgressBar) dialog.findViewById(R.id.progressBar);
		txtUsername = (TextView) dialog.findViewById(R.id.txtUsername);
		txtPassword = (TextView) dialog.findViewById(R.id.txtPassword);
		txtMobileDeviceNo = (TextView) dialog.findViewById(R.id.txtMobileDeviceNo);
		inputUsername = (EditText) dialog.findViewById(R.id.inputUsername);
		inputPassword = (EditText) dialog.findViewById(R.id.inputPassword);
		inputMobileDeviceNo = (EditText) dialog.findViewById(R.id.inputMobileDeviceNo);

		txtUsername.setTypeface(tf);
		txtPassword.setTypeface(tf);
		txtMobileDeviceNo.setTypeface(tf);
		inputUsername.setTypeface(tf);
		inputPassword.setTypeface(tf);
		inputMobileDeviceNo.setTypeface(tf);

		btnSubmit = (Button) dialog.findViewById(R.id.btnSubmit);
		btnSubmit.setTypeface(tf);
		// if button is clicked, close the custom dialog
		btnSubmit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				username = inputUsername.getText().toString();
				password = inputPassword.getText().toString();
				mobileDeviceNo = inputMobileDeviceNo.getText().toString();
				
				if(username.length() > 0 && password.length() > 0) {
					btnSubmit.setEnabled(false);
					Log.d("AKO555", "App Version: " + appPreference.getAppVersion());
					Log.d("AKO555", "Username: " + username);
					Log.d("AKO555", "Password: " + password);
					Log.d("AKO555", "DeviceID: " + mobileDeviceID);
					Log.d("AKO555", "mobileDeviceNo: " + mobileDeviceNo);
					progressBar.setVisibility(View.VISIBLE);
					OkHttpClient okHttpClient = new OkHttpClient();
					okHttpClient.setProtocols(Arrays.asList(Protocol.HTTP_1_1));

					Request.Builder builder = new Request.Builder();
					RequestBody formBody = new FormEncodingBuilder()
							.add("-system-user-id", "")
							.add("-app-version", appVersion)
							.add("username", username)
							.add("password", password)
							.add("mobile-device-uid", mobileDeviceID)
							.add("mobile-device-pno", mobileDeviceNo)
							.build();

					Request request = builder.url(urlSignIn).post(formBody).build();
					//Request request = builder.url(url).get().build();
					okHttpClient.newCall(request).enqueue(new Callback() {
						@Override
						public void onFailure(Request request, IOException e) {
							connectionCallBack("Error - " + e.getMessage());
						}

						@Override
						public void onResponse(Response response) {
							if (response.isSuccessful()) {
								try {
									connectionCallBack(response.body().string());
								} catch (IOException e) {
									e.printStackTrace();
									connectionCallBack("Error - " + e.getMessage());
								}
							} else {
								connectionCallBack("Not Success - code : " + response.code());
							}
						}

						public void connectionCallBack(final String result) {
							activity.runOnUiThread(new Runnable() {
								@Override
								public void run() {
									setSignIn(result);
								}
							});
						}
					});
				} else {
					Toast.makeText(activity, "กรุณากรอกข้อมูลให้ครบ", Toast.LENGTH_SHORT).show();
				}
			}
		});

		dialog.show();

	}

	public void setSignIn(String result) {
		// TODO Auto-generated method stub
		Log.d("AKO555", "setSignIn: " + result);
		progressBar.setVisibility(View.GONE);
		btnSubmit.setEnabled(true);
		boolean status = false;
		String messageTextInter = "";
		String messageTextLocal = "";
		int messageID = 0;
		try {
			JSONObject jObj = new JSONObject(result);
			status = jObj.getBoolean("status");
			
			if(status) {
				JSONObject jObjUser = jObj.getJSONObject("user");
				appPreference.setMemberID(jObjUser.getString("member-id"));
				appPreference.setDeviceID(jObjUser.getString("device-id"));
				appPreference.setSystemUserMobileID(jObjUser.getString("system-user-id"));
				appPreference.setUsername(username);
				appPreference.setPassword(password);
				appPreference.setMobileDeviceNo(mobileDeviceNo);
				Intent intent = new Intent();
	        	intent.setClassName(activity.getPackageName(), activity.getPackageName() + ".AKO555HomeV2");
	        	activity.startActivity(intent);
	            activity.finish();
			} else {
				JSONObject jObjMessage = jObj.getJSONObject("error");
				messageTextInter = jObjMessage.getString("text-inter");
				messageTextLocal = jObjMessage.getString("text-local");
				messageID = jObjMessage.getInt("id");
				Toast.makeText(activity, messageTextLocal, Toast.LENGTH_SHORT).show();
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    
    public void toastMessage(String message) {
    	Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
    }
}
