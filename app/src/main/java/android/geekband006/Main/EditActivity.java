package android.geekband006.Main;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.geekband006.MyDatabaseHelper;
import android.geekband006.R;
import android.geekband006.map.MapActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by cm on 16/5/4.
 */
public class EditActivity extends Activity implements View.OnClickListener {
    private Button cancelBtn;
    private Button comfirmBtn;
    private DatePicker datePicker;
    private TimePicker timePicker;
    private EditText contentText;
    private MyDatabaseHelper myDatabaseHelper;
    private ImageView ic15min;
    private ImageView ic15minClicked;
    private ImageView ic30min;
    private ImageView ic30minClicked;
    private ImageView ic1hour;
    private ImageView ic1hourClicked;
    private ImageView ic1day;
    private ImageView ic1dayClicked;
    private RelativeLayout locate;
    private int minutes = 0;

    private double latitude = 0;
    private double longitude = 0;
    private String describe = " ";
    private boolean isEdit = false;
    private Long id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_edit);
        initView();
        if (getIntent() != null && getIntent().hasExtra("content")) {
            isEdit = true;
            Intent infoIntent = getIntent();

            id = infoIntent.getLongExtra("id",0);

            contentText.setText(infoIntent.getStringExtra("content"));
            Log.d("content",infoIntent.getStringExtra("content"));
            datePicker.updateDate(
                            Integer.valueOf(infoIntent.getStringExtra("date").substring(0, 4)),
                            Integer.valueOf(infoIntent.getStringExtra("date").substring(5, 7)) - 1,
                            Integer.valueOf(infoIntent.getStringExtra("date").substring(8, 10)));
            timePicker.setCurrentHour(Integer.valueOf(infoIntent.getStringExtra("time").substring(0, 2)));
            timePicker.setCurrentMinute(Integer.valueOf(infoIntent.getStringExtra("time").substring(3, 5)));

            Log.d("date","" + Integer.valueOf(infoIntent.getStringExtra("date").substring(0, 4)));
            Log.d("date","" + Integer.valueOf(infoIntent.getStringExtra("date").substring(5, 7)));
            Log.d("date","" + Integer.valueOf(infoIntent.getStringExtra("date").substring(8, 10)));
            Log.d("time","" + Integer.valueOf(infoIntent.getStringExtra("time").substring(0, 2)));
            Log.d("time","" + Integer.valueOf(infoIntent.getStringExtra("time").substring(3, 5)));

            Log.d("before","" + infoIntent.getIntExtra("before",0));

            switch (infoIntent.getIntExtra("before",0)) {
                case 15:
                    ic15min.setVisibility(View.INVISIBLE);
                    ic15minClicked.setVisibility(View.VISIBLE);
                    minutes = 15;
                    break;
                case 30:
                    ic30min.setVisibility(View.INVISIBLE);
                    ic30minClicked.setVisibility(View.VISIBLE);
                    minutes = 30;
                    break;
                case 60:
                    ic1hour.setVisibility(View.INVISIBLE);
                    ic1hourClicked.setVisibility(View.VISIBLE);
                    minutes = 60;
                    break;
                case 1440:
                    ic1day.setVisibility(View.INVISIBLE);
                    ic1dayClicked.setVisibility(View.VISIBLE);
                    minutes = 1440;
                    break;
            }
            latitude = infoIntent.getDoubleExtra("latitude",0);
            longitude = infoIntent.getDoubleExtra("longitude",0);
        }

    }

    void initView() {
        cancelBtn = (Button) findViewById(R.id.edit_cancel);
        comfirmBtn = (Button) findViewById(R.id.edit_confirm);
        contentText = (EditText) findViewById(R.id.content_edittext);
        ic15min = (ImageView) findViewById(R.id.clock_15min);
        ic15minClicked = (ImageView) findViewById(R.id.clock_15min_clicked);
        ic30min = (ImageView) findViewById(R.id.clock_30min);
        ic30minClicked = (ImageView) findViewById(R.id.clock_30min_clicked);
        ic1hour = (ImageView) findViewById(R.id.clock_1hour);
        ic1hourClicked = (ImageView) findViewById(R.id.clock_1hour_clicked);
        ic1day = (ImageView) findViewById(R.id.clock_1day);
        ic1dayClicked = (ImageView) findViewById(R.id.clock_1day_clicked);
        locate = (RelativeLayout) findViewById(R.id.edit_layout);

        cancelBtn.setOnClickListener(this);
        comfirmBtn.setOnClickListener(this);
        ic15min.setOnClickListener(this);
        ic15minClicked.setOnClickListener(this);
        ic30min.setOnClickListener(this);
        ic30minClicked.setOnClickListener(this);
        ic1hour.setOnClickListener(this);
        ic1hourClicked.setOnClickListener(this);
        ic1day.setOnClickListener(this);
        ic1dayClicked.setOnClickListener(this);

        locate.setOnClickListener(this);


        datePicker = (DatePicker) findViewById(R.id.date_picke);
        timePicker = (TimePicker) findViewById(R.id.time_picker);
        timePicker.setIs24HourView(true);
        resizePikcer(datePicker);
        resizePikcer(timePicker);
    }

    void setAlarm(Bundle bundle) {
        Intent intent = new Intent(EditActivity.this, AlarmActivity.class);
        intent.putExtra("bundle",bundle);
        PendingIntent sender = PendingIntent.getActivity(
                EditActivity.this, 0, intent, 0);

        Calendar calendar = Calendar.getInstance();
        calendar.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(), timePicker.getCurrentHour(), timePicker.getCurrentMinute());
        calendar.add(Calendar.MINUTE, -minutes);
        Log.d("time", calendar.getTime().toString());
