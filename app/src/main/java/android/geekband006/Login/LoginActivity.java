package android.geekband006.Login;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.geekband006.Main.MainActivity;
import android.geekband006.MyApplication;
import android.geekband006.MyDatabaseHelper;
import android.geekband006.R;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;


public class LoginActivity extends Activity implements View.OnClickListener {
    private MyDatabaseHelper myDatabaseHelper;
    private SQLiteDatabase sqLiteDatabase;
    private ImageView loginButton;
    private EditText accountText;
    private EditText passwordText;
    private TextView welcomeText;
    private Button exitButton;
    private TextView registerText;
    private TextView forgetText;
    private String userName = "";
    MyApplication myApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        myDatabaseHelper = new MyDatabaseHelper(this);
        sqLiteDatabase = myDatabaseHelper.getWritableDatabase();
        myApplication = (MyApplication)getApplication();

        if (myApplication.getNowUser() == "") {
            if (myApplication.getLastUser() != "") {
                userName = myApplication.getLastUser();
            }
            initLoginView();
        }
        else {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            LoginActivity.this.finish();
        }


//        Cursor cursor = sqLiteDatabase.query(MyDatabaseHelper.ITEM_INFO_TABLE_NAME,
//                new String[]{MyDatabaseHelper.ITEM_ID, MyDatabaseHelper.ALERM_DAY, MyDatabaseHelper.ALERM_TIME},
//                null, null, null, null, MyDatabaseHelper.ALERM_TIME + " desc ", String.valueOf(1));
//
//        if (cursor.moveToFirst() && cursor.getInt(1) == MyDatabaseHelper.IS_ACTIVE) {
//            //setContentView(R.layout.activity_main);
//            Intent intent = new Intent(this, MainActivity.class);
//            startActivity(intent);
//            LoginActivity.this.finish();
//        } else {
//            Log.d("time", "time" + String.valueOf(cursor.getString(2)));
//            if (!(cursor.getLong(2) == MyDatabaseHelper.INIT_TIME))
//                userName = cursor.getString(0);
//            initLoginView();
//        }


    }

    void initLoginView() {
        setContentView(R.layout.activity_login);
        accountText = (EditText) findViewById(R.id.account_edittext);
        passwordText = (EditText) findViewById(R.id.password_edittext);
        loginButton = (ImageView) findViewById(R.id.login_button);
        registerText = (TextView) findViewById(R.id.register_text);
        forgetText = (TextView) findViewById(R.id.forget_text);
        accountText.setText(userName);
        loginButton.setOnClickListener(this);
        registerText.setOnClickListener(this);
        forgetText.setOnClickListener(this);
    }

    void initMainView() {

//        setContentView(R.layout.activity_main);
//        welcomeText = (TextView)findViewById(R.id.welcome_textview);
//        welcomeText.setText("你已登录 " + userName);
//        exitButton = (Button) findViewById(R.id.exit_button);
//        exitButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.login_button:
                Login();
                break;
            case R.id.register_text:
                intent = new Intent(this, RegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.forget_text:
                intent = new Intent(this, ForgetActivity.class);
                startActivity(intent);
                break;
        }
    }

//    void Login() {
//        String account = accountText.getText().toString();
//        String password = passwordText.getText().toString();
//        Cursor cursor = sqLiteDatabase.query(MyDatabaseHelper.ITEM_INFO_TABLE_NAME,
//                new String[]{MyDatabaseHelper.ITEM_ID},
//                MyDatabaseHelper.ITEM_ID + " = \"" + account + "\" and " + MyDatabaseHelper.USER_PASSWORD + " = \"" + password + "\"",
//                null, null, null, null, null);
//        if (cursor.moveToFirst()) {
//            userName = cursor.getString(0);
//            initMainView();
//            ContentValues contentValues = new ContentValues();
//            contentValues.put(MyDatabaseHelper.ALERM_DAY, 1);
//            contentValues.put(MyDatabaseHelper.ALERM_TIME, System.currentTimeMillis());
//            sqLiteDatabase.update(MyDatabaseHelper.ITEM_INFO_TABLE_NAME, contentValues, MyDatabaseHelper.ITEM_ID + "=?", new String[]{userName});
//            //startMainActivity(cursor.getString(0));
//        } else {
//            Toast.makeText(this, "账号或密码错误", Toast.LENGTH_LONG).show();
//            passwordText.setText("");
//        }
//
//        cursor.close();
//    }

    private void Login() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.add("phone", accountText.getText().toString());
        params.add("password", passwordText.getText().toString());
        client.post("http://115.28.48.151/MyProject/login.php", params, new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes) {
                        String response = new String(bytes);
                        Log.e("debug", "state:" + response);
                        JSONObject object = null;
                        try {
                            object = new JSONObject(response);
                            String status = object.getString("status");
                            if (status.equals("error")) {
                                Toast.makeText(LoginActivity.this, "用户名或密码错误", Toast.LENGTH_LONG).show();
                            } else if (status.equals("error")) {
                                Toast.makeText(LoginActivity.this, "出现错误，请稍后重试", Toast.LENGTH_LONG).show();
                            } else if (status.equals("success")) {
                                initLoginState();
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                LoginActivity.this.finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes, Throwable throwable) {
                        Toast.makeText(LoginActivity.this, "网络错误，请检查网络设置", Toast.LENGTH_LONG).show();
                        Log.d("onFailure", throwable.toString());
                    }
                }
        );
    }

    private void initLoginState() {
        String name = accountText.getText().toString();
        myApplication.setNowUser(name);
        myApplication.setLastUser(name);
        SharedPreferences sharedPreferences = getSharedPreferences(MyApplication.PREFERENCE_NAME,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(MyApplication.LAST_USER_NAME,name);
        editor.putString(MyApplication.NOW_USER_NAME,name);
        editor.apply();
    }
}
