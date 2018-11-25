package isymphonyz.ako.ako555;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import isymphonyz.ako.ako555.customview.RSUTextView;
import isymphonyz.ako.ako555.utils.AppPreference;

public class AKO555CustomerInfo extends AppCompatActivity {

    private ProgressBar progressBar;
    private RSUTextView inputFirstname;
    private RSUTextView inputLastname;
    private RSUTextView inputCitizenID;
    private RSUTextView inputAddress;
    private RSUTextView inputTumbon;
    private RSUTextView inputAmphur;
    private RSUTextView inputProvince;
    private RSUTextView inputPostcode;
    private RSUTextView inputTelephone;
    private RSUTextView inputMobile;
    private RSUTextView inputEmail;
    private RSUTextView inputUsername;
    private RSUTextView inputPassword;
    private RSUTextView inputDescription;
    private Button btnEdit;
    private Button btnDelete;
    private Typeface tf;

    private AppPreference appPreference;
    private Bundle extras;
    String customerID;
    String customerFirstname;
    String customerLastname;
    String customerTaxNo;
    String customerAddress;
    String customerSubDistrict;
    String customerDistrict;
    String customerProvince;
    String customerPostcode;
    String customerTelephone;
    String customerMobile;
    String customerEmail;
    String customerUsername;
    String customerPassword;
    String customerDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_info);

        appPreference = new AppPreference(this);
        
        extras = getIntent().getExtras();
        customerID = extras.getString("customerID");
        customerFirstname = extras.getString("customerFirstname");
        customerLastname = extras.getString("customerLastname");
        customerTaxNo = extras.getString("customerTaxNo");
        customerAddress = extras.getString("customerAddress");
        customerSubDistrict = extras.getString("customerSubDistrict");
        customerDistrict = extras.getString("customerDistrict");
        customerProvince = extras.getString("customerProvince");
        customerPostcode = extras.getString("customerPostcode");
        customerTelephone = extras.getString("customerTelephone");
        customerMobile = extras.getString("customerMobile");
        customerEmail = extras.getString("customerEmail");
        customerUsername = extras.getString("customerUsername");
        customerPassword = extras.getString("customerPassword");
        customerDescription = extras.getString("customerDescription");

        tf = Typeface.createFromAsset(getAssets(), "fonts/rsu-light.ttf");

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        inputFirstname = (RSUTextView) findViewById(R.id.inputFirstname);
        inputLastname = (RSUTextView) findViewById(R.id.inputLastname);
        inputCitizenID = (RSUTextView) findViewById(R.id.inputCitizenID);
        inputAddress = (RSUTextView) findViewById(R.id.inputAddress);
        inputTumbon = (RSUTextView) findViewById(R.id.inputTumbon);
        inputAmphur = (RSUTextView) findViewById(R.id.inputAmphur);
        inputProvince = (RSUTextView) findViewById(R.id.inputProvince);
        inputPostcode = (RSUTextView) findViewById(R.id.inputPostcode);
        inputTelephone = (RSUTextView) findViewById(R.id.inputTelephone);
        inputMobile = (RSUTextView) findViewById(R.id.inputMobile);
        inputEmail = (RSUTextView) findViewById(R.id.inputEmail);
        inputUsername = (RSUTextView) findViewById(R.id.inputUsername);
        inputPassword = (RSUTextView) findViewById(R.id.inputPassword);
        inputDescription = (RSUTextView) findViewById(R.id.inputDescription);

        btnEdit = (Button) findViewById(R.id.btnEdit);
        btnEdit.setTypeface(tf);
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnDelete = (Button) findViewById(R.id.btnDelete);
        btnDelete.setTypeface(tf);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        
        setCustomerData();
    }
    
    private void setCustomerData() {
        inputFirstname.setText(": " + customerFirstname);
        inputLastname.setText(": " + customerLastname);
        inputCitizenID.setText(": " + customerTaxNo);
        inputAddress.setText(": " + customerAddress);
        inputTumbon.setText(": " + customerSubDistrict);
        inputAmphur.setText(": " + customerDistrict);
        inputProvince.setText(": " + customerProvince);
        inputPostcode.setText(": " + customerPostcode);
        inputTelephone.setText(": " + customerTelephone);
        inputMobile.setText(": " + customerMobile);
        inputEmail.setText(": " + customerEmail);
        inputUsername.setText(": " + customerUsername);
        inputPassword.setText(": " + customerPassword);
        inputDescription.setText(": " + customerDescription);
    }
}
