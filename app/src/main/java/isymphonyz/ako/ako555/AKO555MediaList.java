package isymphonyz.ako.ako555;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import isymphonyz.ako.ako555.adapter.AKO555ArrayAdapter;

public class AKO555MediaList extends AppCompatActivity {

    private TextView txtTitle;
    private ListView listView;
    private AKO555ArrayAdapter adapter;
    private Typeface tf;

    Bundle extras;
    private String folderName = "";
    private ArrayList<String> mediaNameList;
    private ArrayList<String> mediaImageList;
    private ArrayList<String> mediaUrlList;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.media_list);

        extras = getIntent().getExtras();
        folderName = extras.getString("folderName");
        mediaNameList = extras.getStringArrayList("mediaName");
        mediaImageList = extras.getStringArrayList("mediaImage");
        mediaUrlList = extras.getStringArrayList("mediaUrl");

        tf = Typeface.createFromAsset(getAssets(), "fonts/rsu-light.ttf");

        txtTitle = (TextView) findViewById(R.id.txtTitle);
        txtTitle.setTypeface(tf);

        adapter = new AKO555ArrayAdapter(this);
        adapter.setNameList(mediaNameList);
        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                // TODO Auto-generated method stub
                Intent intent = new Intent();
                intent.setClassName(getPackageName(), getPackageName() + ".AKO555MediaPlayer");
                //intent.putExtra("media", videoOfflineNameArray[arg2]);
                //intent.putExtra("videoOfflineNameArray", videoOfflineNameArray);
                //intent.putExtra("index", arg2);
                intent.putExtra("prefixPath", folderName);
                intent.putExtra("spotName", mediaNameList.get(arg2));
                startActivity(intent);
            }
        });
    }
}
