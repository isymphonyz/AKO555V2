package isymphonyz.ako.ako555;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Protocol;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import isymphonyz.ako.ako555.adapter.AKO555MediaListAdapter;
import isymphonyz.ako.ako555.utils.AppPreference;
import isymphonyz.ako.ako555.utils.MyConfiguration;

public class AKO555ProjectInfo extends AppCompatActivity {

    private ProgressBar progressBar;
    private TextView txtTitle;
    private ImageView btnMedia;
    private ImageView btnEdit;
    private TextView txtProjectTitle;
    private TextView txtDateCreateTitle;
    private TextView txtDateCreate;
    private TextView txtDateUpdateTitle;
    private TextView txtDateUpdate;
    private TextView txtDateStartTitle;
    private TextView txtDateStart;
    private TextView txtDescriptionTitle;
    private TextView txtDescription;
    private TextView txtMediaTitle;
    private TextView txtTotalFileTitle;
    private TextView txtTotalFile;
    private TextView txtFileSizeTitle;
    private TextView txtFileSize;
    private TextView txtDurationTitle;
    private TextView txtDuration;
    private TextView txtCustomerListTitle;
    private TextView txtCustomer;
    private ListView listView;
    private AKO555MediaListAdapter adapter;
    private Typeface tf;

    public static MediaPlayer mediaPlayer;
    public TextView songName, duration;
    private double timeElapsed = 0, finalTime = 0;
    private int forwardTime = 2000, backwardTime = 2000;
    private Handler durationHandler = new Handler();
    private SeekBar seekbar;

    Bundle extras;
    String projectID = "";
    String projectTitle = "";
    String dateCreate = "";
    String dateUpdate = "";
    String dateStart = "";
    String dateEnd = "";
    String description = "";
    String projectDuration = "";

    private String urlReadProjectMedia = MyConfiguration.DOMAIN + MyConfiguration.VERSION + MyConfiguration.READ_PROJECT_MEDIA;
    private ArrayList<String> mediaUrlList;
    private ArrayList<String> mediaNameList;
    private ArrayList<String> mediaImageList;
    private ArrayList<String> mediaSizeList;
    private ArrayList<String> mediaDurationList;
    private ArrayList<Boolean> mediaIsPlayingList;

    String folderName = MyConfiguration.FOLDER_AKO555 + File.separator + MyConfiguration.FOLDER_PRIVATE_SPOT;
    String folderTempName = MyConfiguration.FOLDER_AKO555 + File.separator + MyConfiguration.FOLDER_PUBLIC_SPOT_TEMP;
    private DownloadManager downloadManager;
    private long downloadReference;
    ArrayList<String> files;
    ArrayList<String> videoOnlineNameList;
    ArrayList<String> presentationList;
    ArrayList<Boolean> downloadStatusList;

    private static File root;
    private File temp;

    private String prefixPath = "";
    private String filePath = "";
    private int indexMediaPlaying = 0;

