package com.example.fruit.salerapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fruit.salerapplication.commontool.ConfigUtil;
import com.example.fruit.salerapplication.testhttpapi.service.MyHttpService;
import com.example.fruit.salerapplication.testhttpapi.service.constants.RequestType;

import java.util.Map;


/**
 * Created by fruit on 2017/7/12.
 */

public class ForgetPassword extends AppCompatActivity {
    Button btnReturn, btnSendNewPassword;
    boolean isUsername = false;
    boolean isEmail = false;
    boolean isPassword = false;
    boolean isPasswordConfirm = false;
    boolean isCode = false;
    private MyHttpService myHttpService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        init();
        initView();
        bindView();
    }



    View.OnFocusChangeListener userNameListener = new View.OnFocusChangeListener() {
        private String temp;
        private String regex = "^[a-z0-9A-Z]+$";

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            EditText _v = (EditText) v;
            temp = _v.getText().toString();
            if (!hasFocus) {
                isUsername = false;
                if (temp.isEmpty()) {
                    Toast.makeText(ForgetPassword.this, "用户名不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    if (!temp.matches(regex)) {
                        Toast.makeText(ForgetPassword.this, "用户名只能包含大小写字母与数字", Toast.LENGTH_SHORT).show();
                    } else {
                        if (temp.length() > 20) {
                            Toast.makeText(ForgetPassword.this, "用户名输入过长", Toast.LENGTH_SHORT).show();
                        } else {
                            isUsername = true;
                        }
                    }
                }
            }
        }

    };


    View.OnFocusChangeListener emailListener = new View.OnFocusChangeListener() {
        private String temp;
        String regex = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";


        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            EditText _v = (EditText) v;
            temp = _v.getText().toString();
            if (!hasFocus) {
                isEmail = false;
                if (temp.isEmpty()) {
                    Toast.makeText(ForgetPassword.this, "邮箱不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    if (!temp.matches(regex)) {
                        Toast.makeText(ForgetPassword.this, "邮箱格式错误", Toast.LENGTH_SHORT).show();
                    } else {
                        isEmail = true;
                    }
                }
            }
        }

    };


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Intent intent = null;
            switch (msg.what) {
                case RequestType.USER_FINDBACK_PASSWORD:
                    if (msg.getData().getBoolean("success")) {
                        Toast.makeText(ForgetPassword.this, "新密码已发送", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(ForgetPassword.this, msg.getData().getString("message"), Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:
                    Toast.makeText(ForgetPassword.this, "未知请求类型", Toast.LENGTH_SHORT).show();
                    break;
                //TODO: other type of requst
            }
            super.handleMessage(msg);
        }
    };

    public void initView () {
        btnReturn = (Button) findViewById(R.id.btn_header_left);
        btnSendNewPassword = (Button) findViewById(R.id.forget_password_send_new_password);
        findViewById(R.id.btn_header_right).setVisibility(View.INVISIBLE);
        ((TextView) findViewById(R.id.text_title)).setText("忘记密码");


        ((TextView) findViewById(R.id.forget_password_name)).setOnFocusChangeListener(userNameListener);
        ((TextView) findViewById(R.id.forget_password_email)).setOnFocusChangeListener(emailListener);
    }

    private void init() {
        Map<String, String> serverConfigMap = ConfigUtil.loadServerConfig(ConfigUtil.SERVER_CONFIG_FILENAME, getAssets());
        if (serverConfigMap != null) {
            myHttpService = new MyHttpService(handler, serverConfigMap);
        }
    }

    public void bindView () {
        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ForgetPassword.this.finish();
            }
        });

        btnSendNewPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isEmail&!isUsername) {
                    Toast.makeText(ForgetPassword.this, "请检查输入格式", Toast.LENGTH_SHORT).show();
                }
                else {
                    String email = ((TextView) findViewById(R.id.forget_password_email)).getText().toString();
                    String userame = ((TextView) findViewById(R.id.forget_password_name)).getText().toString();
                    myHttpService.findBackPassword(email, userame);
                }
            }
        });
    }
}
