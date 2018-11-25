package isymphonyz.ako.ako555;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import isymphonyz.ako.ako555.customview.RSUTextView;
import isymphonyz.ako.ako555.dialog.SignInDialog;
import isymphonyz.ako.ako555.utils.AppPreference;
import isymphonyz.ako.ako555.utils.MyConfiguration;
import isymphonyz.ako.ako555.utils.ToastMessage;

public class AKO555LandingPage extends AppCompatActivity {

    private AKO555LandingPage mActivity;

    protected boolean _active = true;
    protected int _splashTime = 1600; // time to display the splash screen in ms

    private ProgressBar progressBar;
    private Button btnRegister;
    private Button btnSignIn;
    private RSUTextView txtVersion;

    PackageInfo pInfo;
    String version = "";

    private AppPreference appPreference;
    private String username = "";
    private String password = "";
    private String mobileDeviceNo = "";
    private String mobileDeviceID = "";

    private String urlSignIn = MyConfiguration.DOMAIN + MyConfiguration.VERSION + MyConfiguration.SIGNIN_URL_NEW;

    Thread splashTread;
    private GCMClientManager pushClientManager;
    String PROJECT_NUMBER = "api-project-697344762177";

    OkHttpClient okHttpClientGCM;
    Request.Builder builderGCM;
    Request requestGCM;
    RequestBody formBodyGCM;
    String urlDeviceToken = "http://authuser.dooplus.tv:7434/aistv/deviceToken.php";
    String device_id = "";
    String app_version = "";

    private static final int REQUEST_PERMISSIONS = 100;
    private static final String PERMISSIONS_REQUIRED[] = new String[]{
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.GET_ACCOUNTS,
            Manifest.permission.INTERNET,
            Manifest.permission.VIBRATE,
            Manifest.permission.CAMERA
    };

