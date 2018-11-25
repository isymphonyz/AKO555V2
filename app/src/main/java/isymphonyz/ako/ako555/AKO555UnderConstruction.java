package isymphonyz.ako.ako555;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class AKO555UnderConstruction extends AppCompatActivity {

    ImageView imgUnderConstruction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.under_construction);

        imgUnderConstruction = (ImageView) findViewById(R.id.imgUnderConstruction);

        Glide.with(this)
                .load(R.mipmap.ic_under_construction_01)
                .placeholder(R.mipmap.ic_launcher)
                .crossFade()
                .into(imgUnderConstruction);
    }
}
