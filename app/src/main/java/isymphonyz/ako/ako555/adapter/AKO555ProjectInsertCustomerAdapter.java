package isymphonyz.ako.ako555.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import isymphonyz.ako.ako555.R;

/**
 * Created by Dooplus on 12/5/15 AD.
 */
public class AKO555ProjectInsertCustomerAdapter extends BaseAdapter {
    private Activity activity;
    private static LayoutInflater inflater=null;
    //ImageLoader imageLoader;
    Typeface tf;

    private ArrayList<String> nameList = null;
    private ArrayList<Boolean> statusList = null;

    //public LazyAdapter(Activity a, String[] d) {
    public AKO555ProjectInsertCustomerAdapter(Activity a) {
        activity = a;
        //imageLoader = new ImageLoader(activity);
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        tf = Typeface.createFromAsset(activity.getAssets(), "fonts/rsu-light.ttf");
    }

    public void setNameList(ArrayList<String> nameList) {
        this.nameList = nameList;
    }

    public void setStatusList(ArrayList<Boolean> statusList) {
        this.statusList = statusList;
    }

    public int getCount() {
        //return data.length;
        return nameList.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder{
        public RelativeLayout layout;
        public CheckBox inputName;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        ViewHolder holder;
        /*if(convertView==null){
            vi = inflater.inflate(R.layout.project_insert_customer_list_item, null);
            holder=new ViewHolder();
            holder.inputName = (CheckBox) vi.findViewById(R.id.inputName);
            vi.setTag(holder);
        }
        else
            holder=(ViewHolder)vi.getTag();*/

        vi = inflater.inflate(R.layout.project_insert_customer_list_item, null);
        holder=new ViewHolder();
        holder.inputName = (CheckBox) vi.findViewById(R.id.inputName);
        vi.setTag(holder);

        holder.inputName.setText(nameList.get(position));
        holder.inputName.setTypeface(tf);
        holder.inputName.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                statusList.set(position, isChecked);
            }
        });

        return vi;
    }
}
