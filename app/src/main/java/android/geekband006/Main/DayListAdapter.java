package android.geekband006.Main;

import android.content.Context;
import android.content.Intent;
import android.geekband006.R;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by cm on 16/4/19.
 */
public class DayListAdapter extends BaseAdapter {
    private ArrayList<DayInfo> dayInfoList = new ArrayList<DayInfo>();
    private ArrayList<ArrayList<ItemInfo>> itemListList = new ArrayList<ArrayList<ItemInfo>>();
    private LayoutInflater mInflater;
    private int mLcdWidth = 0;
    private float mDensity = 0;
    Context context;

    public DayListAdapter(Context context, ArrayList<DayInfo> dayInfoList, ArrayList<ArrayList<ItemInfo>> itemListList) {
        this.context = context;
        this.dayInfoList = dayInfoList;
        this.itemListList = itemListList;
        mInflater = LayoutInflater.from(context);
        getSystemInfo();
    }

    @Override
    public int getCount() {
        return dayInfoList.size();
    }

    @Override
    public Object getItem(int arg0) {
        return dayInfoList.get(arg0);
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
            convertView = mInflater.inflate(R.layout.expand_day, null);

            holder = new ViewHolder();
            holder.expand_icon = (ImageView) convertView.findViewById(R.id.expand_icon);
            holder.day_time = (TextView) convertView.findViewById(R.id.day_textview);
            holder.item_number = (TextView) convertView.findViewById(R.id.item_number_textview);
            holder.item_list = (ListView)convertView.findViewById(R.id.item_listview);
//            holder.item_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    Intent intent = new Intent(context,EditActivity.class);
//                    context.startActivity(intent);
//                }
//            });
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        DayInfo ai = dayInfoList.get(position);
        if (dayInfoList.get(position).state == 0) {
            ai.expand_icon = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_expand);
        } else {
            ai.expand_icon = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_expanded);
        }


        holder.day_time.setText(ai.day_time);
        holder.item_number.setText(ai.item_number);
        holder.expand_icon.setImageBitmap(ai.expand_icon);
        holder.item_list.setAdapter(new ItemAdapter(context,itemListList.get(position)));

//            Button btnOpen = (Button) convertView.findViewById(R.id.btnOpen);
//            lp = (RelativeLayout.LayoutParams) btnOpen.getLayoutParams();
//            lp.width = btnWidth;
//            btnOpen.setLayoutParams(lp);
//            btnOpen.setOnClickListener(new OnClickListener() {
//                @Override
//                public void onClick(View arg0) {
//                    Toast.makeText(getApplicationContext(), "打开应用!", Toast.LENGTH_SHORT).show();
//                }
//            });
//
//            Button btnView = (Button) convertView.findViewById(R.id.btnView);
//            lp = (RelativeLayout.LayoutParams) btnView.getLayoutParams();
//            lp.width = btnWidth;
//            lp.leftMargin = 10;
//            btnView.setLayoutParams(lp);
//            btnView.setOnClickListener(new OnClickListener() {
//                @Override
//                public void onClick(View arg0) {
//                    Toast.makeText(getApplicationContext(), "查看详情!", Toast.LENGTH_SHORT).show();
//                }
//            });
//
//            Button btnWarning = (Button) convertView.findViewById(R.id.btnWarning);
//            lp = (RelativeLayout.LayoutParams) btnWarning.getLayoutParams();
//            lp.width = btnWidth;
//            lp.leftMargin = 10;
//            btnWarning.setLayoutParams(lp);
//            btnWarning.setOnClickListener(new OnClickListener() {
//                @Override
//                public void onClick(View arg0) {
//                    Toast.makeText(getApplicationContext(), "举报应用!", Toast.LENGTH_SHORT).show();
//                }
//            });

        // get footer height
        RelativeLayout footer = (RelativeLayout) convertView.findViewById(R.id.footer);
        int widthSpec = View.MeasureSpec.makeMeasureSpec((int) (mLcdWidth - 10 * mDensity), View.MeasureSpec.EXACTLY);
        footer.measure(widthSpec, 0);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) footer.getLayoutParams();
        params.height = (itemListList.get(position).size() * (int) (75 * mDensity));

        if (dayInfoList.get(position).state == 0) {
            holder.expand_icon.setImageBitmap(ai.expand_icon);
            params.bottomMargin = -params.height;
            footer.setVisibility(View.GONE);
        } else {
            holder.expand_icon.setImageBitmap(ai.expand_icon);
            params.bottomMargin = 0;
            footer.setVisibility(View.VISIBLE);
        }

        return convertView;
    }

    public class ViewHolder {
        private ImageView expand_icon;
        private TextView day_time;
        private TextView item_number;
        private ListView item_list;
    }

    private void getSystemInfo() {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        mLcdWidth = dm.widthPixels;
        mDensity = dm.density;
    }

}