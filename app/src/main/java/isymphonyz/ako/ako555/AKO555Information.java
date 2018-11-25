package isymphonyz.ako.ako555;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import isymphonyz.ako.ako555.customview.RSUTextView;

public class AKO555Information extends AppCompatActivity {

    private RSUTextView txtDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.information);

        txtDetail = (RSUTextView) findViewById(R.id.txtDetail);
        //txtDetail.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
