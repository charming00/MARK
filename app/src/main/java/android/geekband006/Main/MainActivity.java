package android.geekband006.Main;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.geekband006.MyDatabaseHelper;
import android.geekband006.R;
import android.geekband006.SlideMenu;
import android.geekband006.map.AllItemMapActivity;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "ChrisExpand";
    private ListView dayListView;
    private DayListAdapter dayAdapter;
    private ArrayList<DayInfo> dayInfoList = new ArrayList<DayInfo>();


    private ArrayList<ArrayList<ItemInfo>> itemListList = new ArrayList<ArrayList<ItemInfo>>();

    private ImageView addContent;
    private ImageView setting;
    private TextView mapText;
    SlideMenu slideMenu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_main);


        slideMenu = (SlideMenu) findViewById(R.id.main_slide_menu);

        addContent = (ImageView) findViewById(R.id.content_add);
        setting = (ImageView) findViewById(R.id.item_setting);
        mapText = (TextView) findViewById(R.id.item_map);
        mapText.setOnClickListener(this);
        addContent.setOnClickListener(this);
        setting.setOnClickListener(this);

        LinearLayout settingSetting = (LinearLayout) findViewById(R.id.setting_setting_layout);
        settingSetting.setOnClickListener(this);

        dayAdapter = new DayListAdapter(this, dayInfoList, itemListList);
        dayListView = (ListView) findViewById(R.id.day_listview);
        dayListView.setAdapter(dayAdapter);
        dayListView.setDividerHeight(20);

        dayListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View v, int pos, long arg3) {
                Log.d(TAG, "onItemClick");
                if(dayInfoList.get(pos).state == 0) {
                    dayInfoList.get(pos).state = 1;
                } else {
                    dayInfoList.get(pos).state = 0;
                }
                View footer = v.findViewById(R.id.footer);
                footer.startAnimation(new ViewExpandAnimation(footer));
                dayAdapter.notifyDataSetChanged();
            }
        });
        selectFromDB();
