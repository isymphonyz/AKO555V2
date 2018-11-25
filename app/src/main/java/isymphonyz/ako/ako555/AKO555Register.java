package isymphonyz.ako.ako555;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
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
import java.util.Arrays;

import isymphonyz.ako.ako555.utils.AppPreference;
import isymphonyz.ako.ako555.utils.MyConfiguration;
import isymphonyz.ako.ako555.utils.ToastMessage;
import isymphonyz.ako.ako555.utils.UserEmailFetcher;

public class AKO555Register extends AppCompatActivity {

    private ProgressBar progressBar;
    private EditText inputUsername;
    private EditText inputPassword;
    private EditText inputMobileDeviceNo;
    private EditText inputVehiclePlateNo;
    private EditText inputEmail;
    private EditText inputFirstname;
    private EditText inputLastname;
    private Button btnSubmit;

    private String username = "";
    private String password = "";
    private String mobileDeviceNo = "";
    private String vehicleDeviceNo = "";
    private String email = "";
    private String firstname = "";
    private String lastname = "";
    private String mobileDeviceID = "";

    private String urlRegister = MyConfiguration.DOMAIN + MyConfiguration.VERSION + MyConfiguration.REGISTER_URL;

    private AppPreference appPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        appPreference = new AppPreference(this);

        TelephonyManager telemngr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        mobileDeviceID = telemngr.getDeviceId();

        email = UserEmailFetcher.getEmail(AKO555Register.this);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        inputUsername = (EditText) findViewById(R.id.inputUsername);
        inputPassword = (EditText) findViewById(R.id.inputPassword);
        inputMobileDeviceNo = (EditText) findViewById(R.id.inputMobileDeviceNo);
        inputVehiclePlateNo = (EditText) findViewById(R.id.inputVehiclePlateNo);
        inputEmail = (EditText) findViewById(R.id.inputEmail);
        inputFirstname = (EditText) findViewById(R.id.inputFirstname);
        inputLastname = (EditText) findViewById(R.id.inputLastname);

        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/rsu-light.ttf");
        inputUsername.setTypeface(tf);
        inputPassword.setTypeface(tf);
        inputFirstname.setTypeface(tf);
        inputLastname.setTypeface(tf);
        inputEmail.setTypeface(tf);
        inputMobileDeviceNo.setTypeface(tf);
        inputVehiclePlateNo.setTypeface(tf);

        inputEmail.setText(email);

        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btnSubmit.setTypeface(tf);
        btnSubmit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                username = inputUsername.getText().toString();
                password = inputPassword.getText().toString();
                mobileDeviceNo = inputMobileDeviceNo.getText().toString();
                vehicleDeviceNo = inputVehiclePlateNo.getText().toString();
                email = inputEmail.getText().toString();
                firstname = inputFirstname.getText().toString();
                lastname = inputLastname.getText().toString();
                //ToastMessage.show(getApplicationContext(), "Email: " + email);

                progressBar.setVisibility(View.VISIBLE);
                OkHttpClient okHttpClient = new OkHttpClient();
                okHttpClient.setProtocols(Arrays.asList(Protocol.HTTP_1_1));

                Request.Builder builder = new Request.Builder();
                RequestBody formBody = new FormEncodingBuilder()
                        .add("-system-user-id", "")
                        .add("-app-version", appPreference.getAppVersion())
                        .add("username", username)
                        .add("password", password)
                        .add("mobile-device-uid", mobileDeviceID)
                        .add("mobile-device-pno", mobileDeviceNo)
                        .add("vehicle-plate-no", vehicleDeviceNo)
                        .add("email", email)
                        .add("firstname", firstname)
                        .add("lastname", lastname)
                        .build();
                Request request = builder.url(urlRegister).post(formBody).build();
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
                                setRegister(result);
                            }
                        });
                    }
                });
            }
        });
    }

    public void setRegister(String result) {
        // TODO Auto-generated method stub
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
}
