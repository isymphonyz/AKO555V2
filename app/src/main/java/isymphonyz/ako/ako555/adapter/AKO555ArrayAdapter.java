package isymphonyz.ako.ako555.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import isymphonyz.ako.ako555.R;
import isymphonyz.ako.ako555.customview.RSUTextView;

/**
 * Created by Dooplus on 12/5/15 AD.
 */
public class AKO555ArrayAdapter extends BaseAdapter {
    private Activity activity;
    private static LayoutInflater inflater=null;
    //ImageLoader imageLoader;
    Typeface tf;

    private ArrayList<Integer> imageList = null;
    private ArrayList<String> nameList = null;

    //public LazyAdapter(Activity a, String[] d) {
    public AKO555ArrayAdapter(Activity a) {
        activity = a;
        //imageLoader = new ImageLoader(activity);
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //tf = Typeface.createFromAsset(activity.getAssets(), "fonts/rsu-light.ttf");
    }

    public void setImageList(ArrayList<Integer> imageList) {
        this.imageList = imageList;
    }

    public void setNameList(ArrayList<String> nameList) {
        this.nameList = nameList;
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
        public CheckBox checkBox;
        public RSUTextView txtName;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        ViewHolder holder;
        if(convertView==null){
            vi = inflater.inflate(R.layout.array_list_item, null);
            holder=new ViewHolder();
            holder.txtName = (RSUTextView) vi.findViewById(R.id.txtName);
            vi.setTag(holder);
        }
        else
            holder=(ViewHolder)vi.getTag();

        holder.txtName.setText(nameList.get(position));

        return vi;
    }
}
