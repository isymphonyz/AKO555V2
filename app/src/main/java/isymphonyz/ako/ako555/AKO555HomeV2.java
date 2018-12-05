package isymphonyz.ako.ako555;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.pm.ShortcutInfoCompat;
import android.support.v4.content.pm.ShortcutManagerCompat;
import android.support.v4.graphics.drawable.IconCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Protocol;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import isymphonyz.ako.ako555.adapter.AKO555HomeV2ListAdapter;
import isymphonyz.ako.ako555.service.AKO555ForegroundService;
import isymphonyz.ako.ako555.service.AKO555GPSTrackingService;
import isymphonyz.ako.ako555.service.AKO555PublicMediaService;
import isymphonyz.ako.ako555.utils.MyConfiguration;

public class AKO555HomeV2 extends AppCompatActivity {

    private GridView gridView;
    private AKO555HomeV2ListAdapter adapter;

    private ArrayList<Integer> imageList;
    private ArrayList<String> nameList;

    private String folderNamePublic = MyConfiguration.FOLDER_AKO555 + File.separator + MyConfiguration.FOLDER_PUBLIC_SPOT;
    private String folderNamePrivate = MyConfiguration.FOLDER_AKO555 + File.separator + MyConfiguration.FOLDER_PRIVATE_SPOT;

    private String urlPantamit = MyConfiguration.WWW_PANTAMIT_COM;
    private String urlPrivacy = MyConfiguration.WWW_PRIVACY;

