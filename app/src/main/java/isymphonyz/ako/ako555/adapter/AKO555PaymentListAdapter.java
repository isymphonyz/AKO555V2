package isymphonyz.ako.ako555.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import isymphonyz.ako.ako555.R;
import isymphonyz.ako.ako555.customview.RSUTextView;

/**
 * Created by Dooplus on 12/5/15 AD.
 */
public class AKO555PaymentListAdapter extends BaseAdapter {
    private Activity activity;
    private static LayoutInflater inflater=null;
    //ImageLoader imageLoader;
    Typeface tf;

    private ArrayList<Integer> imageList = null;
    private ArrayList<String> nameList = null;
    private ArrayList<String> descriptionList = null;

    //public LazyAdapter(Activity a, String[] d) {
    public AKO555PaymentListAdapter(Activity a) {
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

    public void setDescriptionList(ArrayList<String> descriptionList) {
        this.descriptionList = descriptionList;
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
        public ImageView imageView;
        public RSUTextView txtName;
        public RSUTextView txtDescription;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        ViewHolder holder;
        if(convertView==null){
            vi = inflater.inflate(R.layout.payment_list_item, null);
            holder=new ViewHolder();
            //holder.layout = (RelativeLayout) vi.findViewById(R.id.layout);
            holder.imageView = (ImageView) vi.findViewById(R.id.imageView);
            holder.txtName = (RSUTextView) vi.findViewById(R.id.txtName);
            holder.txtDescription = (RSUTextView) vi.findViewById(R.id.txtDescription);
            vi.setTag(holder);
        }
        else
            holder=(ViewHolder)vi.getTag();

        holder.txtName.setText(nameList.get(position));
        holder.txtDescription.setText(descriptionList.get(position));

        holder.imageView.setVisibility(View.GONE);

        return vi;
    }
}