    private boolean checkPermission(String permissions[]) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void checkPermissions() {
        boolean permissionsGranted = checkPermission(PERMISSIONS_REQUIRED);
        if (permissionsGranted) {
            //Toast.makeText(this, "You've granted all required permissions!", Toast.LENGTH_SHORT).show();
            Log.d("AKO555", "You've granted all required permissions!");
        } else {
            boolean showRationale = true;
            for (String permission : PERMISSIONS_REQUIRED) {
                showRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, permission);
                if (!showRationale) {
                    break;
                }
            }

            String dialogMsg = showRationale ? "We need some permissions to run this APP!" : "You've declined the required permissions, please grant them from your phone settings";

            new AlertDialog.Builder(this)
                    .setTitle("Permissions Required")
                    .setMessage(dialogMsg)
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(mActivity, PERMISSIONS_REQUIRED, REQUEST_PERMISSIONS);
                        }
                    }).create().show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d("AKO555", "requestCode: " + requestCode);
        Log.d("AKO555", "Permissions:" + Arrays.toString(permissions));
        Log.d("AKO555", "grantResults: " + Arrays.toString(grantResults));

        if (requestCode == REQUEST_PERMISSIONS) {
            boolean hasGrantedPermissions = true;
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    hasGrantedPermissions = false;
                    Log.d("AKO555", "Grant: " + permissions[i] + Arrays.toString(grantResults));
                    break;
                }
            }

            if (!hasGrantedPermissions) {
                finish();
            }

        } else {
            finish();
        }

        Log.d("AKO555", "Restart Activity");
        finish();
        Intent intent = new Intent(AKO555LandingPage.this, AKO555LandingPage.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        overridePendingTransition(0,0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);

        mActivity = this;

        checkPermissions();

        setContentView(R.layout.landing_page);

        // Add code to print out the key hash
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "isymphonyz.ako.ako555",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("AKO555", "KeyHash: " + Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.d("AKO555", "NameNotFoundException: " + e.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.d("AKO555", "NoSuchAlgorithmException: " + e.toString());
        }

        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        TelephonyManager telemngr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            Log.d("AKO555", "We have a permission to check DeviceID.");

            onResume();
            return;
        }
        mobileDeviceID = telemngr.getDeviceId();

        device_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        //mobileDeviceID = device_id;

        Log.d("AKO555", "DeviceID: " + device_id);
        Log.d("AKO555", "MobileDeviceID: " + mobileDeviceID);

        appPreference = new AppPreference(this);
        username = appPreference.getUsername();
        password = appPreference.getPassword();
        mobileDeviceNo = appPreference.getMobileDeviceNo();
        appPreference.setMobileDeviceID(mobileDeviceID);
        appPreference.setAppVersion(version);

        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/rsu-light.ttf");

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        txtVersion = (RSUTextView) findViewById(R.id.txtVersion);
        txtVersion.setText("version " + version);

        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnRegister.setTypeface(tf);
        btnRegister.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent();
                intent.setClassName(getPackageName(), getPackageName() + ".AKO555Register");
                startActivity(intent);
                //finish();
            }
        });

        btnSignIn = (Button) findViewById(R.id.btnSignIn);
        btnSignIn.setTypeface(tf);
        btnSignIn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                SignInDialog signinDialog = new SignInDialog(AKO555LandingPage.this);
                //signinDialog.setProgressBar(progressBar);
                signinDialog.showSignInDialog();
                Intent intent = new Intent();
                intent.setClassName(getPackageName(), getPackageName() + ".AKO555Home");
                //startActivity(intent);
                //finish();
            }
        });

        // thread for displaying the SplashScreen
        splashTread = new Thread() {
            @Override
            public void run() {
                try {
                    int waited = 0;
                    while (_active && (waited < _splashTime)) {
                        sleep(100);
                        if (_active) {
                            waited += 100;
                        }
                    }
                } catch (InterruptedException e) {
                    // do nothing
                } finally {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //stuff that updates ui
                            btnRegister.setVisibility(View.VISIBLE);
                            btnSignIn.setVisibility(View.VISIBLE);

                            if(username.length() > 0 && password.length() > 0) {
                                autoSignIn();
                            }
                        }
                    });
                }
            }
        };
        //splashTread.start();

        pushClientManager = new GCMClientManager(this, PROJECT_NUMBER);
        pushClientManager.registerIfNeeded(new GCMClientManager.RegistrationCompletedHandler() {
            @Override
            public void onSuccess(String registrationId, boolean isNewRegistration) {
                //Toast.makeText(AISLiveTVSplashScreen.this, registrationId, Toast.LENGTH_SHORT).show();
                // SEND async device registration to your back-end server
                // linking user with device registration id
                // POST https://my-back-end.com/devices/register?user_id=123&device_id=abc
                //splashTread.start();
                Log.d("AKO555", "RegistrationID: " + registrationId);

                /*Log.d("AISLiveTV", "RegistrationID: " + registrationId);
                formBodyGCM = new FormEncodingBuilder()
                        .add("device_token", device_id)
                        .add("device_os", "ANDROID")
                        .add("device_os_version", Build.VERSION.RELEASE)
                        .add("device_type", "P")
                        .add("app_version", app_version)
                        .add("gcm_id", registrationId)
                        .build();
                requestGCM = builderGCM.url(urlDeviceToken).post(formBodyGCM).build();
                okHttpClientGCM.newCall(requestGCM).enqueue(new Callback() {
                    @Override
                    public void onFailure(Request request, IOException e) {
                        updateView("Error - " + e.getMessage());
                    }

                    @Override
                    public void onResponse(Response response) {
                        if (response.isSuccessful()) {
                            try {
                                updateView(response.body().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                                updateView("Error - " + e.getMessage());
                            }
                        } else {
                            updateView("Not Success - code : " + response.code());
                        }
                    }

                    public void updateView(final String strResult) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //textResult.setText(strResult);
                                Log.d("AISLiveTV", "Result: " + strResult);
                                splashTread.start();
                            }
                        });
                    }
                });*/
            }

            @Override
            public void onFailure(String ex) {
                super.onFailure(ex);
                // If there is an error registering, don't just keep trying to register.
                // Require the user to click a button again, or perform
                // exponential back-off when retrying.
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        LocationManager mlocManager = (LocationManager) AKO555LandingPage.this.getSystemService(Context.LOCATION_SERVICE);
        boolean enabledGPS = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean enabledNetwork = mlocManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if(enabledGPS || enabledNetwork) {
            //autoSignIn();
            //Toast.makeText(getApplicationContext(), "GPS On", Toast.LENGTH_SHORT).show();
            try {
                splashTread.start();
            } catch(Exception e) {

            }
        } else {
            //Toast.makeText(getApplicationContext(), "GPS Off", Toast.LENGTH_SHORT).show();
            //turnGPSOn();
            //startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            openAlert();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void autoSignIn() {
        progressBar.setVisibility(View.VISIBLE);
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setProtocols(Arrays.asList(Protocol.HTTP_1_1));

        Request.Builder builder = new Request.Builder();
        RequestBody formBody = new FormEncodingBuilder()
                .add("-system-user-id", "")
                .add("-app-version", version)
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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setSignIn(result);
                    }
                });
            }
        });
    }

    public void setSignIn(String result) {
        // TODO Auto-generated method stub
        Log.d("AKO555", "Result: " + result);
        progressBar.setVisibility(View.GONE);
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

                JSONObject jObjData = jObj.getJSONObject("data");
                JSONObject jObjDataSystemSetting = jObjData.getJSONObject("system-setting");
                appPreference.setMobileTrackingIntervalLocation(jObjDataSystemSetting.getString("mobile-tracking-interval-location"));

                Intent intent = new Intent();
                intent.setClassName(getPackageName(), getPackageName() + ".AKO555Home");
                startActivity(intent);
                finish();
            } else {
                JSONObject jObjMessage = jObj.getJSONObject("message");
                messageTextInter = jObjMessage.getString("text-inter");
                messageTextLocal = jObjMessage.getString("text-local");
                messageID = jObjMessage.getInt("id");
                ToastMessage.show(getApplicationContext(), messageTextLocal);
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Intent intent = new Intent();
            intent.setClassName(getPackageName(), getPackageName() + ".AKO555Home");
            //startActivity(intent);
            //finish();
        }
    }

    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        }else{
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }

    private void turnGPSOn(){
        String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        if(!provider.contains("gps")){ //if gps is disabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            sendBroadcast(poke);
            Toast.makeText(getApplicationContext(), "GPS On", Toast.LENGTH_SHORT).show();
        }
    }

    private void openAlert() {
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(this);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setTitle(getText(R.string.app_name));
        alertDialogBuilder.setMessage(getText(R.string.landing_page_txt_please_open_gps));
        // set positive button: Yes message
        alertDialogBuilder.setPositiveButton(getText(R.string.landing_page_txt_ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // go to a new activity of the app
                startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        });
        // set negative button: No message
        alertDialogBuilder.setNegativeButton(getText(R.string.landing_page_txt_close), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // cancel the alert box and put a Toast to the user
                dialog.cancel();
                finish();
            }
        });

        android.app.AlertDialog alertDialog = alertDialogBuilder.create();
        // show alert
        alertDialog.show();
    }
}