//        Log.d("time", "" + datePicker.getYear() + datePicker.getMonth() + (datePicker.getDayOfMonth()+1) + timePicker.getCurrentHour() + timePicker.getCurrentMinute() );
        // Schedule the alarm!
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
        Toast.makeText(this, "success", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edit_cancel:
                finish();
                break;
            case R.id.edit_confirm:
                addItem();
                break;
            case R.id.edit_layout:
                Intent intent = new Intent(this, MapActivity.class);
                intent.putExtra("latitude",latitude);
                intent.putExtra("longitude",longitude);
                startActivityForResult(intent, 1);
                break;
            case R.id.clock_15min:
                ic15min.setVisibility(View.INVISIBLE);
                ic15minClicked.setVisibility(View.VISIBLE);

                ic30minClicked.setVisibility(View.INVISIBLE);
                ic1hourClicked.setVisibility(View.INVISIBLE);
                ic1dayClicked.setVisibility(View.INVISIBLE);

                ic30min.setVisibility(View.VISIBLE);
                ic1hour.setVisibility(View.VISIBLE);
                ic1day.setVisibility(View.VISIBLE);

                minutes = 15;

                break;
            case R.id.clock_15min_clicked:
                ic15minClicked.setVisibility(View.INVISIBLE);
                ic15min.setVisibility(View.VISIBLE);

                ic30minClicked.setVisibility(View.INVISIBLE);
                ic1hourClicked.setVisibility(View.INVISIBLE);
                ic1dayClicked.setVisibility(View.INVISIBLE);

                ic30min.setVisibility(View.VISIBLE);
                ic1hour.setVisibility(View.VISIBLE);
                ic1day.setVisibility(View.VISIBLE);

                minutes = 0;

                break;
            case R.id.clock_30min:
                ic30min.setVisibility(View.INVISIBLE);
                ic30minClicked.setVisibility(View.VISIBLE);

                ic15minClicked.setVisibility(View.INVISIBLE);
                ic1hourClicked.setVisibility(View.INVISIBLE);
                ic1dayClicked.setVisibility(View.INVISIBLE);

                ic15min.setVisibility(View.VISIBLE);
                ic1hour.setVisibility(View.VISIBLE);
                ic1day.setVisibility(View.VISIBLE);

                minutes = 30;

                break;
            case R.id.clock_30min_clicked:

                ic30minClicked.setVisibility(View.INVISIBLE);
                ic30min.setVisibility(View.VISIBLE);

                ic15minClicked.setVisibility(View.INVISIBLE);
                ic1hourClicked.setVisibility(View.INVISIBLE);
                ic1dayClicked.setVisibility(View.INVISIBLE);

                ic15min.setVisibility(View.VISIBLE);
                ic1hour.setVisibility(View.VISIBLE);
                ic1day.setVisibility(View.VISIBLE);

                minutes = 0;

                break;
            case R.id.clock_1hour:
                ic1hour.setVisibility(View.INVISIBLE);
                ic1hourClicked.setVisibility(View.VISIBLE);

                ic15minClicked.setVisibility(View.INVISIBLE);
                ic30minClicked.setVisibility(View.INVISIBLE);
                ic1dayClicked.setVisibility(View.INVISIBLE);

                ic15min.setVisibility(View.VISIBLE);
                ic30min.setVisibility(View.VISIBLE);
                ic1day.setVisibility(View.VISIBLE);

                minutes = 60;

                break;
            case R.id.clock_1hour_clicked:
                ic1hourClicked.setVisibility(View.INVISIBLE);
                ic1hour.setVisibility(View.VISIBLE);

                ic15minClicked.setVisibility(View.INVISIBLE);
                ic30minClicked.setVisibility(View.INVISIBLE);
                ic1dayClicked.setVisibility(View.INVISIBLE);

                ic15min.setVisibility(View.VISIBLE);
                ic30min.setVisibility(View.VISIBLE);
                ic1day.setVisibility(View.VISIBLE);

                minutes = 60;

                break;
            case R.id.clock_1day:
                ic1day.setVisibility(View.INVISIBLE);
                ic1dayClicked.setVisibility(View.VISIBLE);

                ic15minClicked.setVisibility(View.INVISIBLE);
                ic30minClicked.setVisibility(View.INVISIBLE);
                ic1hourClicked.setVisibility(View.INVISIBLE);

                ic15min.setVisibility(View.VISIBLE);
                ic30min.setVisibility(View.VISIBLE);
                ic1hour.setVisibility(View.VISIBLE);

                minutes = 1440;

                break;
            case R.id.clock_1day_clicked:
                ic1dayClicked.setVisibility(View.INVISIBLE);
                ic1day.setVisibility(View.VISIBLE);

                ic15minClicked.setVisibility(View.INVISIBLE);
                ic30minClicked.setVisibility(View.INVISIBLE);
                ic1hourClicked.setVisibility(View.INVISIBLE);

                ic15min.setVisibility(View.VISIBLE);
                ic30min.setVisibility(View.VISIBLE);
                ic1hour.setVisibility(View.VISIBLE);

                minutes = 0;

                break;
        }

    }

    private void addItem() {
        if (contentText.getText().toString().isEmpty()) {
            Toast.makeText(this, "请输入提醒内容", Toast.LENGTH_LONG).show();
            Log.d("addItem", "addItem");
            return;
        }
        myDatabaseHelper = new MyDatabaseHelper(this);
        SQLiteDatabase myDatabase = myDatabaseHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(MyDatabaseHelper.ITEM_CONTENT, contentText.getText().toString());
//        contentValues.put(MyDatabaseHelper.ALARM_YEAR, datePicker.getYear());
//        contentValues.put(MyDatabaseHelper.ALARM_MONTH, datePicker.getMonth());
//        contentValues.put(MyDatabaseHelper.ALARM_DAY, datePicker.getDayOfMonth());
//        contentValues.put(MyDatabaseHelper.ALARM_HOUR, timePicker.getCurrentHour());
//        contentValues.put(MyDatabaseHelper.ALARM_MINUTE, timePicker.getCurrentMinute());
        contentValues.put(MyDatabaseHelper.ALARM_DATE, timeToString(datePicker.getYear()) + timeToString(datePicker.getMonth() + 1) + timeToString(datePicker.getDayOfMonth()));
        contentValues.put(MyDatabaseHelper.ALARM_TIME, timeToString(timePicker.getCurrentHour()) + timeToString(timePicker.getCurrentMinute()));
        contentValues.put(MyDatabaseHelper.ALARM_BEFORE, minutes);
        contentValues.put(MyDatabaseHelper.LOCATION_LATITUDE, latitude);
        contentValues.put(MyDatabaseHelper.LOCATION_LONGITUDE, longitude);
        contentValues.put(MyDatabaseHelper.ITEM_DESCRIBE, describe);

        Toast.makeText(this, timeToString(timePicker.getCurrentHour()) + timeToString(timePicker.getCurrentMinute()), Toast.LENGTH_LONG).show();
        Log.d("date", "" + datePicker.getYear() + datePicker.getMonth() + datePicker.getDayOfMonth());
        if (isEdit) {
            myDatabase.update(MyDatabaseHelper.ITEM_INFO_TABLE_NAME,contentValues, MyDatabaseHelper.ITEM_ID + " = " + id, null);
        }
        else {
            id = myDatabase.insert(MyDatabaseHelper.ITEM_INFO_TABLE_NAME, null, contentValues);
        }
        Bundle bundle = new Bundle();
        bundle.putLong("id",id);
        bundle.putString("content",contentText.getText().toString());
        bundle.putString("describe",describe);
        bundle.putString("date",timeToString(datePicker.getYear()) + "." + timeToString(datePicker.getMonth() + 1) + "." + timeToString(datePicker.getDayOfMonth()));
        bundle.putString("time",timeToString(timePicker.getCurrentHour()) + ":" + timeToString(timePicker.getCurrentMinute()));
        setAlarm(bundle);
        finish();
    }

    String timeToString(int i) {
        if (i < 10) {
            return 0 + String.valueOf(i);
        } else {
            return String.valueOf(i);
        }
    }


    private void resizePikcer(FrameLayout tp) {
        List<NumberPicker> npList = findNumberPicker(tp);
        for (NumberPicker np : npList) {
            resizeNumberPicker(np);
        }
    }

    /**
     * 得到viewGroup里面的numberpicker组件
     *
     * @param viewGroup
     * @return
     */
    private List<NumberPicker> findNumberPicker(ViewGroup viewGroup) {
        List<NumberPicker> npList = new ArrayList<NumberPicker>();
        View child = null;
        if (null != viewGroup) {
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                child = viewGroup.getChildAt(i);
                if (child instanceof NumberPicker) {
                    npList.add((NumberPicker) child);
                } else if (child instanceof LinearLayout) {
                    List<NumberPicker> result = findNumberPicker((ViewGroup) child);
                    if (result.size() > 0) {
                        return result;
                    }
                }
            }
        }
        return npList;
    }

    /*
         * 调整numberpicker大小
		 */
    private void resizeNumberPicker(NumberPicker np) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(85, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(10, 0, 10, 0);
        np.setLayoutParams(params);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (null != data) {
            latitude = data.getDoubleExtra("latitude", 0);
            longitude = data.getDoubleExtra("longitude", 0);
            describe = data.getStringExtra("describe");
        }
//        Toast.makeText(this,"" + data.getDoubleExtra("latitude",0) + data.getDoubleExtra("longitude",0),Toast.LENGTH_LONG).show();
    }
}
