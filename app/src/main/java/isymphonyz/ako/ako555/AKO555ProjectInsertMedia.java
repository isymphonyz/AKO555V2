package isymphonyz.ako.ako555;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Protocol;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import isymphonyz.ako.ako555.adapter.AKO555ProjectInsertCustomerAdapter;
import isymphonyz.ako.ako555.customview.RSUTextView;
import isymphonyz.ako.ako555.utils.AppPreference;
import isymphonyz.ako.ako555.utils.MyConfiguration;
import isymphonyz.ako.ako555.utils.ToastMessage;

public class AKO555ProjectInsertMedia extends AppCompatActivity {

    private ProgressBar progressBar;
    private RSUTextView inputProjectName;
    private ListView listView;
    private AKO555ProjectInsertCustomerAdapter adapter;
    private Button btnSubmit;
    private Typeface tf;

    private String urlReadMedia = MyConfiguration.DOMAIN + MyConfiguration.VERSION + MyConfiguration.READ_MEDIA;
    private String urlInsertProjectMedia = MyConfiguration.DOMAIN + MyConfiguration.VERSION + MyConfiguration.INSERT_PROJECT_MEDIA;

    private ArrayList<String> mediaIDList;
    private ArrayList<String> mediaNameList;
    private ArrayList<Boolean> mediaStatusList;

    private AppPreference appPreference;
    private Bundle extras;
    private String projectID;
    private String projectName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.project_insert_media);

        appPreference = new AppPreference(this);

        extras = getIntent().getExtras();
        projectID = extras.getString("projectID");
        projectName = extras.getString("projectName");

        mediaIDList = new ArrayList<>();
        mediaNameList = new ArrayList<>();
        mediaStatusList = new ArrayList<>();

        initUI();
        setUI();

        getMedia();
    }

    private void initUI() {
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        inputProjectName = (RSUTextView) findViewById(R.id.inputProjectName);
        listView = (ListView) findViewById(R.id.listView);
        adapter = new AKO555ProjectInsertCustomerAdapter(this);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);

        tf = Typeface.createFromAsset(getAssets(), "fonts/rsu-light.ttf");
    }

    private void setUI() {
        inputProjectName.setText(projectName);

        btnSubmit.setTypeface(tf);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaStatusList.indexOf(true) >= 0) {
                    insertMedia();
                } else {
                    ToastMessage.show(getApplicationContext(), getText(R.string.project_insert_customer_txt_select_atleast_one).toString());
                }
            }
        });
    }

    private void getMedia() {
        progressBar.setVisibility(View.VISIBLE);
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setProtocols(Arrays.asList(Protocol.HTTP_1_1));

        Request.Builder builder = new Request.Builder();
        RequestBody formBody = new FormEncodingBuilder()
                .add("-app-version", appPreference.getAppVersion())
                .add("-system-user-id", appPreference.getSystemUserMobileID())
                .build();
        Request request = builder.url(urlReadMedia).post(formBody).build();
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
                        setMedia(result);
                    }
                });
            }
        });
    }

    private void setMedia(String result) {
        // TODO Auto-generated method stub
        Log.d("AKO555", "setMedia: " + result);
        progressBar.setVisibility(View.GONE);
        boolean status = false;
        String messageTextInter = "";
        String messageTextLocal = "";
        int messageID = 0;
        try {
            JSONObject jObj = new JSONObject(result);
            status = jObj.getBoolean("status");

            if(status) {
                mediaIDList.clear();
                mediaNameList.clear();
                mediaStatusList.clear();

                JSONArray jArray = jObj.optJSONArray("data");
                for(int x=0; x<jArray.length(); x++) {
                    mediaIDList.add(jArray.optJSONObject(x).optString("id"));
                    mediaNameList.add(jArray.optJSONObject(x).optString("title-text"));
                    mediaStatusList.add(false);
                }

                adapter.setNameList(mediaNameList);
                adapter.setStatusList(mediaStatusList);
                adapter.notifyDataSetChanged();
                listView.setAdapter(adapter);
                listView.invalidateViews();
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

    private void insertMedia() {
        progressBar.setVisibility(View.VISIBLE);
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setProtocols(Arrays.asList(Protocol.HTTP_1_1));

        Request.Builder builder = new Request.Builder();
        RequestBody formBody = getRequestBody();

        Request request = builder.url(urlInsertProjectMedia).post(formBody).build();
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
                        setInsertMedia(result);
                    }
                });
            }
        });
    }

    private void setInsertMedia(String result) {
        Log.d("AKO555", "setInsertMedia: " + result);
        progressBar.setVisibility(View.GONE);

        boolean status = false;
        String messageTextInter = "";
        String messageTextLocal = "";
        int messageID = 0;
        try {
            JSONObject jObj = new JSONObject(result);
            status = jObj.getBoolean("status");

            if(status) {
                Intent intent = new Intent();
                intent.setClassName(getPackageName(), getPackageName() + ".AKO555ProjectInsertDevice");
                intent.putExtra("projectID", projectID);
                intent.putExtra("projectName", inputProjectName.getText().toString());
                startActivity(intent);
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

    public RequestBody getRequestBody() {
        int index = 0;
        JSONObject jObj = new JSONObject();
        JSONArray jArrayData = new JSONArray();
        for(int x=0; x<mediaStatusList.size(); x++) {
            try {
                if(mediaStatusList.get(x)) {
                    JSONObject jObjData = new JSONObject();
                    jObjData.put("project-id", projectID);
                    jObjData.put("media-id", mediaIDList.get(x));
                    jArrayData.put(jObjData);
                }
                jObj.put("data", jArrayData);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            /*if(customerStatusList.get(x)) {
                multipartBuilder.addFormDataPart("data", "project-id[" + index + "][" + projectID + "]");
                multipartBuilder.addFormDataPart("data", "customer-id[" + index + "][" + customerIDList.get(x) + "]");
                index++;
            }*/
        }
        Log.d("AKO555", "jObj.toString(): " + jObj.toString());

        /*RequestBody requestBody = new MultipartBuilder().type(MultipartBuilder.FORM)
                .addFormDataPart("project-id", "" + 0)
                .addFormDataPart("title", inputSpotTitle.getText().toString())
                .addFormDataPart("description", inputSpotDescription.getText().toString())
                .addFormDataPart("-system-user-id", appPreference.getSystemUserMobileID())
                .addFormDataPart("media-request-image[]", "file.png", RequestBody.create(MediaType.parse("image/jpg"), getFile()))
                .build();*/
        MultipartBuilder multipartBuilder = new MultipartBuilder().type(MultipartBuilder.FORM);
        multipartBuilder.addFormDataPart("-app-version", appPreference.getAppVersion())
                .addFormDataPart("-system-user-id", appPreference.getSystemUserMobileID())
                .addFormDataPart("data", jArrayData.toString());

        RequestBody formBody = new FormEncodingBuilder()
                .add("-app-version", appPreference.getAppVersion())
                .add("-system-user-id", appPreference.getSystemUserMobileID())
                .add("data", jArrayData.toString())
                .build();

        //RequestBody requestBody = multipartBuilder.build();
        return formBody;
    }
}
