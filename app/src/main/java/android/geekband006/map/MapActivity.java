package android.geekband006.map;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;

import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.geekband006.R;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class MapActivity extends Activity implements View.OnClickListener{

    protected static MapView bmapView = null;
    protected static BaiduMap mBaiduMap = null;


    protected static BitmapDescriptor realtimeBitmap;


    protected static Context mContext = null;

    protected static MapStatusUpdate msUpdate = null;

    private Boolean updateFocus = true;
    private Boolean selected = false;

    protected static OverlayOptions overlayNow;
    protected static OverlayOptions overlaySelected;

    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new MyLocationListener();

    private TextView cancelText;
    private TextView confirmText;

    private double latitude;
    private double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_map);

        mContext = getApplicationContext();

        // 初始化组件
        initComponent();

        mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
        mLocationClient.registerLocationListener(myListener);    //注册监听函数

        initLocation();

        mLocationClient.start();

        mBaiduMap.setOnMapLongClickListener(listener);

        if (getIntent()!= null && getIntent().hasExtra("latitude")) {
            Intent infoIntent = getIntent();
            realtimeBitmap = BitmapDescriptorFactory
                    .fromResource(R.drawable.icon_gcoding);
            overlaySelected = new MarkerOptions().position(new LatLng(infoIntent.getDoubleExtra("latitude",0),infoIntent.getDoubleExtra("longitude",0)))
                    .icon(realtimeBitmap).zIndex(9).draggable(true);
            mBaiduMap.addOverlay(overlaySelected);
        }




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        setActionBarLayout(R.layout.activity_map_actionbar, this);
        cancelText = (TextView) findViewById(R.id.map_cancel);
        confirmText = (TextView) findViewById(R.id.map_confirm);
        cancelText.setOnClickListener(this);
        confirmText.setOnClickListener(this);
        return true;
    }

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

        bmapView = (MapView) findViewById(R.id.bmapView);
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
        mBaiduMap.clear();
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
            mBaiduMap.addOverlay(overlayNow);
        }

        if (selected) {
            mBaiduMap.addOverlay(overlaySelected);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.map_cancel:
                finish();
                break;
            case R.id.map_confirm:
                addDialog();
                break;
        }
    }

    private void addDialog() {
        final AlertDialog alertDialog = new AlertDialog.Builder(MapActivity.this).create();
        alertDialog.show();
        Window window = alertDialog.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        window.setContentView(R.layout.dialog_describe);
        final EditText describeText = (EditText)window.findViewById(R.id.describe_text);
        Button cancel = (Button) window.findViewById(R.id.describe_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                AlarmActivity.this.finish();
                alertDialog.cancel();
            }
        });
        Button confirm = (Button)window.findViewById(R.id.describe_confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("latitude",latitude);
                intent.putExtra("longitude",longitude);
                intent.putExtra("describe",describeText.getText().toString());
                setResult(1,intent);
                finish();
            }
        });
    }


    private class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            //Receive Location
            startLocation(location);
//            mLocationClient.unRegisterLocationListener(myListener);
//            Toast.makeText(MapActivity.this,"更新",Toast.LENGTH_LONG).show();
//            LatLng point = new LatLng(location.getLatitude(), location.getLongitude());
//            if(SpatialRelationUtil.isCircleContainsPoint(point, 100, point)) {
//                Toast.makeText(MapActivity.this,"succeed",Toast.LENGTH_LONG).show();
//            }
//            else {
//                Toast.makeText(MapActivity.this,"failed",Toast.LENGTH_LONG).show();
//            }
        }
    }


    BaiduMap.OnMapLongClickListener listener = new BaiduMap.OnMapLongClickListener() {
        /**
         * 地图长按事件监听回调函数
         * @param point 长按的地理坐标
         */
        public void onMapLongClick(LatLng point) {
            realtimeBitmap = BitmapDescriptorFactory
                    .fromResource(R.drawable.icon_gcoding);

            overlaySelected = new MarkerOptions().position(point)
                    .icon(realtimeBitmap).zIndex(9).draggable(true);
            mBaiduMap.addOverlay(overlaySelected);
            latitude = point.latitude;
            longitude = point.longitude;
            selected = true;
        }
    };


}
