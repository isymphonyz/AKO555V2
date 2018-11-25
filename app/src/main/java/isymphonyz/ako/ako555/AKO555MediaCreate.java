package isymphonyz.ako.ako555;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Protocol;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import isymphonyz.ako.ako555.utils.AppPreference;
import isymphonyz.ako.ako555.utils.MyConfiguration;
import isymphonyz.ako.ako555.utils.ToastMessage;

@SuppressLint("NewApi")
public class AKO555MediaCreate extends AppCompatActivity {

    private String TAG = "AKO555MediaCreate";

    private ProgressBar progressBar;
    private EditText inputMediaName;
    private EditText inputDescription;
    private EditText inputMedia;
    private Button btnSubmit;

    private Calendar myCalendar;

    private String mediaName = "";
    private String description = "";
    private String mediaURL = "";

    private String urlMediaCreate = MyConfiguration.DOMAIN + MyConfiguration.VERSION + MyConfiguration.CREATE_MEDIA;

    private static final int FILE_SELECT_CODE = 0;

    private AppPreference appPreference;

    Bundle extras;
    String folderName = "";
    String isPublic = "0";

    private MediaPlayer mediaPlayer;
    private File mediaFile;
    private String mediaType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.media_create);

        extras = getIntent().getExtras();
        folderName = extras.getString("folderName");
        if(folderName.contains("Public")) {
            isPublic = "1";
        } else {
            isPublic = "0";
        }

        appPreference = new AppPreference(this);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        inputMediaName = (EditText) findViewById(R.id.inputMediaName);
        inputDescription = (EditText) findViewById(R.id.inputDescription);
        inputMedia = (EditText) findViewById(R.id.inputMedia);

        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/rsu-light.ttf");
        inputMediaName.setTypeface(tf);
        inputDescription.setTypeface(tf);
        inputMedia.setTypeface(tf);

        inputMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectMediaDialog();
            }
        });

        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btnSubmit.setTypeface(tf);
        btnSubmit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                mediaName = inputMediaName.getText().toString();
                description = inputDescription.getText().toString();
                mediaURL = inputMedia.getText().toString();

                progressBar.setVisibility(View.VISIBLE);
                OkHttpClient okHttpClient = new OkHttpClient();
                okHttpClient.setConnectTimeout(60, TimeUnit.SECONDS);
                okHttpClient.setReadTimeout(60, TimeUnit.SECONDS);
                okHttpClient.setProtocols(Arrays.asList(Protocol.HTTP_1_1));

                Request.Builder builder = new Request.Builder();
                /*RequestBody formBody = new FormEncodingBuilder()
                        .add("-system-user-id", "")
                        .add("-app-version", appPreference.getAppVersion())
                        .add("title-text", mediaName)
                        .add("optional-download", mediaType)
                        .add("description", description)
                        .add("status-public", isPublic)
                        .add("url", mediaURL)
                        .build();*/

                RequestBody formBody;
                if(mediaType.equals(MyConfiguration.MEDIA_TYPE_FILE)) {
                    String fileName = mediaURL.substring(mediaURL.lastIndexOf("/") + 1);
                    Log.d("AKO555", "mediaURL: " + mediaURL);
                    Log.d("AKO555", "fileName: " + fileName);
                    String fileType = "";
                    try {
                        fileType = mediaURL.split("\\.")[1];
                    } catch(Exception e) {
                        fileType = "mp3";
                    }
                    final MediaType MEDIA_TYPE_AUDIO = MediaType.parse("audio/" + fileType);
                    formBody = new MultipartBuilder()
                            .type(MultipartBuilder.FORM)
                            .addFormDataPart("-system-user-id", appPreference.getSystemUserMobileID())
                            .addFormDataPart("-app-version", appPreference.getAppVersion())
                            .addFormDataPart("title-text", mediaName)
                            .addFormDataPart("media-type-id", mediaType)
                            .addFormDataPart("description", description)
                            .addFormDataPart("status-public", isPublic)
                            .addFormDataPart("url", fileName, RequestBody.create(MEDIA_TYPE_AUDIO, mediaFile))
                            .addFormDataPart("optional-download", "0")
                            .addFormDataPart("status", "1")
                            .build();
                } else {
                    formBody = new MultipartBuilder()
                            .type(MultipartBuilder.FORM)
                            .addFormDataPart("-system-user-id", appPreference.getSystemUserMobileID())
                            .addFormDataPart("-app-version", appPreference.getAppVersion())
                            .addFormDataPart("title-text", mediaName)
                            .addFormDataPart("media-type-id", mediaType)
                            .addFormDataPart("description", description)
                            .addFormDataPart("status-public", isPublic)
                            .addFormDataPart("url", mediaURL)
                            .addFormDataPart("optional-download", "1")
                            .addFormDataPart("status", "1")
                            .build();
                }
                Request request = builder.url(urlMediaCreate).post(formBody).build();
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
                                setMediaCreate(result);
                            }
                        });
                    }
                });
            }
        });
    }

    public void setMediaCreate(String result) {
        // TODO Auto-generated method stub
        Log.d(TAG, "setMediaCreate: " + result);
        progressBar.setVisibility(View.GONE);
        boolean status = false;
        String messageTextInter = "";
        String messageTextLocal = "";
        int messageID = 0;
        try {
            JSONObject jObj = new JSONObject(result);
            status = jObj.getBoolean("status");

            if(status) {
                ToastMessage.show(this, getString(R.string.register_txt_success).toString());
                finish();
            } else {
                JSONObject jObjMessage = jObj.getJSONObject("error");
                messageTextInter = jObjMessage.getString("text-inter");
                messageTextLocal = jObjMessage.getString("text-local");
                messageID = jObjMessage.getInt("id");
                ToastMessage.show(this, messageTextLocal);
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void showSelectMediaDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getText(R.string.media_create_dialog_txt_title));
        builder.setPositiveButton(getText(R.string.media_create_dialog_txt_file), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                showFilePickerDialog();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(getText(R.string.media_create_dialog_txt_url), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showInputURLDialog();
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void showFilePickerDialog() {
        mediaType = MyConfiguration.MEDIA_TYPE_FILE;
        Intent intent2 = new Intent(Intent.ACTION_GET_CONTENT);
        intent2.setType("*/*");
        intent2.addCategory(Intent.CATEGORY_OPENABLE);

        /*try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Upload"),
                    FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(this, "Please install a File Manager.",
                    Toast.LENGTH_SHORT).show();
        }*/

        if (Build.VERSION.SDK_INT <19){
            Intent intent = new Intent();
            intent.setType("audio/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            //startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.media_create_dialog_txt_title)),FILE_SELECT_CODE);
        } else {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("audio/*");
            //startActivityForResult(intent, FILE_SELECT_CODE);
        }

        DialogProperties properties = new DialogProperties();
        properties.selection_mode= DialogConfigs.SINGLE_MODE;
        properties.selection_type=DialogConfigs.FILE_SELECT;
        //properties.root=new File(DialogConfigs.DIRECTORY_SEPERATOR);
        properties.root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        properties.error_dir=new File(DialogConfigs.DEFAULT_DIR);
        properties.extensions=null;
        FilePickerDialog dialog = new FilePickerDialog(this, properties);
        dialog.setTitle("Select a File");
        dialog.setDialogSelectionListener(new DialogSelectionListener() {
            @Override
            public void onSelectedFilePaths(String[] files) {
                //files is the array of the paths of files selected by the Application User.
                mediaFile = new File(files[0]);
                inputMedia.setText(files[0]);
            }
        });
        dialog.show();
    }

    private void showInputURLDialog() {
        mediaType = MyConfiguration.MEDIA_TYPE_INTERNET;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /*switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    // Get the Uri of the selected file
                    Uri uri = data.getData();
                    Log.d(TAG, "File Uri: " + uri.toString());
                    // Get the path
                    String path = null;
                    try {
                        path = FileUtils.getPath(this, uri);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                        Log.d(TAG, "URISyntaxException: " + e.toString());
                    }
                    Log.d(TAG, "File Path1: " + path);
                    Log.d(TAG, "File Path2: " + uri.getPath());

                    mediaURL = uri.getPath();
                    inputMedia.setText(mediaURL);

                    Uri mediaUri = Uri.parse(uri.toString());
                    Log.d("AKO555", "mediaUri: " + mediaUri.toString());
                    Log.d("AKO555", "getDataString: " + data.getDataString());
                    //mediaFile = new File(mediaUri.getPath());

                    Uri testUri = data.getData();
                    String[] projection = { MediaStore.Audio.Media.DATA };

                    Cursor cursor = getContentResolver().query(testUri, projection, null, null, null);
                    cursor.moveToFirst();

                    Log.d(TAG, DatabaseUtils.dumpCursorToString(cursor));

                    int columnIndex = cursor.getColumnIndex(projection[0]);
                    String picturePath = cursor.getString(columnIndex); // returns null
                    cursor.close();
                    Log.d("AKO555", "picturePath: " + picturePath);
                    mediaFile = new File(picturePath);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);*/
        /*if (resultCode != Activity.RESULT_OK) return;
        if (null == data) return;
        Uri originalUri = null;
        if (requestCode == FILE_SELECT_CODE) {
            originalUri = data.getData();
        } else if (requestCode == FILE_SELECT_CODE) {
            originalUri = data.getData();
            final int takeFlags = data.getFlags()
                    & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            // Check for the freshest data.
            getContentResolver().takePersistableUriPermission(originalUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);

            String id = originalUri.getLastPathSegment().split(":")[1];
            final String[] imageColumns = {MediaStore.Images.Media.DATA };
            final String imageOrderBy = null;
        }*/
    }
}
