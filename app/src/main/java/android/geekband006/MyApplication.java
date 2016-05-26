package android.geekband006;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.baidu.mapapi.SDKInitializer;

/**
 * Created by cm on 16/4/28.
 */
public class MyApplication extends Application {
    public static final String PREFERENCE_NAME = "loginPreference";
    public static final String NOW_USER_NAME = "nowUser";
    public static final String LAST_USER_NAME = "lastUser";

    public String nowUser;
    public String lastUser;
    public MyApplication myApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences sharedPreferences = this.getSharedPreferences(PREFERENCE_NAME,MODE_PRIVATE);
        nowUser = sharedPreferences.getString(NOW_USER_NAME,"");
        lastUser = sharedPreferences.getString(LAST_USER_NAME,"");

        SDKInitializer.initialize(getApplicationContext());

    }

    public String getNowUser() {
        return nowUser;
    }

    public void setNowUser(String nowUser) {
        this.nowUser = nowUser;
    }

    public String getLastUser() {
        return lastUser;
    }

    public void setLastUser(String lastUser) {
        this.lastUser = lastUser;
    }


}