//        addDayInfo();
//        addItemInfo();

    }

    @Override
    protected void onResume() {
        super.onResume();
        dayInfoList.clear();
        itemListList.clear();
        selectFromDB();

    }

    void selectFromDB() {
        MyDatabaseHelper myDatabaseHelper = new MyDatabaseHelper(this);
        SQLiteDatabase database = myDatabaseHelper.getReadableDatabase();
        String sql = "select * from " + MyDatabaseHelper.ITEM_INFO_TABLE_NAME + " where " + MyDatabaseHelper.ALARM_STATE + " = 0 " + " order by " + MyDatabaseHelper.ALARM_DATE + " , " + MyDatabaseHelper.ALARM_TIME + ";";
        Log.d("sql", sql);
        Cursor cursor = database.rawQuery(sql,null);
        ArrayList<ItemInfo> itemInfos = new ArrayList<ItemInfo>();
        String nowDate = "";
        int num = 0;

        if (cursor.moveToFirst()) {
//            Log.d("selectFromDB","moveToFirst");
            ItemInfo itemInfo = new ItemInfo();
            itemInfo.id = cursor.getLong(0);
            itemInfo.content = cursor.getString(1);
            itemInfo.date = cursor.getString(2).substring(0,4) + "." + cursor.getString(2).substring(4,6) + "." + cursor.getString(2).substring(6,8) ;
            itemInfo.time = cursor.getString(3).substring(0,2) + ":" + cursor.getString(3).substring(2,4);
            itemInfo.before = cursor.getInt(4);
//            Log.d("read before", "" + cursor.getInt(4));
            itemInfo.latitude = cursor.getDouble(5);
            itemInfo.longitude = cursor.getDouble(6);
            itemInfo.location = cursor.getString(8);
            num++;
            nowDate = itemInfo.date;
            itemInfos.add(itemInfo);
            Log.d("date",itemInfo.date);
        }

        while(cursor.moveToNext()) {
//            Log.d("selectFromDB","moveToNext");
            ItemInfo itemInfo = new ItemInfo();
            itemInfo.id = cursor.getLong(0);
            itemInfo.content = cursor.getString(1);
            itemInfo.date = cursor.getString(2).substring(0,4) + "." + cursor.getString(2).substring(4,6) + "." + cursor.getString(2).substring(6,8) ;
            itemInfo.time = cursor.getString(3).substring(0,2) + ":" + cursor.getString(3).substring(2,4);
            itemInfo.before = cursor.getInt(4);
            itemInfo.latitude = cursor.getDouble(5);
            itemInfo.longitude = cursor.getDouble(6);
            itemInfo.location = cursor.getString(8);
//            Log.d("date",itemInfo.date);
            if (!itemInfo.date.equals(nowDate)) {
                DayInfo dayInfo = new DayInfo();
                dayInfo.expand_icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_expand);
                dayInfo.day_time = nowDate;
                dayInfo.item_number = String.valueOf(num);
                dayInfoList.add(dayInfo);

                itemListList.add(itemInfos);
                itemInfos = new ArrayList<ItemInfo>();

                num = 0;
                nowDate = itemInfo.date;
            }
            num++;
            itemInfos.add(itemInfo);
        }
        DayInfo dayInfo = new DayInfo();
        dayInfo.expand_icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_expand);
        dayInfo.day_time = nowDate;
        dayInfo.item_number = String.valueOf(num);
        if (num > 0 ) {
            dayInfoList.add(dayInfo);
        }


        itemListList.add(itemInfos);

        dayAdapter.notifyDataSetChanged();

    }
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        setActionBarLayout(R.layout.activity_main_actionbar, this);
//        addContent = (ImageView) findViewById(R.id.content_add);
//        setting = (ImageView) findViewById(R.id.item_setting);
//        mapText = (TextView) findViewById(R.id.item_map);
//        mapText.setOnClickListener(this);
//        addContent.setOnClickListener(this);
//        setting.setOnClickListener(this);
//        return true;
//    }

    @SuppressLint("NewApi")
    public void setActionBarLayout(int layoutId, Context mContext) {
        ActionBar actionBar = getActionBar();
        if (null != actionBar) {
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

            LayoutInflater inflator = (LayoutInflater) this
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = inflator.inflate(layoutId, new LinearLayout(mContext),
                    false);
            ActionBar.LayoutParams layout = new ActionBar.LayoutParams(
                    ViewPager.LayoutParams.MATCH_PARENT, ViewPager.LayoutParams.MATCH_PARENT);
            actionBar.setCustomView(v, layout);
        }
    }


    private void addDayInfo() {
        for (int i = 0; i < 30; i++) {
            DayInfo dayInfo = new DayInfo();
            dayInfo.expand_icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_expand);

            int j = i + 1;
            dayInfo.day_time = "日期：" + j;
            dayInfo.item_number = "" + j;
            dayInfoList.add(dayInfo);
        }
        dayAdapter.notifyDataSetChanged();
    }

    private void addItemInfo() {
        for (int i = 0; i < 30; i++) {
            ArrayList<ItemInfo> itemInfoList = new ArrayList<ItemInfo>();
            for (int j = 0; j < i+1; j++) {
                ItemInfo itemInfo = new ItemInfo();
                int k = i + 1;
                itemInfo.content = "事项：" + k;
                itemInfo.time = "12:30";
                itemInfo.location = "紫金港";
                itemInfoList.add(itemInfo);
            }
            itemListList.add(itemInfoList);
        }
//        itemAdapter.notifyDataSetChanged();
        dayAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.content_add:
                Intent intent = new Intent(this,EditActivity.class);
                startActivity(intent);
                break;
            case R.id.item_map:
                Intent intent1 = new Intent(this, AllItemMapActivity.class);
                startActivity(intent1);
                finish();
                break;
            case R.id.item_setting:
                if (slideMenu.isMainScreenShowing()) {
                    slideMenu.openMenu();
                } else {
                    slideMenu.closeMenu();
                }
                break;
            case R.id.setting_setting_layout:
                Toast.makeText(this,"clicked",Toast.LENGTH_SHORT).show();
        }
    }
}
