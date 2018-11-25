package isymphonyz.ako.ako555;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;

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
import java.util.Calendar;

import isymphonyz.ako.ako555.adapter.AKO555ArrayAdapter;
import isymphonyz.ako.ako555.utils.AppPreference;
import isymphonyz.ako.ako555.utils.MyConfiguration;
import isymphonyz.ako.ako555.utils.ToastMessage;

public class AKO555CustomerCreate extends AppCompatActivity {

    private ProgressBar progressBar;
    private EditText inputFirstname;
    private EditText inputLastname;
    private EditText inputCitizenID;
    private EditText inputAddress;
    private Spinner inputTumbon;
    private Spinner inputAmphur;
    private Spinner inputProvince;
    private EditText inputPostcode;
    private EditText inputTelephone;
    private EditText inputMobile;
    private EditText inputEmail;
    private EditText inputUsername;
    private EditText inputPassword;
    private EditText inputDescription;
    private Button btnSubmit;
    private AKO555ArrayAdapter adapterProvince;
    private AKO555ArrayAdapter adapterDistrict;
    private AKO555ArrayAdapter adapterSubDistrict;

    private Calendar myCalendarStartDate;
    private Calendar myCalendarEndDate;

    private String urlCustomerCreate = MyConfiguration.DOMAIN + MyConfiguration.VERSION + MyConfiguration.CREATE_CUSTOMER;
    private String urlProvince = MyConfiguration.DOMAIN + MyConfiguration.VERSION + MyConfiguration.PROVINCE;
    private String urlDistrict = MyConfiguration.DOMAIN + MyConfiguration.VERSION + MyConfiguration.DISTRICT;
    private String urlSubDistrict = MyConfiguration.DOMAIN + MyConfiguration.VERSION + MyConfiguration.SUBDISTRICT;

    private ArrayList<String> provinceIDList;
    private ArrayList<String> provinceNameList;
    private ArrayList<String> districtIDList;
    private ArrayList<String> districtNameList;
    private ArrayList<String> subDistrictIDList;
    private ArrayList<String> subDistrictNameList;