    private String urlAllow = MyConfiguration.ALLOW;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_v2);

        if(!isMyServiceRunning(AKO555PublicMediaService.class)) {
            Intent serviceIntent = new Intent(this, AKO555PublicMediaService.class);
            //serviceIntent.setAction("isymphonyz.ako.ako555.service.AKO555GPSTrackingService");
            startService(serviceIntent);
            //ToastMessage.show(this, "AKO555PublicMediaService is starting");
        } else {
            //ToastMessage.show(this, "AKO555PublicMediaService is still running");
        }

        if(!isMyServiceRunning(AKO555GPSTrackingService.class)) {
            Intent serviceIntent = new Intent(this, AKO555GPSTrackingService.class);
            //serviceIntent.setAction("isymphonyz.ako.ako555.service.AKO555GPSTrackingService");
            startService(serviceIntent);
        } else {
            //ToastMessage.show(this, "AKO555PublicMediaService is still running");
        }

        if(!isMyServiceRunning(AKO555ForegroundService.class)) {
            Intent serviceIntent = new Intent(this, AKO555ForegroundService.class);
            //serviceIntent.setAction("isymphonyz.ako.ako555.service.AKO555GPSTrackingService");
            startService(serviceIntent);
        } else {
            //ToastMessage.show(this, "AKO555PublicMediaService is still running");
        }

        imageList = new ArrayList<Integer>();
        imageList.add(R.mipmap.ic_customer_01);
        imageList.add(R.mipmap.ic_private_spot_01);
        imageList.add(R.mipmap.ic_project_01);
        imageList.add(R.mipmap.ic_public_spot_01);
        imageList.add(R.mipmap.ic_send_spot_01);
        imageList.add(R.mipmap.ic_map_01);
        imageList.add(R.mipmap.ic_barcode_01);
        imageList.add(R.mipmap.ic_broadcast_01);
        imageList.add(R.mipmap.ic_information_01);
        imageList.add(R.mipmap.ic_www_01);
        imageList.add(R.mipmap.ic_privacy_01);

        nameList = new ArrayList<String>();
        nameList.add(getText(R.string.home_btn_customer).toString());
        nameList.add(getText(R.string.home_btn_private_spot).toString());
        nameList.add(getText(R.string.home_btn_project).toString());
        nameList.add(getText(R.string.home_btn_public_spot).toString());
        nameList.add(getText(R.string.home_btn_send_spot).toString());
        nameList.add(getText(R.string.home_btn_map).toString());
        nameList.add(getText(R.string.home_btn_barcode).toString());
        nameList.add(getText(R.string.home_btn_broadcast).toString());
        nameList.add(getText(R.string.home_btn_information).toString());
        nameList.add(getText(R.string.home_btn_www).toString());
        nameList.add(getText(R.string.home_btn_privacy).toString());

        gridView = (GridView) findViewById(R.id.gridView);
        gridView.setNumColumns((nameList.size()/2) + nameList.size()%2);
        adapter = new AKO555HomeV2ListAdapter(this);
        adapter.setImageList(imageList);
        adapter.setNameList(nameList);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 9) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(urlPantamit));
                    startActivity(i);
                } else if(position == 10) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(urlPrivacy));
                    startActivity(i);
                } else {
                    Intent intent = new Intent();
                    String activityName = "";
                    if(position == 2) {
                        activityName = ".AKO555Project";
                    } else if(position == 1) {
                        activityName = ".AKO555Spot";
                        intent.putExtra("title", nameList.get(position));
                        intent.putExtra("folderName", folderNamePrivate);
                    } else if(position == 3) {
                        activityName = ".AKO555Spot";
                        intent.putExtra("title", nameList.get(position));
                        intent.putExtra("folderName", folderNamePublic);
                    } else if(position == 4) {
                        activityName = ".AKO555SendSpot";
                    } else if(position == 5) {
                        activityName = ".AKO555Map";
                    } else if(position == 6) {
                        activityName = ".AKO555Payment";
                    } else if(position == 8) {
                        activityName = ".AKO555Information";
                    } else if(position == 0) {
                        activityName = ".AKO555Customer";
                    } else {
                        activityName = ".AKO555UnderConstruction";
                    }
                    intent.setClassName(getPackageName(), getPackageName() + activityName);
                    startActivity(intent);
                }
            }
        });

        checkAllow();

        ShortcutIcon();
        addShortcutToHomeScreen(getApplicationContext());
    }

    private void checkAllow() {

        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setProtocols(Arrays.asList(Protocol.HTTP_1_1));

        Request.Builder builder = new Request.Builder();
        RequestBody formBody = new FormEncodingBuilder()
                .add("-system-user-id", "")
                .build();
        Request request = builder.url(urlAllow).post(formBody).build();
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
                        try {
                            JSONObject jObj = new JSONObject(result);
                            int allow = jObj.optInt("allow");
                            if(allow == 0) {
                                finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void ShortcutIcon(){
        Intent shortcutIntent = new Intent(getApplicationContext(), AKO555Information.class);
        shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        Intent addIntent = new Intent();
        addIntent.putExtra("duplicate", false);
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "Test");
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(getApplicationContext(), R.mipmap.ic_type_folder));
        addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        getApplicationContext().sendBroadcast(addIntent);
    }

    public void addShortcutToHomeScreen(Context context) {
        if (ShortcutManagerCompat.isRequestPinShortcutSupported(context)) {
            Intent intent = new Intent(context, AKO555Spot.class).setAction(Intent.ACTION_MAIN);
            intent.putExtra("title", getText(R.string.home_btn_public_spot).toString());
            intent.putExtra("folderName", folderNamePublic);

            ShortcutInfoCompat shortcutInfo = new ShortcutInfoCompat.Builder(context, "#1")
                    //.setIntent(new Intent(context, AKO555Spot.class).setAction(Intent.ACTION_MAIN)) // !!! intent's action must be set on oreo
                    .setIntent(intent)
                    .setShortLabel(getText(R.string.home_btn_public_spot).toString())
                    .setIcon(IconCompat.createWithResource(context, R.mipmap.ic_folder_01))
                    .build();
            ShortcutManagerCompat.requestPinShortcut(context, shortcutInfo, null);
        }
        else
        {
            // Shortcut is not supported by your launcher
        }
    }
}
