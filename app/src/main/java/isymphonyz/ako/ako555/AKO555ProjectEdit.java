package isymphonyz.ako.ako555;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;

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
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;

import isymphonyz.ako.ako555.utils.AppPreference;
import isymphonyz.ako.ako555.utils.MyConfiguration;
import isymphonyz.ako.ako555.utils.ToastMessage;

public class AKO555ProjectEdit extends AppCompatActivity {

    private ProgressBar progressBar;
    private EditText inputProjectName;
    private EditText inputStartDate;
    private EditText inputEndDate;
    private EditText inputDescription;
    private Button btnSubmit;

    private Calendar myCalendarStartDate;
    private Calendar myCalendarEndDate;

    private String projectName = "";
    private String startDate = "";
    private String endDate = "";
    private String description = "";

    private String urlProjectCreate = MyConfiguration.DOMAIN + MyConfiguration.VERSION + MyConfiguration.CREATE_PROJECT;

    private AppPreference appPreference;

    private Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.project_create);

        appPreference = new AppPreference(this);

        extras = getIntent().getExtras();
        projectName = extras.getString("projectName");
        startDate = extras.getString("startDate");
        endDate = extras.getString("endDate");
        description = extras.getString("description");

        myCalendarStartDate = Calendar.getInstance();
        myCalendarEndDate = Calendar.getInstance();

        final DatePickerDialog.OnDateSetListener dateStart = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendarStartDate.set(Calendar.YEAR, year);
                myCalendarStartDate.set(Calendar.MONTH, monthOfYear);
                myCalendarStartDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateStartDate();
            }

        };

        final DatePickerDialog.OnDateSetListener dateEnd = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendarEndDate.set(Calendar.YEAR, year);
                myCalendarEndDate.set(Calendar.MONTH, monthOfYear);
                myCalendarEndDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateEndDate();
            }

        };

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        inputProjectName = (EditText) findViewById(R.id.inputProjectName);
        inputStartDate = (EditText) findViewById(R.id.inputStartDate);
        inputEndDate = (EditText) findViewById(R.id.inputEndDate);
        inputDescription = (EditText) findViewById(R.id.inputDescription);

        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/rsu-light.ttf");
        inputProjectName.setTypeface(tf);
        inputStartDate.setTypeface(tf);
        inputEndDate.setTypeface(tf);
        inputDescription.setTypeface(tf);

        inputProjectName.setText(projectName);
        inputStartDate.setText(startDate);
        inputEndDate.setText(endDate);
        inputDescription.setText(description);

        inputStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(AKO555ProjectEdit.this, dateStart, myCalendarStartDate
                        .get(Calendar.YEAR), myCalendarStartDate.get(Calendar.MONTH),
                        myCalendarStartDate.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        inputEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(AKO555ProjectEdit.this, dateEnd, myCalendarEndDate
                        .get(Calendar.YEAR), myCalendarEndDate.get(Calendar.MONTH),
                        myCalendarEndDate.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btnSubmit.setTypeface(tf);
        btnSubmit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                projectName = inputProjectName.getText().toString();
                startDate = convertDate(inputStartDate.getText().toString());
                endDate = convertDate(inputEndDate.getText().toString());
                description = inputDescription.getText().toString();

                Log.d("AKO555", "startDate: " + startDate);
                Log.d("AKO555", "endDate: " + endDate);

                progressBar.setVisibility(View.VISIBLE);
                OkHttpClient okHttpClient = new OkHttpClient();
                okHttpClient.setProtocols(Arrays.asList(Protocol.HTTP_1_1));

                Request.Builder builder = new Request.Builder();
                RequestBody formBody = new FormEncodingBuilder()
                        .add("-system-user-id", "")
                        .add("-app-version", appPreference.getAppVersion())
                        .add("title", projectName)
                        .add("date-start", startDate)
                        .add("date-finish", endDate)
                        .add("description", description)
                        .build();
                Request request = builder.url(urlProjectCreate).post(formBody).build();
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
                                setProjectCreate(result);
                            }
                        });
                    }
                });
            }
        });
    }

    public void setProjectCreate(String result) {
        // TODO Auto-generated method stub
        Log.d("AKO555", "setProjectCreate: " + result);
        progressBar.setVisibility(View.GONE);
        boolean status = false;
        String messageTextInter = "";
        String messageTextLocal = "";
        int messageID = 0;
        try {
            JSONObject jObj = new JSONObject(result);
            status = jObj.getBoolean("status");

            if(status) {
                //ToastMessage.show(this, getString(R.string.register_txt_success).toString());
                String projectID = jObj.optJSONObject("data").optString("project-id");
                Intent intent = new Intent();
                intent.setClassName(getPackageName(), getPackageName() + ".AKO555ProjectInsertCustomer");
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

    private void updateStartDate() {

        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat);

        String[] date = sdf.format(myCalendarStartDate.getTime()).split("-");
        String year = "" + (Integer.parseInt(date[0]) + 543);
        String month = date[1];
        String day = date[2];
        inputStartDate.setText(day + "-" + month + "-" + year);
    }

    private void updateEndDate() {

        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat);

        String[] date = sdf.format(myCalendarEndDate.getTime()).split("-");
        String year = "" + (Integer.parseInt(date[0]) + 543);
        String month = date[1];
        String day = date[2];
        inputEndDate.setText(day + "-" + month + "-" + year);
    }

    private String convertDate(String oldDate) {
        String[] newDate = oldDate.split("-");
        String year = (Integer.parseInt(newDate[2])-543) + "";
        String date = year + "-" + newDate[1] + "-" + newDate[0];
        return date;
    }
}
