package android.geekband006.map;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;

import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.geekband006.Main.DayInfo;
import android.geekband006.Main.EditActivity;
import android.geekband006.Main.ItemInfo;
import android.geekband006.Main.MainActivity;
import android.geekband006.MyDatabaseHelper;
import android.geekband006.R;
import android.geekband006.SlideMenu;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@SuppressLint("NewApi")
public class AllItemMapActivity extends Activity implements View.OnClickListener{

    protected static MapView bmapView = null;
    protected static BaiduMap mBaiduMap = null;


    protected static BitmapDescriptor realtimeBitmap;
    protected static BitmapDescriptor dbBitmap;


    protected static Context mContext = null;

    protected static MapStatusUpdate msUpdate = null;

    private Boolean updateFocus = true;
    private Boolean selected = false;
    private Boolean located = false;

    protected static OverlayOptions overlayNow;
    protected static List<OverlayOptions> overlayOptionsList = new LinkedList<>();
    protected static OverlayOptions overlaySelected;

    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new MyLocationListener();

    private TextView item;
    private TextView map;
    private ImageView addContent;
    private ImageView setting;
    SlideMenu slideMenu;

    private double latitude;
    private double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_all_item);

        mContext = getApplicationContext();

        // 初始化组件
        initComponent();

        mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
        mLocationClient.registerLocationListener(myListener);    //注册监听函数

        initLocation();

        mLocationClient.start();

        initAllItem();

        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (marker.getExtraInfo() != null) {
                    Bundle infoBundle = marker.getExtraInfo();
//                    Toast.makeText(AllItemMapActivity.this, infoBundle.getString("date", "") + infoBundle.getString("time", "") + infoBundle.getString("content"), Toast.LENGTH_SHORT).show();
                }
//                else

//                    Toast.makeText(AllItemMapActivity.this, "null", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        slideMenu = (SlideMenu) findViewById(R.id.map_slide_menu);

        addContent = (ImageView) findViewById(R.id.all_content_add);
        addContent.setOnClickListener(this);
        setting = (ImageView) findViewById(R.id.all_setting);
        setting.setOnClickListener(this);
        item = (TextView) findViewById(R.id.map_item);
        item.setOnClickListener(this);

//        mBaiduMap.setOnMapLongClickListener(listener);

//        if (getIntent()!= null && getIntent().hasExtra("latitude")) {
//            Intent infoIntent = getIntent();
//            realtimeBitmap = BitmapDescriptorFactory
//                    .fromResource(R.drawable.icon_gcoding);
//            overlaySelected = new MarkerOptions().position(new LatLng(infoIntent.getDoubleExtra("latitude",0),infoIntent.getDoubleExtra("longitude",0)))
//                    .icon(realtimeBitmap).zIndex(9).draggable(true);
//            mBaiduMap.addOverlay(overlaySelected);
//        }


    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        setActionBarLayout(R.layout.activity_all_item_map_actionbar, this);
//        addContent = (ImageView) findViewById(R.id.content_add);
//        addContent.setOnClickListener(this);
//        item = (TextView) findViewById(R.id.map_item);
//        item.setOnClickListener(this);
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

    void initAllItem() {
        MyDatabaseHelper myDatabaseHelper = new MyDatabaseHelper(this);
        SQLiteDatabase database = myDatabaseHelper.getReadableDatabase();
        String sql = "select * from " + MyDatabaseHelper.ITEM_INFO_TABLE_NAME + " where " + MyDatabaseHelper.ALARM_STATE + " = 0 " + " order by " + MyDatabaseHelper.ALARM_DATE + " , " + MyDatabaseHelper.ALARM_TIME + ";";
        Log.d("sql", sql);
        Cursor cursor = database.rawQuery(sql, null);

        while (cursor.moveToNext()) {
//            Toast.makeText(AllItemMapActivity.this, "add", Toast.LENGTH_SHORT).show();
//            Toast.makeText(this, "" + cursor.getDouble(5) + cursor.getDouble(6),Toast.LENGTH_SHORT).show();
            dbBitmap = BitmapDescriptorFactory
                    .fromResource(R.drawable.icon_gcoding);

            overlaySelected = new MarkerOptions().position(new LatLng(cursor.getDouble(5), cursor.getDouble(6)))
                    .icon(dbBitmap).zIndex(9).draggable(true);
            overlayOptionsList.add(overlaySelected);
            Marker itemMarker = (Marker) mBaiduMap.addOverlay(overlaySelected);
            Bundle bundle = new Bundle();
            bundle.putString("content", cursor.getString(1));
            bundle.putString("date", cursor.getString(2).substring(0, 4) + "." + cursor.getString(2).substring(4, 6) + "." + cursor.getString(2).substring(6, 8) + "\n");
            bundle.putString("time", cursor.getString(3).substring(0, 2) + ":" + cursor.getString(3).substring(2, 4) + "\n");
            itemMarker.setExtraInfo(bundle);
            selected = true;
//
        }
    }

    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span = 1000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        mLocationClient.setLocOption(option);
    }

    /**
     * 初始化组件
     */
    private void initComponent() {

        bmapView = (MapView) findViewById(R.id.all_bmapView);
        mBaiduMap = bmapView.getMap();
        bmapView.showZoomControls(false);
    }


    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
