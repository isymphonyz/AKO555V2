package isymphonyz.ako.ako555;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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

import isymphonyz.ako.ako555.adapter.AKO555PaymentListAdapter;
import isymphonyz.ako.ako555.utils.AppPreference;
import isymphonyz.ako.ako555.utils.MyConfiguration;

public class AKO555Payment extends AppCompatActivity {

    private ProgressBar progressBar;
    private ListView listView;
    private AKO555PaymentListAdapter adapter;

    private ArrayList<Integer> imageList;
    private ArrayList<String> nameList;

    private AppPreference appPreference;

    private String urlPayment = MyConfiguration.DOMAIN + MyConfiguration.VERSION + MyConfiguration.READ_PAYMENT;
    private ArrayList<String> paymentIDList;
    private ArrayList<String> paymentTitleList;
    private ArrayList<String> paymentCodeList;
    private ArrayList<String> paymentDescriptionList;
    private ArrayList<String> paymentAmountList;
    private ArrayList<String> paymentDueDateList;
    private ArrayList<String> paymentBarcodeCounterServiceList;
    private ArrayList<String> paymentBarcodeTescoLotusList;
    private ArrayList<String> paymentBarcodeNumberCounterServiceList;
    private ArrayList<String> paymentBarcodeNumberTescoLotusList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment);

        appPreference = new AppPreference(this);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        listView = (ListView) findViewById(R.id.listView);
        adapter = new AKO555PaymentListAdapter(this);

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
        Request request = builder.url(urlPayment).post(formBody).build();
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
        if(paymentIDList != null) {
            paymentIDList.clear();
            paymentTitleList.clear();
            paymentCodeList.clear();
            paymentDescriptionList.clear();
            paymentAmountList.clear();
            paymentDueDateList.clear();
            paymentBarcodeCounterServiceList.clear();
            paymentBarcodeTescoLotusList.clear();
            paymentBarcodeNumberCounterServiceList.clear();
            paymentBarcodeNumberTescoLotusList.clear();
        } else {
            paymentIDList = new ArrayList<String>();
            paymentTitleList = new ArrayList<String>();
            paymentCodeList = new ArrayList<String>();
            paymentDescriptionList = new ArrayList<String>();
            paymentAmountList = new ArrayList<String>();
            paymentDueDateList = new ArrayList<String>();
            paymentBarcodeCounterServiceList = new ArrayList<String>();
            paymentBarcodeTescoLotusList = new ArrayList<String>();
            paymentBarcodeNumberCounterServiceList = new ArrayList<String>();
            paymentBarcodeNumberTescoLotusList = new ArrayList<String>();
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
                    paymentIDList.add(jArrayData.getJSONObject(x).getString("id"));
                    paymentTitleList.add(jArrayData.getJSONObject(x).getString("title"));
                    paymentCodeList.add(jArrayData.getJSONObject(x).getString("code"));
                    paymentDescriptionList.add(jArrayData.getJSONObject(x).getString("description"));
                    paymentAmountList.add(jArrayData.getJSONObject(x).getString("amount"));
                    paymentDueDateList.add(jArrayData.getJSONObject(x).getString("datetime-expire"));
                    paymentBarcodeCounterServiceList.add(jArrayData.getJSONObject(x).getJSONObject("payment-gateway-response").getString("barcode-image-cs"));
                    paymentBarcodeTescoLotusList.add(jArrayData.getJSONObject(x).getJSONObject("payment-gateway-response").getString("barcode-image-tc"));
                    paymentBarcodeNumberCounterServiceList.add(jArrayData.getJSONObject(x).getJSONObject("payment-gateway-response").getString("barcode-text-cs"));
                    paymentBarcodeNumberTescoLotusList.add(jArrayData.getJSONObject(x).getJSONObject("payment-gateway-response").getString("barcode-text-tc"));
                }
            } else {
                JSONObject jObjMessage = jObj.getJSONObject("message");
                messageTextInter = jObjMessage.getString("text-inter");
                messageTextLocal = jObjMessage.getString("text-local");
                messageID = jObjMessage.getInt("id");
            }

            adapter = new AKO555PaymentListAdapter(this);
            adapter.setNameList(paymentTitleList);
            adapter.setDescriptionList(paymentDescriptionList);
            listView.setAdapter(adapter);
            listView.invalidateViews();
            adapter.notifyDataSetChanged();
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                        long arg3) {
                    // TODO Auto-generated method stub
                    appPreference.setProjectID(paymentIDList.get(arg2));
                    Intent intent = new Intent();
                    intent.setClassName(getPackageName(), getPackageName() + ".AKO555PaymentInfo");
                    intent.putExtra("paymentTitle", paymentTitleList.get(arg2));
                    intent.putExtra("description", paymentDescriptionList.get(arg2));
                    intent.putExtra("paymentNumber", paymentCodeList.get(arg2));
                    intent.putExtra("amount", paymentAmountList.get(arg2));
                    intent.putExtra("dueDate", paymentDueDateList.get(arg2));
                    intent.putExtra("barcodeCounterService", paymentBarcodeCounterServiceList.get(arg2));
                    intent.putExtra("barcodeTescoLotus", paymentBarcodeTescoLotusList.get(arg2));
                    intent.putExtra("barcodeNumberCounterService", paymentBarcodeNumberCounterServiceList.get(arg2));
                    intent.putExtra("barcodeNumberTescoLotus", paymentBarcodeNumberTescoLotusList.get(arg2));
                    startActivity(intent);
                }
            });
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
