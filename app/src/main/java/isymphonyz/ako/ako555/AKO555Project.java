package isymphonyz.ako.ako555;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;

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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import isymphonyz.ako.ako555.adapter.AKO555ProjectListAdapter;
import isymphonyz.ako.ako555.utils.AppPreference;
import isymphonyz.ako.ako555.utils.MyConfiguration;

public class AKO555Project extends AppCompatActivity {

    private ProgressBar progressBar;
    private ListView listView;
    private AKO555ProjectListAdapter adapter;
    private ImageView btnAdd;

    private ArrayList<Integer> imageList;
    private ArrayList<String> nameList;

    private AppPreference appPreference;

    private String urlProject = MyConfiguration.DOMAIN + MyConfiguration.VERSION + MyConfiguration.READ_PROJECT;
    private ArrayList<String> projectIDList;
    private ArrayList<String> projectTitleList;
    private ArrayList<String> projectTrackIntervalLocationList;
    private ArrayList<String> projectDateCreateList;
    private ArrayList<String> projectDateUpdateList;
    private ArrayList<String> projectDateStartList;
    private ArrayList<String> projectDateEndList;
    private ArrayList<String> projectDescriptionList;
    private ArrayList<String> projectNumberOfMediaList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.project);

        appPreference = new AppPreference(this);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        listView = (ListView) findViewById(R.id.listView);
        adapter = new AKO555ProjectListAdapter(this);

        btnAdd = (ImageView) findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClassName(getPackageName(), getPackageName() + ".AKO555ProjectCreate");
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getProject();
    }

    private void getProject() {
        Log.d("AKO555", "device-id: " + appPreference.getDeviceID());
        Log.d("AKO555", "member-id: " + appPreference.getMemberID());
        Log.d("AKO555", "-system-user-id: " + appPreference.getSystemUserMobileID());
        progressBar.setVisibility(View.VISIBLE);
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setProtocols(Arrays.asList(Protocol.HTTP_1_1));

        Request.Builder builder = new Request.Builder();
        RequestBody formBody = new FormEncodingBuilder()
                .add("device-id", appPreference.getDeviceID())
                .add("-system-user-id", appPreference.getSystemUserMobileID())
                .add("-app-version", appPreference.getAppVersion())
                .build();
        Request request = builder.url(urlProject).post(formBody).build();
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
                        setProject(result);
                    }
                });
            }
        });
    }

    public void setProject(String result) {
        Log.d("AKO555", result);
        progressBar.setVisibility(View.GONE);
        if(projectIDList != null) {
            projectIDList.clear();
            projectTitleList.clear();
            projectTrackIntervalLocationList.clear();
            projectDateCreateList.clear();
            projectDateUpdateList.clear();
            projectDateStartList.clear();
            projectDateEndList.clear();
            projectDescriptionList.clear();
            projectNumberOfMediaList.clear();
        } else {
            projectIDList = new ArrayList<String>();
            projectTitleList = new ArrayList<String>();
            projectTrackIntervalLocationList = new ArrayList<String>();
            projectDateCreateList = new ArrayList<String>();
            projectDateUpdateList = new ArrayList<String>();
            projectDateStartList = new ArrayList<String>();
            projectDateEndList = new ArrayList<String>();
            projectDescriptionList = new ArrayList<String>();
            projectNumberOfMediaList = new ArrayList<String>();
        }

        boolean status = false;
        String messageTextInter = "";
        String messageTextLocal = "";
        int messageID = 0;
        try {
            JSONObject jObj = new JSONObject(result);
            status = jObj.getBoolean("status");

            if(status) {
                JSONArray jArrayData = jObj.getJSONArray("data");
                for(int x=0; x<jArrayData.length(); x++) {
                    projectIDList.add(jArrayData.getJSONObject(x).getString("id"));
                    projectTitleList.add(jArrayData.getJSONObject(x).getString("title"));
                    projectTrackIntervalLocationList.add(jArrayData.getJSONObject(x).getString("tracking-interval-location"));
                    projectDateCreateList.add(jArrayData.getJSONObject(x).getString("datetime-create"));
                    projectDateUpdateList.add(jArrayData.getJSONObject(x).getString("datetime-modify"));
                    projectDateStartList.add(jArrayData.getJSONObject(x).getString("date-start"));
                    projectDateEndList.add(jArrayData.getJSONObject(x).getString("date-finish"));
                    projectDescriptionList.add(jArrayData.getJSONObject(x).getString("description"));
                    //projectNumberOfMediaList.add("" + jArrayData.getJSONObject(x).getJSONArray("project-media").length());
                    projectNumberOfMediaList.add("" + jArrayData.getJSONObject(x).getString("project-media"));
                }
            } else {
                JSONObject jObjMessage = jObj.getJSONObject("message");
                messageTextInter = jObjMessage.getString("text-inter");
                messageTextLocal = jObjMessage.getString("text-local");
                messageID = jObjMessage.getInt("id");
            }

            adapter = new AKO555ProjectListAdapter(this);
            adapter.setNameList(projectTitleList);
            listView.setAdapter(adapter);
            listView.invalidateViews();
            adapter.notifyDataSetChanged();
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                        long arg3) {
                    // TODO Auto-generated method stub
                    appPreference.setProjectID(projectIDList.get(arg2));
                    Intent intent = new Intent();
                    intent.setClassName(getPackageName(), getPackageName() + ".AKO555ProjectInfo");
                    intent.putExtra("projectID", projectIDList.get(arg2));
                    intent.putExtra("projectTitle", projectTitleList.get(arg2));
                    intent.putExtra("dateCreate", projectDateCreateList.get(arg2));
                    intent.putExtra("dateUpdate", projectDateUpdateList.get(arg2));
                    intent.putExtra("dateStart", projectDateStartList.get(arg2));
                    intent.putExtra("dateEnd", projectDateEndList.get(arg2));
                    intent.putExtra("description", projectDescriptionList.get(arg2));
                    startActivity(intent);
                }
            });
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
