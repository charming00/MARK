package android.geekband006.Login;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.geekband006.Main.MainActivity;
import android.geekband006.MyApplication;
import android.geekband006.R;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.loopj.android.http.*;

import org.json.*;


import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

/**
 * Created by cm on 16/4/13.
 */
public class RegisterActivity extends Activity implements View.OnClickListener {
    private EditText nameText;
    private EditText phoneText;
    private EditText checkNumText;
    private EditText firstPasswordText;
    private EditText secondPasswordText;
    private ImageView sendCheckNumBtn;
    private ImageView registerBtn;
    String des;
    private String phoneNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_register);
        initView();
        initSms();
    }

    private void initSms() {
        SMSSDK.initSDK(this, "11beabea88977", "f8708638fab2550d20715b574b02ac85");
        EventHandler eh = new EventHandler() {
            @Override
            public void afterEvent(int event, int result, Object data) {
                Message message = handler.obtainMessage();
                if (result == SMSSDK.RESULT_COMPLETE) {
                    Log.d("eventhandler", "RESULT_COMPLETE");
                    //回调完成
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                        Log.d("eventhandler", "EVENT_SUBMIT_VERIFICATION_CODE");
                        message.what = 0;
                        handler.sendMessage(message);
                        //提交验证码成功
                    } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                        Log.d("eventhandler", "EVENT_GET_VERIFICATION_CODE");
                        message.what = 1;
                        handler.sendMessage(message);
                        //获取验证码成功
                    }
                } else {
                    try {
                        Throwable throwable = (Throwable) data;
                        throwable.printStackTrace();
                        JSONObject object = new JSONObject(throwable.getMessage());
                        des = object.optString("detail");//错误描述
                        int status = object.optInt("status");//错误代码
                        Log.d("des", des);
                        Log.d("status", String.valueOf(status));
                        if (status > 0 && !TextUtils.isEmpty(des)) {
                            message.what = status;
                            handler.sendMessage(message);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        SMSSDK.registerEventHandler(eh); //注册短信回调
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_LONG).show();
                    register();
                    break;
                case 1:
                    Toast.makeText(RegisterActivity.this, "验证码发送成功", Toast.LENGTH_LONG).show();
                    break;
                case 462:
                    Toast.makeText(RegisterActivity.this, "获取验证码过于频繁，请在一分钟之后再次尝试", Toast.LENGTH_LONG).show();
                    break;
                case 468:
                    Toast.makeText(RegisterActivity.this, "验证码错误", Toast.LENGTH_LONG).show();
                    break;
                case 603:
                    Toast.makeText(RegisterActivity.this, "请输入正确的手机号", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };

    private void initView() {
        nameText = (EditText) findViewById(R.id.register_name);
        phoneText = (EditText) findViewById(R.id.register_phone);
        checkNumText = (EditText) findViewById(R.id.register_checknum);
        firstPasswordText = (EditText) findViewById(R.id.register_password);
        secondPasswordText = (EditText) findViewById(R.id.register_recheck);
        sendCheckNumBtn = (ImageView) findViewById(R.id.send_checknum);
        registerBtn = (ImageView) findViewById(R.id.register_button);
        sendCheckNumBtn.setOnClickListener(this);
        registerBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send_checknum:
                sendCheckNum();
                break;
            case R.id.register_button:
                submitCheckNum();
        }
    }

    public void register() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.add("phone", phoneText.getText().toString());
        params.add("username", nameText.getText().toString());
        params.add("password", firstPasswordText.getText().toString());
        client.post("http://115.28.48.151/MyProject/reg.php", params, new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes) {
                        String response = new String(bytes);
                        Log.e("debug", "state:"+response);
                        JSONObject object = null;
                        try {
                            object = new JSONObject(response);
                            String status = object.getString("status");
                            if (status.equals("exists")) {
                                Toast.makeText(RegisterActivity.this, "该手机已注册请直接登录", Toast.LENGTH_LONG).show();
                            } else if (status.equals("error")) {
                                Toast.makeText(RegisterActivity.this, "出现错误，请稍后重试", Toast.LENGTH_LONG).show();

                            } else if (status.equals("success")) {
                                initLoginState();
                                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                startActivity(intent);
                                RegisterActivity.this.finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes, Throwable throwable) {
                        Toast.makeText(RegisterActivity.this, "网络错误，请检查网络设置", Toast.LENGTH_LONG).show();
                        Log.d("onFailure",throwable.toString());
                    }
                }
        );
    }

    private void submitCheckNum() {
        if (nameText.getText().toString() == "") {
            Toast.makeText(RegisterActivity.this, "用户名不得为空", Toast.LENGTH_LONG).show();
            return;
        }
        if (phoneText.getText().toString() == "") {
            Toast.makeText(RegisterActivity.this, "手机号不得为空", Toast.LENGTH_LONG).show();
            return;
        }
        if (checkNumText.getText().toString() == "") {
            Toast.makeText(RegisterActivity.this, "验证码不得为空", Toast.LENGTH_LONG).show();
            return;
        }
        if (firstPasswordText.getText().toString() == "") {
            Toast.makeText(RegisterActivity.this, "密码不得为空", Toast.LENGTH_LONG).show();
            return;
        }
        if (secondPasswordText.getText().toString() == "") {
            Toast.makeText(RegisterActivity.this, "密码不得为空", Toast.LENGTH_LONG).show();
            return;
        }
        if (!firstPasswordText.getText().toString().equals(secondPasswordText.getText().toString())) {
            Toast.makeText(RegisterActivity.this, "两次密码不一致", Toast.LENGTH_LONG).show();
            return;
        }

        if (phoneNum == null || phoneNum == "") {
            Toast.makeText(RegisterActivity.this, "请先获取验证码", Toast.LENGTH_LONG).show();
            return;
        }

        SMSSDK.submitVerificationCode("86", phoneNum, checkNumText.getText().toString());

    }

    private void sendCheckNum() {
        phoneNum = phoneText.getText().toString();
        SMSSDK.getVerificationCode("86", phoneNum);
    }

    private void initLoginState() {
        MyApplication myApplication = (MyApplication)getApplication();
        String name = phoneText.getText().toString();
        myApplication.setNowUser(name);
        myApplication.setLastUser(name);
        SharedPreferences sharedPreferences = getSharedPreferences(MyApplication.PREFERENCE_NAME,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(MyApplication.LAST_USER_NAME,name);
        editor.putString(MyApplication.NOW_USER_NAME,name);
        editor.apply();
    }
}
