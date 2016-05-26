package android.geekband006.Main;

import android.content.Context;
import android.content.Intent;
import android.geekband006.R;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cm on 16/4/19.
 */
public class ItemAdapter extends BaseAdapter {
    private List<ItemInfo> itemInfoList = new ArrayList<ItemInfo>();
    private LayoutInflater mInflater;
    private Context context;

    ItemAdapter(Context context, List<ItemInfo> itemInfoList) {
        this.context = context;
        this.itemInfoList = itemInfoList;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return itemInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return itemInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (getCount() == 0) {
            return null;
        }

        ViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.expand_item, null);

            holder = new ViewHolder();
            holder.content = (TextView) convertView.findViewById(R.id.content_textview);
            holder.time = (TextView) convertView.findViewById(R.id.time_textview);
            holder.location = (TextView) convertView.findViewById(R.id.location_textview);
            holder.edit = (ImageView) convertView.findViewById(R.id.edit_image);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final ItemInfo itemInfo = itemInfoList.get(position);
        holder.content.setText(itemInfo.content);
        holder.time.setText(itemInfo.time);
        holder.location.setText(itemInfo.location);
        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EditActivity.class);
                intent.putExtra("id", itemInfo.id);
                intent.putExtra("content", itemInfo.content);
                intent.putExtra("date", itemInfo.date);
                intent.putExtra("time", itemInfo.time);
                intent.putExtra("before", itemInfo.before);
                intent.putExtra("latitude", itemInfo.latitude);
                intent.putExtra("longitude", itemInfo.longitude);
                Log.d("post", "" + itemInfo.before);
                context.startActivity(intent);
            }
        });

        return convertView;


    }

    public class ViewHolder {
        private TextView content;
        private TextView time;
        private TextView location;
        private ImageView edit;
    }
}