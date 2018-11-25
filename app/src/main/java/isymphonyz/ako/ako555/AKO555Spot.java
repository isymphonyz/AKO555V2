package isymphonyz.ako.ako555;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import isymphonyz.ako.ako555.adapter.AKO555ArrayAdapter;
import isymphonyz.ako.ako555.customview.RSUTextView;
import isymphonyz.ako.ako555.utils.AppPreference;
import isymphonyz.ako.ako555.utils.MyConfiguration;

public class AKO555Spot extends AppCompatActivity {

    private ProgressBar progressBar;
    private RSUTextView txtTitle;
    private static ListView listView;
    private static AKO555ArrayAdapter adapter;
    private ImageView btnAdd;

    public static String folderName = MyConfiguration.FOLDER_AKO555 + File.separator + MyConfiguration.FOLDER_PUBLIC_SPOT;
    String folderTempName = MyConfiguration.FOLDER_AKO555 + File.separator + MyConfiguration.FOLDER_PUBLIC_SPOT_TEMP;
    ArrayList<String> files;
    ArrayList<String> videoOnlineNameList;
    ArrayList<String> presentationList;
    ArrayList<Boolean> downloadStatusList;

    public static Activity activity;
    private static File root;
    private File temp;

    AppPreference appPreference;
    String presentationURL = "";

    private DownloadManager downloadManager;
    private long downloadReference;

    Bundle extras;
    String title = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.spot);

        activity = this;

        extras = getIntent().getExtras();
        folderName = extras.getString("folderName");
        title = extras.getString("title");

        txtTitle = (RSUTextView) findViewById(R.id.txtTitle);
        txtTitle.setText(title);

        listView = (ListView) findViewById(R.id.listView);

        btnAdd = (ImageView) findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClassName(getPackageName(), getPackageName() + ".AKO555MediaCreate");
                intent.putExtra("folderName", folderName);
                startActivity(intent);
            }
        });

        appPreference = new AppPreference(this);
        //presentationURL = appPreference.getHostname() + appPreference.getProjectVersion() + Configuration.PRESENTATION_URL;

        root = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + folderName);
        root.mkdirs();

        temp = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + folderTempName);
        temp.mkdirs();

        if(folderName.contains(MyConfiguration.FOLDER_PUBLIC_SPOT)) {
            btnAdd.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        showList();
    }

    public static String[] videoOfflineNameArray;
    public static void showList() {
        adapter = null;
        //listView = null;

        videoOfflineNameArray = null;
        videoOfflineNameArray = root.list();
        //adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, videoOfflineNameArray);
        adapter = null;
        adapter = new AKO555ArrayAdapter(activity);
        adapter.setNameList(new ArrayList<String>(Arrays.asList(videoOfflineNameArray)));
        listView.setAdapter(adapter);
        listView.invalidateViews();
        adapter.notifyDataSetChanged();
        listView.setOnItemClickListener(new OnItemClickListener() {

            @SuppressLint("NewApi")
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                // TODO Auto-generated method stub
                Intent intent = new Intent();
                intent.setClassName(activity.getPackageName(), activity.getPackageName() + ".AKO555MediaPlayer");
                //intent.putExtra("media", videoOfflineNameArray[arg2]);
                //intent.putExtra("videoOfflineNameArray", videoOfflineNameArray);
                //intent.putExtra("index", arg2);
                intent.putExtra("prefixPath", folderName);
                intent.putExtra("spotName", videoOfflineNameArray[arg2]);
                activity.startActivity(intent);
            }
        });
    }
}
