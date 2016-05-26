package android.geekband006.Main;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.geekband006.MyDatabaseHelper;
import android.geekband006.R;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by cm on 16/5/7.
 */
public class AlarmActivity extends Activity {
    MediaPlayer alarmMusic;
    Bundle bundle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bundle = getIntent().getBundleExtra("bundle");
        setVibrator();
        sendNotification();


//        Toast.makeText(this, "onCreate", Toast.LENGTH_LONG).show();
//        Log.d("onCreate", "onCreate" + getIntent().getLongExtra("id", 0));
//        alarmMusic = MediaPlayer.create(this, R.raw.alarm);
//        alarmMusic.setLooping(true);
//        // 播放音乐
//        alarmMusic.start();
        // 创建一个对话框
//        new AlertDialog.Builder(AlarmActivity.this)
//                .setTitle("闹钟")
//                .setMessage("闹钟响了,Go！Go！Go！")
//                .setPositiveButton(
//                        "确定",
//                        new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                // 停止音乐
//                                alarmMusic.stop();
//                                // 结束该Activity
//                                AlarmActivity.this.finish();
//                            }
//                        }
//                )
//                .show();
        final AlertDialog alertDialog = new AlertDialog.Builder(AlarmActivity.this).create();
        alertDialog.show();
        Window window = alertDialog.getWindow();
        window.setContentView(R.layout.dialog_alarm);

        Button knowBtn = (Button) window.findViewById(R.id.dialog_know);
        Button doneBtn = (Button) window.findViewById(R.id.dialog_done);
        TextView content = (TextView) window.findViewById(R.id.dialog_content);
        TextView time = (TextView) window.findViewById(R.id.dialog_time);
        TextView describe = (TextView) window.findViewById(R.id.dialog_describe);

        content.setText(bundle.getString("content"));
        time.setText(bundle.getString("date") + " " + bundle.getString("time"));
        describe.setText(bundle.getString("describe"));


        knowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlarmActivity.this.finish();
//                alertDialog.cancel();
            }
        });

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyDatabaseHelper myDatabaseHelper = new MyDatabaseHelper(AlarmActivity.this);
                SQLiteDatabase myDatabase = myDatabaseHelper.getWritableDatabase();
                ContentValues contentValues = new ContentValues();
                contentValues.put(MyDatabaseHelper.ALARM_STATE, 1);
                myDatabase.update(MyDatabaseHelper.ITEM_INFO_TABLE_NAME,contentValues,MyDatabaseHelper.ITEM_ID + " = " + bundle.getLong("id"), null);

                AlarmActivity.this.finish();

//                alertDialog.cancel();
            }
        });

    }

    private void sendNotification() {
        NotificationManager mNManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification notify;
        Notification.Builder mBuilder = new Notification.Builder(this);
        mBuilder.setContentTitle("MARK提醒")                        //标题
                .setContentText(bundle.getString("content"))      //内容
                .setTicker("您有一条MARK提醒~")             //收到信息后状态栏显示的文字信息
                .setWhen(System.currentTimeMillis())           //设置通知时间
                .setSmallIcon(R.mipmap.ic_launcher)            //设置小图标
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE)    //设置默认的三色灯与振动器
                .setAutoCancel(true) ;                        //设置点击后取消Notification
        notify = mBuilder.getNotification();
        mNManager.notify(1, notify);


    }

    private void setVibrator() {
        Vibrator vibrator;
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {100, 400, 100, 400};   // 停止 开启 停止 开启
        vibrator.vibrate(pattern, -1);
    }
}
