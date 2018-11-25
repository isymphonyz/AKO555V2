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

public class AKO555Customer extends AppCompatActivity {

    private ProgressBar progressBar;
    private ListView listView;
    private AKO555ProjectListAdapter adapter;
    private ImageView btnAdd;

    private ArrayList<Integer> imageList;
    private ArrayList<String> nameList;

    private AppPreference appPreference;

    private String urlCustomer = MyConfiguration.DOMAIN + MyConfiguration.VERSION + MyConfiguration.READ_CUSTOMERS;
    private ArrayList<String> customerIDList;
    private ArrayList<String> customerFirstnameList;
    private ArrayList<String> customerLastameList;
    private ArrayList<String> customerTaxNoList;
    private ArrayList<String> customerAddressList;
    private ArrayList<String> customerSubDistrictList;
    private ArrayList<String> customerDistrictList;
    private ArrayList<String> customerProvinceList;
    private ArrayList<String> customerPostcodeList;
    private ArrayList<String> customerTelephoneList;
    private ArrayList<String> customerMobileList;
    private ArrayList<String> customerEmailList;
    private ArrayList<String> customerUsernameList;
    private ArrayList<String> customerPasswordList;
    private ArrayList<String> customerDescriptionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer);

        appPreference = new AppPreference(this);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        listView = (ListView) findViewById(R.id.listView);
        adapter = new AKO555ProjectListAdapter(this);

        btnAdd = (ImageView) findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClassName(getPackageName(), getPackageName() + ".AKO555CustomerCreate");
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getCustomer();
    }

    private void getCustomer() {
        Log.d("AKO555", "device-id: " + appPreference.getDeviceID());
        Log.d("AKO555", "member-id: " + appPreference.getMemberID());
        Log.d("AKO555", "-system-user-id: " + appPreference.getSystemUserMobileID());
        progressBar.setVisibility(View.VISIBLE);
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setProtocols(Arrays.asList(Protocol.HTTP_1_1));

        Request.Builder builder = new Request.Builder();
        RequestBody formBody = new FormEncodingBuilder()
                .add("member-id", appPreference.getMemberID())
                .add("-system-user-id", appPreference.getSystemUserMobileID())
                .add("-app-version", appPreference.getAppVersion())
                .build();
        Request request = builder.url(urlCustomer).post(formBody).build();
        Log.d("AKO555", "urlCustomer: " + urlCustomer);
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
                        setCustomer(result);
                    }
                });
            }
        });
    }

    public void setCustomer(String result) {
        Log.d("AKO555", "setCustomer: " + result);
        progressBar.setVisibility(View.GONE);
        if(customerIDList != null) {
            customerIDList.clear();
            customerFirstnameList.clear();
            customerLastameList.clear();
            customerTaxNoList.clear();
            customerAddressList.clear();
            customerSubDistrictList.clear();
            customerDistrictList.clear();
            customerProvinceList.clear();
            customerPostcodeList.clear();
            customerTelephoneList.clear();
            customerMobileList.clear();
            customerEmailList.clear();
            customerUsernameList.clear();
            customerPasswordList.clear();
            customerDescriptionList.clear();
        } else {
            customerIDList = new ArrayList<String>();
            customerFirstnameList = new ArrayList<String>();
            customerLastameList = new ArrayList<String>();
            customerTaxNoList = new ArrayList<String>();
            customerAddressList = new ArrayList<String>();
            customerSubDistrictList = new ArrayList<String>();
            customerDistrictList = new ArrayList<String>();
            customerProvinceList = new ArrayList<String>();
            customerPostcodeList = new ArrayList<String>();
            customerTelephoneList = new ArrayList<String>();
            customerMobileList = new ArrayList<String>();
            customerEmailList = new ArrayList<String>();
            customerUsernameList = new ArrayList<String>();
            customerPasswordList = new ArrayList<String>();
            customerDescriptionList = new ArrayList<String>();
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
                    customerIDList.add(jArrayData.optJSONObject(x).optString("id"));
                    customerFirstnameList.add(jArrayData.optJSONObject(x).optString("firstname"));
                    customerLastameList.add(jArrayData.optJSONObject(x).optString("lastname"));
                    customerTaxNoList.add(jArrayData.optJSONObject(x).optString("tax-no"));
                    customerAddressList.add(jArrayData.optJSONObject(x).optString("address"));
                    if(jArrayData.optJSONObject(x).optJSONObject("subdistrict").has("name-local")) {
                        customerSubDistrictList.add(jArrayData.optJSONObject(x).optJSONObject("subdistrict").optString("name-local"));
                    } else {
                        customerSubDistrictList.add("");
                    }
                    if(jArrayData.optJSONObject(x).optJSONObject("district").has("name-local")) {
                        customerDistrictList.add(jArrayData.optJSONObject(x).optJSONObject("district").optString("name-local"));
                    } else {
                        customerDistrictList.add("");
                    }
                    if(jArrayData.optJSONObject(x).optJSONObject("province").has("name-local")) {
                        customerProvinceList.add(jArrayData.optJSONObject(x).optJSONObject("province").optString("name-local"));
                    } else {
                        customerProvinceList.add("");
                    }
                    //customerSubDistrictList.add(jArrayData.optJSONObject(x).optJSONObject("subdistrict").optString("name-local"));
                    //customerDistrictList.add(jArrayData.optJSONObject(x).optJSONObject("district").optString("name-local"));
                    //customerProvinceList.add(jArrayData.optJSONObject(x).optJSONObject("province").optString("name-local"));
                    //customerSubDistrictList.add(jArrayData.optJSONObject(x).optString("subdistrict"));
                    //customerDistrictList.add(jArrayData.optJSONObject(x).optString("district"));
                    //customerProvinceList.add(jArrayData.optJSONObject(x).optString("province"));
                    customerPostcodeList.add(jArrayData.optJSONObject(x).optString("postcode"));
                    customerTelephoneList.add(jArrayData.optJSONObject(x).optString("phone-home"));
                    customerMobileList.add(jArrayData.optJSONObject(x).optString("phone-mobile"));
                    customerEmailList.add(jArrayData.optJSONObject(x).optString("email"));
                    customerUsernameList.add(jArrayData.optJSONObject(x).optString("username"));
                    customerPasswordList.add(jArrayData.optJSONObject(x).optString("password"));
                    customerDescriptionList.add(jArrayData.optJSONObject(x).optString("description"));
                }
            } else {
                JSONObject jObjMessage = jObj.optJSONObject("message");
                messageTextInter = jObjMessage.optString("text-inter");
                messageTextLocal = jObjMessage.optString("text-local");
                messageID = jObjMessage.getInt("id");
            }

            adapter = new AKO555ProjectListAdapter(this);
            adapter.setNameList(customerFirstnameList);
            listView.setAdapter(adapter);
            listView.invalidateViews();
            adapter.notifyDataSetChanged();
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                        long arg3) {
                    // TODO Auto-generated method stub
                    Intent intent = new Intent();
                    intent.setClassName(getPackageName(), getPackageName() + ".AKO555CustomerInfo");
                    intent.putExtra("customerID", customerIDList.get(arg2));
                    intent.putExtra("customerFirstname", customerFirstnameList.get(arg2));
                    intent.putExtra("customerLastame", customerLastameList.get(arg2));
                    intent.putExtra("customerTaxNo", customerTaxNoList.get(arg2));
                    intent.putExtra("customerAddress", customerAddressList.get(arg2));
                    intent.putExtra("customerSubDistrict", customerSubDistrictList.get(arg2));
                    intent.putExtra("customerDistrict", customerDistrictList.get(arg2));
                    intent.putExtra("customerProvince", customerProvinceList.get(arg2));
                    intent.putExtra("customerPostcode", customerPostcodeList.get(arg2));
                    intent.putExtra("customerTelephone", customerTelephoneList.get(arg2));
                    intent.putExtra("customerMobile", customerMobileList.get(arg2));
                    intent.putExtra("customerEmail", customerEmailList.get(arg2));
                    intent.putExtra("customerUsername", customerUsernameList.get(arg2));
                    intent.putExtra("customerPassword", customerPasswordList.get(arg2));
                    intent.putExtra("customerDescription", customerDescriptionList.get(arg2));
                    startActivity(intent);
                }
            });
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