//        client.onDestroy();
//        android.os.Process.killProcess(android.os.Process.myPid());
    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    void startLocation(BDLocation location) {
//        Toast.makeText(this, "clear", Toast.LENGTH_SHORT).show();
//        mBaiduMap.clear();
        LatLng point = new LatLng(location.getLatitude(), location.getLongitude());
        MapStatus mMapStatus = new MapStatus.Builder().target(point).zoom(18).build();

        msUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);

        realtimeBitmap = BitmapDescriptorFactory
                .fromResource(R.drawable.ic_my_location);

        overlayNow = new MarkerOptions().position(point)
                .icon(realtimeBitmap).zIndex(9).draggable(true);

        if (null != msUpdate && updateFocus) {
            mBaiduMap.setMapStatus(msUpdate);
            updateFocus = false;
        }

        // 实时点覆盖物
        if (null != overlayNow) {
            Bundle nowBundle = new Bundle();
            nowBundle.putString("content", "当前位置");
            mBaiduMap.addOverlay(overlayNow).setExtraInfo(nowBundle);
        }

//        if (selected) {
//            Toast.makeText(this, "selected", Toast.LENGTH_SHORT).show();
//            for (OverlayOptions i : overlayOptionsList) {
//                mBaiduMap.addOverlay(i);
//            }
//        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.map_item:
                Intent intent = new Intent(AllItemMapActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.all_content_add:
                Intent intent1 = new Intent(this,EditActivity.class);
                startActivity(intent1);
                break;
            case R.id.all_setting:
                if (slideMenu.isMainScreenShowing()) {
                    slideMenu.openMenu();
                } else {
                    slideMenu.closeMenu();
                }
                break;
        }
    }


    private class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            //Receive Location
            if (!located) {
                startLocation(location);
                located = true;
                mLocationClient.unRegisterLocationListener(myListener);
            }


//            Toast.makeText(AllItemMapActivity.this,"更新",Toast.LENGTH_LONG).show();
//            LatLng point = new LatLng(location.getLatitude(), location.getLongitude());
//            if(SpatialRelationUtil.isCircleContainsPoint(point, 100, point)) {
//                Toast.makeText(MapActivity.this,"succeed",Toast.LENGTH_LONG).show();
//            }
//            else {
//                Toast.makeText(MapActivity.this,"failed",Toast.LENGTH_LONG).show();
//            }
        }
    }


//    BaiduMap.OnMapLongClickListener listener = new BaiduMap.OnMapLongClickListener() {
//        /**
//         * 地图长按事件监听回调函数
//         * @param point 长按的地理坐标
//         */
//        public void onMapLongClick(LatLng point) {
//            realtimeBitmap = BitmapDescriptorFactory
//                    .fromResource(R.drawable.icon_gcoding);
//
//            overlaySelected = new MarkerOptions().position(point)
//                    .icon(realtimeBitmap).zIndex(9).draggable(true);
//            mBaiduMap.addOverlay(overlaySelected);
//            latitude = point.latitude;
//            longitude = point.longitude;
//            selected = true;
//        }
//    };


}