    private AppPreference appPreference;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.project_info);

        appPreference = new AppPreference(this);

        extras = getIntent().getExtras();
        projectID = extras.getString("projectID");
        projectTitle = extras.getString("projectTitle");
        dateCreate = extras.getString("dateCreate");
        dateUpdate = extras.getString("dateUpdate");
        dateStart = extras.getString("dateStart");
        dateEnd = extras.getString("dateEnd");
        description = extras.getString("description");
        projectDuration = dateStart + " ถึง " + dateEnd;

        tf = Typeface.createFromAsset(getAssets(), "fonts/rsu-light.ttf");

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        txtTitle = (TextView) findViewById(R.id.txtTitle);
        txtProjectTitle = (TextView) findViewById(R.id.txtProjectTitle);
        txtDateCreateTitle = (TextView) findViewById(R.id.txtDateCreateTitle);
        txtDateCreate = (TextView) findViewById(R.id.txtDateCreate);
        txtDateUpdateTitle = (TextView) findViewById(R.id.txtDateUpdateTitle);
        txtDateUpdate = (TextView) findViewById(R.id.txtDateUpdate);
        txtDateStartTitle = (TextView) findViewById(R.id.txtDateStartTitle);
        txtDateStart = (TextView) findViewById(R.id.txtDateStart);
        txtDescriptionTitle = (TextView) findViewById(R.id.txtDescriptionTitle);
        txtDescription = (TextView) findViewById(R.id.txtDescription);
        txtMediaTitle = (TextView) findViewById(R.id.txtMediaTitle);
        txtTotalFileTitle = (TextView) findViewById(R.id.txtTotalFileTitle);
        txtTotalFile = (TextView) findViewById(R.id.txtTotalFile);
        txtFileSizeTitle = (TextView) findViewById(R.id.txtFileSizeTitle);
        txtFileSize = (TextView) findViewById(R.id.txtFileSize);
        txtDurationTitle = (TextView) findViewById(R.id.txtDurationTitle);
        txtDuration = (TextView) findViewById(R.id.txtDuration);
        txtCustomerListTitle = (TextView) findViewById(R.id.txtCustomerListTitle);
        txtCustomer = (TextView) findViewById(R.id.txtCustomer);

        txtTitle.setTypeface(tf);
        txtProjectTitle.setTypeface(tf);
        txtDateCreateTitle.setTypeface(tf);
        txtDateCreate.setTypeface(tf);
        txtDateUpdateTitle.setTypeface(tf);
        txtDateUpdate.setTypeface(tf);
        txtDateStartTitle.setTypeface(tf);
        txtDateStart.setTypeface(tf);
        txtDescriptionTitle.setTypeface(tf);
        txtDescription.setTypeface(tf);
        txtMediaTitle.setTypeface(tf);
        txtTotalFileTitle.setTypeface(tf);
        txtTotalFile.setTypeface(tf);
        txtFileSizeTitle.setTypeface(tf);
        txtFileSize.setTypeface(tf);
        txtDurationTitle.setTypeface(tf);
        txtDuration.setTypeface(tf);
        txtCustomerListTitle.setTypeface(tf);
        txtCustomer.setTypeface(tf);

        txtProjectTitle.setText(projectTitle);
        txtDateCreate.setText(dateCreate);
        txtDateUpdate.setText(dateUpdate);
        txtDateStart.setText(projectDuration);
        txtDescription.setText(description);

        btnMedia = (ImageView) findViewById(R.id.btnMedia);
        btnMedia.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent();
                intent.setClassName(getPackageName(), getPackageName() + ".AKO555MediaList");
                intent.putExtra("folderName", folderName);
                intent.putStringArrayListExtra("mediaName", mediaNameList);
                intent.putStringArrayListExtra("mediaImage", mediaImageList);
                intent.putStringArrayListExtra("mediaUrl", mediaUrlList);
                startActivity(intent);
            }
        });

        btnEdit = (ImageView) findViewById(R.id.btnEdit);
        btnEdit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent();
                intent.setClassName(getPackageName(), getPackageName() + ".AKO555ProjectEdit");
                intent.putExtra("projectName", projectTitle);
                intent.putExtra("description", description);
                startActivity(intent);
            }
        });

        downloadManager = (DownloadManager) this.getSystemService(this.DOWNLOAD_SERVICE);
        this.getApplicationContext().registerReceiver(downloadReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        root = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + folderName);
        root.mkdirs();

        temp = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + folderTempName);
        temp.mkdirs();

        Log.d("AKO555", "projectID: " + projectID);
        progressBar.setVisibility(View.VISIBLE);
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setProtocols(Arrays.asList(Protocol.HTTP_1_1));

        Request.Builder builder = new Request.Builder();
        RequestBody formBody = new FormEncodingBuilder()
                .add("project-id", projectID)
                .add("-system-user-id", appPreference.getSystemUserMobileID())
                .add("-app-version", appPreference.getAppVersion())
                .build();
        Request request = builder.url(urlReadProjectMedia).post(formBody).build();
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
                        setProjectInfo(result);
                    }
                });
            }
        });
    }

    public void setProjectInfo(String result) {
        // TODO Auto-generated method stub
        progressBar.setVisibility(View.GONE);

        mediaNameList = new ArrayList<String>();
        mediaImageList = new ArrayList<String>();
        mediaSizeList = new ArrayList<String>();
        mediaDurationList = new ArrayList<String>();
        mediaUrlList = new ArrayList<String>();
        mediaIsPlayingList = new ArrayList<Boolean>();

        try {
            JSONObject jObj = new JSONObject(result);
            JSONArray jArrayData = jObj.getJSONArray("data");
            for(int x=0; x<jArrayData.length(); x++) {
                mediaNameList.add(jArrayData.getJSONObject(x).getJSONObject("media").getString("title-text"));
                mediaImageList.add(jArrayData.getJSONObject(x).getJSONObject("media").getString("title-image"));
                mediaSizeList.add(jArrayData.getJSONObject(x).getJSONObject("media").getString("size"));
                mediaDurationList.add(jArrayData.getJSONObject(x).getJSONObject("media").getString("duration"));
                mediaUrlList.add(jArrayData.getJSONObject(x).getJSONObject("media").getString("url"));
                mediaIsPlayingList.add(false);
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        txtTotalFile.setText("" + mediaNameList.size());

        String fileSize = "";
        double size = 0;
        String unit = "";
        for(int x=0; x<mediaSizeList.size(); x++) {
            size += Double.parseDouble(mediaSizeList.get(x));
        }

        if(size >= 1000000000) {
            fileSize = "" + (size/1000000000);
            unit = " GB";
        } else if(size >= 1000000) {
            fileSize = "" + (size/1000000);
            unit = " MB";
        } else if(size >= 1000) {
            fileSize = "" + (size/1000);
            unit = " KB";
        }
        if(fileSize.replace(" ", "").equals("")) {
            fileSize = "0.00";
        }
        double fs = Double.parseDouble(fileSize);
        DecimalFormat d = new DecimalFormat("#.##");
        String dx = d.format(fs);
        fs = Double.valueOf(dx);
        txtFileSize.setText("" + fs + unit);

        String fileDuration = "";
        double duration = 0;
        for(int x=0; x<mediaDurationList.size(); x++) {
            duration += Double.parseDouble(mediaDurationList.get(x));
        }
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        fileDuration = df.format(duration- TimeZone.getDefault().getRawOffset());
        txtDuration.setText(fileDuration);

        prefixPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + folderName + File.separator;
        adapter = new AKO555MediaListAdapter(this);
        adapter.setNameList(mediaNameList);
        adapter.setMediaIsPlayingList(mediaIsPlayingList);
        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                // TODO Auto-generated method stub
                if(mediaIsPlayingList.get(arg2)) {
                    mediaPlayer.pause();
                    mediaIsPlayingList.set(arg2, false);
                } else {
                    Intent intent = new Intent();
                    intent.setClassName(getPackageName(), getPackageName() + ".AKO555MediaPlayer");
                    //intent.putExtra("media", videoOfflineNameArray[arg2]);
                    //intent.putExtra("videoOfflineNameArray", videoOfflineNameArray);
                    //intent.putExtra("index", arg2);
                    intent.putExtra("prefixPath", folderName);
                    intent.putExtra("spotName", mediaNameList.get(arg2));
                    //startActivity(intent);

                    filePath = prefixPath + mediaNameList.get(arg2);
                    setMediaPlayer(filePath);
                    mediaIsPlayingList.set(indexMediaPlaying, false);
                    mediaIsPlayingList.set(arg2, true);
                    indexMediaPlaying = arg2;
                }
                adapter.notifyDataSetChanged();
                listView.invalidateViews();
            }
        });

        prepareMediaDownload();
    }

    public void prepareMediaDownload() {
        boolean isDuplicate = false;
        String fileName = "";
        String[] videoOfflineNameArray = root.list();
        String[] videoTempNameArray = root.list();
        videoOnlineNameList = null;
        videoOnlineNameList = new ArrayList<String>();
        presentationList = null;
        presentationList = new ArrayList<String>();
        downloadStatusList = null;
        downloadStatusList = new ArrayList<Boolean>();

        for(int x=0; x<mediaUrlList.size(); x++) {
            fileName = mediaUrlList.get(x).split("/")[mediaUrlList.get(x).split("/").length-1];
            if(fileName.toLowerCase().endsWith(".mp4") || fileName.toLowerCase().endsWith(".avi") || fileName.toLowerCase().endsWith(".3gp") || fileName.toLowerCase().endsWith(".wmv") || fileName.toLowerCase().endsWith(".mp3") || fileName.toLowerCase().endsWith(".wav")) {
                videoOnlineNameList.add(mediaNameList.get(x));
                downloadStatusList.add(true);
            } else {
                Log.d("AKO555", "");
            }
        }

        /*for(int x=0; x<videoOfflineNameArray.length; x++) {
            for(int y=0; y<videoOnlineNameList.size(); y++) {
                if(videoOnlineNameList.get(y).equals(videoOfflineNameArray[x])) {
                    downloadStatusList.set(y, false);
                }
            }
        }*/

        for(int x=0; x<videoOnlineNameList.size(); x++) {
            for(int y=0; y<videoOfflineNameArray.length; y++) {
                if(videoOnlineNameList.get(x).equals(videoOfflineNameArray[y])) {
                    downloadStatusList.set(x, false);
                }
            }
        }

        for(int x=0; x<downloadStatusList.size(); x++) {
            if(downloadStatusList.get(x)) {
                listView.setEnabled(false);
                //String videoURL = "http://58.64.37.251/ako555content/content/media/" + Uri.encode(videoOnlineNameList.get(x));
                String videoURL = mediaUrlList.get(x);
                downloadManager = (DownloadManager) this.getSystemService(this.DOWNLOAD_SERVICE);
                //Uri Download_Uri = Uri.parse(videoURL + presentationList.get(arg2));
                Uri Download_Uri = Uri.parse(videoURL);
                Log.d("AKO555", "videoURL: " + videoURL);
                DownloadManager.Request request = new DownloadManager.Request(Download_Uri);

                //request.setMimeType(Mim)

                //Restrict the types of networks over which this download may proceed.
                request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
                //Set whether this download may proceed over a roaming connection.
                request.setAllowedOverRoaming(false);
                //Set the title of this download, to be displayed in notifications (if enabled).
                //request.setTitle(videoOnlineNameList.get(x));
                request.setTitle(mediaNameList.get(x));
                //Set a description of this download, to be displayed in notifications (if enabled)
                //request.setDescription("Android Data download using DownloadManager.");
                //request.setDescription("Download: " + presentationList.get(arg2));
                //request.setDescription("Download: " + videoOnlineNameList.get(x));
                request.setDescription("Download: " + mediaNameList.get(x));
                //Set the local destination for the downloaded file to a path within the application's external files directory
                //request.setDestinationInExternalFilesDir(PCConsultPresentation.this, Environment.DIRECTORY_DOWNLOADS, presentationList.get(arg2));
                //request.setDestinationInExternalPublicDir(File.separator + folderName, presentationList.get(arg2));
                request.setDestinationInExternalPublicDir(File.separator + folderName, mediaNameList.get(x));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
                }
                request.setVisibleInDownloadsUi(true);
                Log.d("AKO555", "Path: " + root.getPath());

                //Enqueue a new download and same the referenceId
                downloadReference = downloadManager.enqueue(request);
            }
        }
    }

    private BroadcastReceiver downloadReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (!DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                //Log.d("PCConsult", "Download");
                return;
            }
            //getActivity().getApplicationContext().unregisterReceiver(downloadReceiver);
            //getActivity().unregisterReceiver(downloadReceiver);
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(downloadReference);
            Cursor c = downloadManager.query(query);
            if (c.moveToFirst()) {
                int columnIndex = c.getColumnIndex(DownloadManager.COLUMN_STATUS);
                if (DownloadManager.STATUS_SUCCESSFUL == c.getInt(columnIndex)) {
                    String uriString = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                    Log.i("AKO555", "downloaded file " + uriString);
                    Intent videoIntent = new Intent();
                    videoIntent.setAction(Intent.ACTION_VIEW);
                    videoIntent.setDataAndType(Uri.parse(uriString), "video/*");
                    Log.d("AKO555", "File: " + Uri.parse(uriString));
                    //startActivity(videoIntent);
                    //onResume();
                    listView.setEnabled(true);
                } else {
                    Log.i("PCConsult", "download failed " + c.getInt(columnIndex));
                }
            }
        }
    };

    public void setMediaPlayer(String filePath) {
        Log.d("AKO555", "FilePath: " + filePath);
        //mediaPlayer = null;
        //mediaPlayer = AKO555Home.mediaPlayer;
		/*if(mediaPlayer == null) {
			mediaPlayer = new MediaPlayer();
			Log.d("AKO555", "new MediaPlayer()");
		}*/
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.reset();
        }
        mediaPlayer = MediaPlayer.create(this, Uri.parse(filePath));
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
    }

    // handler to change seekBarTime
    private Runnable updateSeekBarTime = new Runnable() {
        public void run() {
            try {
                // get current position
                timeElapsed = mediaPlayer.getCurrentPosition();
                // set seekbar progress
                seekbar.setProgress((int) timeElapsed);
                // set time remaing
                double timeRemaining = finalTime - timeElapsed;
                duration.setText(String.format(
                        "%d min, %d sec",
                        TimeUnit.MILLISECONDS.toMinutes((long) timeRemaining),
                        TimeUnit.MILLISECONDS.toSeconds((long) timeRemaining)
                                - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
                                .toMinutes((long) timeRemaining))));

                // repeat yourself that again in 100 miliseconds
                durationHandler.postDelayed(this, 100);
            } catch(Exception e) {

            }
        }
    };

    // play mp3 song
    public void play(View view) {
        mediaPlayer.start();
        timeElapsed = mediaPlayer.getCurrentPosition();
        seekbar.setProgress((int) timeElapsed);
        //durationHandler.postDelayed(updateSeekBarTime, 100);

        //media_play.setBackgroundResource(R.drawable.custom_blue_button);
        //media_play.invalidate();

        //media_pause.setBackgroundResource(R.drawable.custom_green_button);
        //media_pause.invalidate();
    }

    // pause mp3 song
    public void pause(View view) {
        mediaPlayer.pause();
        //media_play.setBackgroundResource(R.drawable.custom_green_button);
        //media_play.invalidate();

        //media_pause.setBackgroundResource(R.drawable.custom_blue_button);
        //media_pause.invalidate();
    }
}
