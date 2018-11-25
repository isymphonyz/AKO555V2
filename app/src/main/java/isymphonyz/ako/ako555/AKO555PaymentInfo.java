package isymphonyz.ako.ako555;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import isymphonyz.ako.ako555.customview.RSUTextView;

public class AKO555PaymentInfo extends AppCompatActivity {

    private ProgressBar progressBar;
    private TextView txtTitle;
    private ImageView btnMedia;
    private TextView txtPaymentTitle;
    private TextView txtDescriptionTitle;
    private TextView txtDescription;
    private TextView txtPaymentNumberTitle;
    private TextView txtPaymentNumber;
    private TextView txtAmountTitle;
    private TextView txtAmount;
    private TextView txtDueDateTitle;
    private TextView txtDueDate;
    private TextView txtBarcodeCounterServiceTitle;
    private TextView txtBarcodeTescoLotusTitle;
    private ImageView imgBarcodeCounterService;
    private ImageView imgBarcodeTescoLotus;
    private RSUTextView txtBarcodeCounterService;
    private RSUTextView txtBarcodeTescoLotus;
    private Typeface tf;

    Bundle extras;
    String paymentTitle = "";
    String description = "";
    String paymentNumber = "";
    String amount = "";
    String dueDate = "";
    String barcodeCounterService = "";
    String barcodeTescoLotus = "";
    String barcodeNumberCounterService = "";
    String barcodeNumberTescoLotus = "";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_info);

        extras = getIntent().getExtras();
        paymentTitle = extras.getString("paymentTitle");
        description = extras.getString("description");
        paymentNumber = extras.getString("paymentNumber");
        amount = extras.getString("amount");
        dueDate = extras.getString("dueDate");
        barcodeCounterService = extras.getString("barcodeCounterService");
        barcodeTescoLotus = extras.getString("barcodeTescoLotus");
        barcodeNumberCounterService = extras.getString("barcodeNumberCounterService");
        barcodeNumberTescoLotus = extras.getString("barcodeNumberTescoLotus");

        tf = Typeface.createFromAsset(getAssets(), "fonts/rsu-light.ttf");

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        txtTitle = (TextView) findViewById(R.id.txtTitle);
        txtPaymentTitle = (TextView) findViewById(R.id.txtPaymentTitle);
        txtDescriptionTitle = (TextView) findViewById(R.id.txtDescriptionTitle);
        txtDescription = (TextView) findViewById(R.id.txtDescription);
        txtPaymentNumberTitle = (TextView) findViewById(R.id.txtPaymentNumberTitle);
        txtPaymentNumber = (TextView) findViewById(R.id.txtPaymentNumber);
        txtAmountTitle = (TextView) findViewById(R.id.txtAmountTitle);
        txtAmount = (TextView) findViewById(R.id.txtAmount);
        txtDueDateTitle = (TextView) findViewById(R.id.txtDueDateTitle);
        txtDueDate = (TextView) findViewById(R.id.txtDueDate);
        txtBarcodeCounterServiceTitle = (TextView) findViewById(R.id.txtBarcodeCounterServiceTitle);
        txtBarcodeTescoLotusTitle = (TextView) findViewById(R.id.txtBarcodeTescoLotusTitle);

        txtTitle.setTypeface(tf);
        txtPaymentTitle.setTypeface(tf);
        txtDescriptionTitle.setTypeface(tf);
        txtDescription.setTypeface(tf);
        txtPaymentNumberTitle.setTypeface(tf);
        txtPaymentNumber.setTypeface(tf);
        txtAmountTitle.setTypeface(tf);
        txtAmount.setTypeface(tf);
        txtDescriptionTitle.setTypeface(tf);
        txtDescription.setTypeface(tf);
        txtDueDateTitle.setTypeface(tf);
        txtDueDate.setTypeface(tf);
        txtBarcodeCounterServiceTitle.setTypeface(tf);
        txtBarcodeTescoLotusTitle.setTypeface(tf);

        txtPaymentTitle.setText(paymentTitle);
        txtDescription.setText(description);
        txtPaymentNumber.setText(paymentNumber);
        txtAmount.setText(amount);
        txtDueDate.setText(dueDate);

        txtBarcodeCounterService = (RSUTextView) findViewById(R.id.txtBarcodeCounterService);
        txtBarcodeCounterService.setText(barcodeNumberCounterService);
        imgBarcodeCounterService = (ImageView) findViewById(R.id.imgBarcodeCounterService);
        Glide.with(this)
                .load(barcodeCounterService)
                .crossFade()
                .into(imgBarcodeCounterService);

        txtBarcodeTescoLotus = (RSUTextView) findViewById(R.id.txtBarcodeTescoLotus);
        txtBarcodeTescoLotus.setText(barcodeNumberTescoLotus);
        imgBarcodeTescoLotus = (ImageView) findViewById(R.id.imgBarcodeTescoLotus);
        Glide.with(this)
                .load(barcodeTescoLotus)
                .crossFade()
                .into(imgBarcodeTescoLotus);
    }
}
