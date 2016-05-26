package android.geekband006.Main;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.geekband006.R;
import android.media.MediaPlayer;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by cm on 16/5/7.
 */
public class MyReceiver extends BroadcastReceiver {
    MediaPlayer alarmMusic;

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "onCreate", Toast.LENGTH_LONG).show();
        Log.d("onCreate", "onCreate");
        alarmMusic = MediaPlayer.create(context, R.raw.alarm);
        alarmMusic.setLooping(true);
        // 播放音乐
        alarmMusic.start();
        // 创建一个对话框
        new AlertDialog.Builder(context)
                .setTitle("闹钟")
                .setMessage("闹钟响了,Go！Go！Go！")
                .setPositiveButton(
                        "确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 停止音乐
                                alarmMusic.stop();
                                // 结束该Activity
                            }
                        }
                )
                .show();
    }
}
