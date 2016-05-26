package android.geekband006;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by cm on 16/3/31.
 */
public class MyDatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "item_info.db";
    public static final String ITEM_INFO_TABLE_NAME = "item_info";
    public static final String ITEM_ID = "item_id";
    public static final String ITEM_CONTENT = "item_content";
    public static final String ITEM_DESCRIBE = "item_describe";
    public static final String ALARM_YEAR = "alarm_year";
    public static final String ALARM_MONTH = "alarm_month";
    public static final String ALARM_DAY = "alarm_day";
    public static final String ALARM_HOUR = "alarm_hour";
    public static final String ALARM_DATE = "alarm_date";
    public static final String ALARM_TIME = "alarm_time";
    public static final String ALARM_MINUTE = "alarm_minute";

    public static final String ALARM_BEFORE = "alarm_before";

    public static final String LOCATION_LATITUDE = "location_latitude";
    public static final String LOCATION_LONGITUDE = "location_longitude";
    public static final String ALARM_STATE = "alarm_state";


    public static final int VERSION = 2;
    public static final long INIT_TIME = 0;


//    String user1 = "'user1'," +


    public MyDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("MyDatabaseHelper", "onCreate");
        db.execSQL("create table " + ITEM_INFO_TABLE_NAME + " ("
                + ITEM_ID + " INTEGER NOT NULL PRIMARY KEY autoincrement, "
                + ITEM_CONTENT + " text NOT NULL, "
                + ALARM_DATE + " text NOT NULL, "
                + ALARM_TIME + " text NOT NULL, "
                + ALARM_BEFORE + " INTEGER DEFAULT 0, "
                + LOCATION_LATITUDE + " double,"
                + LOCATION_LONGITUDE + " double,"
                + ALARM_STATE + " INTEGER DEFAULT 0,"
                + ITEM_DESCRIBE + " text)");

        long time = INIT_TIME;
        insertInfo(db,"快点击右上角的+号添加事项吧","紫金港", "20080808","0808",0,0,0,0);
//        insertInfo(db, "事情1", "20160302", "1220", 15, 1.1, 1.1, 1);
//        insertInfo(db, "事情2", "20160302", "1220", 15, 1.1, 1.1, 0);
//        insertInfo(db, "事情3", "20160302", "1220", 15, 1.1, 1.1, 0);
//        insertInfo(db, "事情4", "20160302", "1220", 15, 1.1, 1.1, 0);
//        insertInfo(db, "事情5", "20160304", "1230", 15, 1.1, 1.1, 0);
//        insertInfo(db, "事情6", "20160304", "1220", 15, 1.1, 1.1, 0);
//        insertInfo(db, "事情7", "20160309", "1220", 15, 1.1, 1.1, 0);
//        insertInfo(db, "事情8", "20160403", "1230", 15, 1.1, 1.1, 0);

    }

    public void insertInfo(SQLiteDatabase db, String content, String describe, String date, String time, int before, double latitude, double longitude, int state) {
        Log.d("MyDatabaseHelper", "insertInfo");
        ContentValues contentValues = new ContentValues();
        contentValues.put(ITEM_CONTENT, content);
        contentValues.put(ITEM_DESCRIBE, describe);
        contentValues.put(ALARM_DATE, date);
        contentValues.put(ALARM_TIME, time);
        contentValues.put(ALARM_BEFORE, before);
//        contentValues.put(ALARM_MILLIS, millis);
        contentValues.put(LOCATION_LATITUDE, latitude);
        contentValues.put(LOCATION_LONGITUDE, longitude);
        contentValues.put(ALARM_STATE, state);
        Log.d("id", "id=" + db.insert(ITEM_INFO_TABLE_NAME, null, contentValues));
    }

//    @Override
//    public void onCreate(SQLiteDatabase db) {
//        Log.d("MyDatabaseHelper", "onCreate");
//        db.execSQL("create table " + ITEM_INFO_TABLE_NAME + " ("
//                + ITEM_ID + " INTEGER NOT NULL PRIMARY KEY autoincrement, "
//                + ITEM_CONTENT + " text NOT NULL, "
//                + ALARM_YEAR + " INTEGER NOT NULL, "
//                + ALARM_MONTH + " INTEGER NOT NULL, "
//                + ALARM_DAY + " INTEGER NOT NULL, "
//                + ALARM_HOUR + " INTEGER NOT NULL, "
//                + ALARM_MINUTE + " INTEGER NOT NULL, "
//                + ALARM_DATE + " text NOT NULL, "
//                + ALARM_TIME + " text NOT NULL, "
//                + ALARM_BEFORE + " INTEGER DEFAULT 0, "
//                + LOCATION_LATITUDE + " double,"
//                + LOCATION_LONGITUDE + " double,"
//                + ALARM_STATE + " INTEGER DEFAULT 0)");
//
//        long time = INIT_TIME;
////        insertInfo(db, "事情1", 2016, 4, 10, 12, 30, 15, 1.1, 1.1, 1);
//
//    }
//
//    public void insertInfo(SQLiteDatabase db, String content, int year, int month, int day, int hour, int minute, int before, double latitude, double longitude, int state) {
//        Log.d("MyDatabaseHelper", "insertInfo");
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(ITEM_CONTENT, content);
//        contentValues.put(ALARM_YEAR, year);
//        contentValues.put(ALARM_MONTH, month);
//        contentValues.put(ALARM_DAY, day);
//        contentValues.put(ALARM_HOUR, hour);
//        contentValues.put(ALARM_MINUTE, minute);
//        contentValues.put(ALARM_BEFORE, before);
////        contentValues.put(ALARM_MILLIS, millis);
//        contentValues.put(LOCATION_LATITUDE, latitude);
//        contentValues.put(LOCATION_LONGITUDE, longitude);
//        contentValues.put(ALARM_STATE, state);
//        Log.d("id", "id=" + db.insert(ITEM_INFO_TABLE_NAME, null, contentValues));
//    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