    private AppPreference appPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_create);

        appPreference = new AppPreference(this);

        provinceIDList = new ArrayList<>();
        provinceNameList = new ArrayList<>();
        districtIDList = new ArrayList<>();
        districtNameList = new ArrayList<>();
        subDistrictIDList = new ArrayList<>();
        subDistrictNameList = new ArrayList<>();

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        inputFirstname = (EditText) findViewById(R.id.inputFirstname);
        inputLastname = (EditText) findViewById(R.id.inputLastname);
        inputCitizenID = (EditText) findViewById(R.id.inputCitizenID);
        inputAddress = (EditText) findViewById(R.id.inputAddress);
        inputTumbon = (Spinner) findViewById(R.id.inputTumbon);
        inputAmphur = (Spinner) findViewById(R.id.inputAmphur);
        inputProvince = (Spinner) findViewById(R.id.inputProvince);
        inputPostcode = (EditText) findViewById(R.id.inputPostcode);
        inputTelephone = (EditText) findViewById(R.id.inputTelephone);
        inputMobile = (EditText) findViewById(R.id.inputMobile);
        inputEmail = (EditText) findViewById(R.id.inputEmail);
        inputUsername = (EditText) findViewById(R.id.inputUsername);
        inputPassword = (EditText) findViewById(R.id.inputPassword);
        inputDescription = (EditText) findViewById(R.id.inputDescription);

        adapterProvince = new AKO555ArrayAdapter(this);
        adapterDistrict = new AKO555ArrayAdapter(this);
        adapterSubDistrict = new AKO555ArrayAdapter(this);

        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/rsu-light.ttf");
        inputFirstname.setTypeface(tf);
        inputLastname.setTypeface(tf);
        inputCitizenID.setTypeface(tf);
        inputAddress.setTypeface(tf);
        inputPostcode.setTypeface(tf);
        inputTelephone.setTypeface(tf);
        inputMobile.setTypeface(tf);
        inputEmail.setTypeface(tf);
        inputUsername.setTypeface(tf);
        inputPassword.setTypeface(tf);
        inputDescription.setTypeface(tf);

        inputProvince.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getDistrict(provinceIDList.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        inputAmphur.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getSubDistrict(provinceIDList.get(inputProvince.getSelectedItemPosition()), districtIDList.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btnSubmit.setTypeface(tf);
        btnSubmit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                String firstName = inputFirstname.getText().toString();
                String lastName = inputLastname.getText().toString();
                String citizenID = inputCitizenID.getText().toString();
                String address = inputAddress.getText().toString();
                String tumbonID = subDistrictIDList.get(inputTumbon.getSelectedItemPosition());
                String amphurID = districtIDList.get(inputAmphur.getSelectedItemPosition());
                String provinceID = provinceIDList.get(inputProvince.getSelectedItemPosition());
                String postcode = inputPostcode.getText().toString();
                String telephone = inputTelephone.getText().toString();
                String mobile = inputMobile.getText().toString();
                String email = inputEmail.getText().toString();
                String username = inputUsername.getText().toString();
                String password = inputPassword.getText().toString();
                String description = inputDescription.getText().toString();

                progressBar.setVisibility(View.VISIBLE);
                OkHttpClient okHttpClient = new OkHttpClient();
                okHttpClient.setProtocols(Arrays.asList(Protocol.HTTP_1_1));

                Request.Builder builder = new Request.Builder();
                RequestBody formBody = new FormEncodingBuilder()
                        .add("-system-user-id", appPreference.getSystemUserMobileID())
                        .add("-app-version", appPreference.getAppVersion())
                        .add("member-id", appPreference.getMemberID())
                        .add("firstname", firstName)
                        .add("lastname", lastName)
                        .add("tax-no", citizenID)
                        .add("address", address)
                        .add("subdistrict-id", tumbonID)
                        .add("district-id", amphurID)
                        .add("province-id", provinceID)
                        .add("postcode", postcode)
                        .add("phone-home", telephone)
                        .add("phone-mobile", mobile)
                        .add("email", email)
                        .add("username", username)
                        .add("password", password)
                        .add("description", description)
                        .add("status", "1")
                        .build();
                Request request = builder.url(urlCustomerCreate).post(formBody).build();
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
                                setCustomerCreate(result);
                            }
                        });
                    }
                });
            }
        });

        getProvince();
    }

    public void setCustomerCreate(String result) {
        // TODO Auto-generated method stub
        Log.d("AKO555", "setCustomerCreate: " + result);
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

    private void getProvince() {
        Log.d("AKO555", "urlProvince: " + urlProvince);
        progressBar.setVisibility(View.VISIBLE);
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setProtocols(Arrays.asList(Protocol.HTTP_1_1));

        Request.Builder builder = new Request.Builder();
        RequestBody formBody = new FormEncodingBuilder()
                .add("-system-user-id", appPreference.getSystemUserMobileID())
                .add("-app-version", appPreference.getAppVersion())
                .build();
        Request request = builder.url(urlProvince).post(formBody).build();
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
                        setProvince(result);
                    }
                });
            }
        });
    }

    private void setProvince(String result) {
        Log.d("AKO555", "setProvince: " + result);
        progressBar.setVisibility(View.GONE);

        provinceIDList.clear();
        provinceNameList.clear();
        districtIDList.clear();
        districtNameList.clear();
        subDistrictIDList.clear();
        subDistrictNameList.clear();

        boolean status = false;
        String messageTextInter = "";
        String messageTextLocal = "";
        int messageID = 0;
        try {
            JSONObject jObj = new JSONObject(result);
            status = jObj.getBoolean("status");

            if(status) {
                //ToastMessage.show(this, getString(R.string.register_txt_success).toString());
                //finish();
                JSONArray jArray = jObj.optJSONArray("data");
                for(int x=0; x<jArray.length(); x++) {
                    provinceIDList.add(jArray.optJSONObject(x).optString("value"));
                    provinceNameList.add(jArray.optJSONObject(x).optString("name"));
                }

                adapterProvince.setNameList(provinceNameList);
                adapterProvince.notifyDataSetChanged();
                inputProvince.setAdapter(adapterProvince);
                inputProvince.invalidate();
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

    private void getDistrict(String provinceID) {
        Log.d("AKO555", "urlDistrict: " + urlDistrict);
        progressBar.setVisibility(View.VISIBLE);
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setProtocols(Arrays.asList(Protocol.HTTP_1_1));

        Request.Builder builder = new Request.Builder();
        RequestBody formBody = new FormEncodingBuilder()
                .add("-system-user-id", appPreference.getSystemUserMobileID())
                .add("-app-version", appPreference.getAppVersion())
                .add("province-id", provinceID)
                .build();
        Request request = builder.url(urlDistrict).post(formBody).build();
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
                        setDistrict(result);
                    }
                });
            }
        });
    }

    private void setDistrict(String result) {
        Log.d("AKO555", "setDistrict: " + result);
        progressBar.setVisibility(View.GONE);
        districtIDList.clear();
        districtNameList.clear();
        subDistrictIDList.clear();
        subDistrictNameList.clear();

        boolean status = false;
        String messageTextInter = "";
        String messageTextLocal = "";
        int messageID = 0;
        try {
            JSONObject jObj = new JSONObject(result);
            status = jObj.getBoolean("status");

            if(status) {
                //ToastMessage.show(this, getString(R.string.register_txt_success).toString());
                //finish();
                JSONArray jArray = jObj.optJSONArray("data");
                for(int x=0; x<jArray.length(); x++) {
                    districtIDList.add(jArray.optJSONObject(x).optString("value"));
                    districtNameList.add(jArray.optJSONObject(x).optString("name"));
                }

                adapterDistrict.setNameList(districtNameList);
                adapterDistrict.notifyDataSetChanged();
                inputAmphur.setAdapter(adapterDistrict);
                inputAmphur.invalidate();
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

    private void getSubDistrict(String provinceID, String districtID) {
        Log.d("AKO555", "urlSubDistrict: " + urlSubDistrict);
        progressBar.setVisibility(View.VISIBLE);
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setProtocols(Arrays.asList(Protocol.HTTP_1_1));

        Request.Builder builder = new Request.Builder();
        RequestBody formBody = new FormEncodingBuilder()
                .add("-system-user-id", appPreference.getSystemUserMobileID())
                .add("-app-version", appPreference.getAppVersion())
                .add("province-id", provinceID)
                .add("district-id", districtID)
                .build();
        Request request = builder.url(urlSubDistrict).post(formBody).build();
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
                        setSubDistrict(result);
                    }
                });
            }
        });
    }

    private void setSubDistrict(String result) {
        Log.d("AKO555", "setSubDistrict: " + result);
        progressBar.setVisibility(View.GONE);
        subDistrictIDList.clear();
        subDistrictNameList.clear();

        boolean status = false;
        String messageTextInter = "";
        String messageTextLocal = "";
        int messageID = 0;
        try {
            JSONObject jObj = new JSONObject(result);
            status = jObj.getBoolean("status");

            if(status) {
                //ToastMessage.show(this, getString(R.string.register_txt_success).toString());
                //finish();
                JSONArray jArray = jObj.optJSONArray("data");
                for(int x=0; x<jArray.length(); x++) {
                    subDistrictIDList.add(jArray.optJSONObject(x).optString("value"));
                    subDistrictNameList.add(jArray.optJSONObject(x).optString("name"));
                }

                adapterSubDistrict.setNameList(subDistrictNameList);
                adapterSubDistrict.notifyDataSetChanged();
                inputTumbon.setAdapter(adapterSubDistrict);
                inputTumbon.invalidate();
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
}
