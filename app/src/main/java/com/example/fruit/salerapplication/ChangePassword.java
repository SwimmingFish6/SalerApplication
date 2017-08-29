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
import com.example.fruit.salerapplication.commontool.FruitDB;
import com.example.fruit.salerapplication.testhttpapi.service.MyHttpService;
import com.example.fruit.salerapplication.testhttpapi.service.constants.RequestType;

import java.util.HashMap;

/**
 * Created by fruit on 2017/7/12.
 */

public class ChangePassword extends AppCompatActivity {
    Button btnReturn, btnSubmit;
    boolean isPassword = false, isPasswordConfirm = false, isOldPassword = false;
    FruitDB db;
    MyHttpService myHttpService;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Intent intent = null;
            switch (msg.what) {
                case RequestType.USER_MODIFY_PASSWORD:
                    intent = new Intent(ChangePassword.this, SetUserAccount.class);
                    if(msg.getData().getBoolean("success")) {
                        Toast.makeText(ChangePassword.this, "修改密码成功", Toast.LENGTH_SHORT).show();
                        //ChangePassword.this.startActivity(intent);
                        ChangePassword.this.finish();
                    }
                    else{
                        Toast.makeText(ChangePassword.this, msg.getData().getString("message"), Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:
                    Toast.makeText(ChangePassword.this, "未知请求类型", Toast.LENGTH_SHORT).show();
                    break;
                //TODO: other type of requst
            }
            super.handleMessage(msg);
        }
    };

    View.OnFocusChangeListener oldPasswordListener = new View.OnFocusChangeListener() {
        private String temp;
        private String regex = "^[a-z0-9A-Z]+$";

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            EditText _v = (EditText) v;
            temp = _v.getText().toString();
            if (!hasFocus) {
                isOldPassword = false;
                if (temp.isEmpty()) {
                    Toast.makeText(ChangePassword.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    if (!temp.matches(regex)) {
                        Toast.makeText(ChangePassword.this, "密码只能包含大小写字母与数字", Toast.LENGTH_SHORT).show();
                    } else {
                        if (temp.length() > 20) {
                            Toast.makeText(ChangePassword.this, "密码输入过长", Toast.LENGTH_SHORT).show();
                        } else {
                            isOldPassword = true;
                        }
                    }
                }
            }
        }
    };


    View.OnFocusChangeListener passwordListener = new View.OnFocusChangeListener() {
        private String temp;
        private String regex = "^[a-z0-9A-Z]+$";

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            EditText _v = (EditText) v;
            temp = _v.getText().toString();
            if (!hasFocus) {
                isPassword = false;
                if (temp.isEmpty()) {
                    Toast.makeText(ChangePassword.this, "新密码不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    if (!temp.matches(regex)) {
                        Toast.makeText(ChangePassword.this, "新密码只能包含大小写字母与数字", Toast.LENGTH_SHORT).show();
                    } else {
                        if (temp.length() > 20) {
                            Toast.makeText(ChangePassword.this, "新密码输入过长", Toast.LENGTH_SHORT).show();
                        } else {
                            isPassword = true;
                        }
                    }
                }
            }
        }

    };

    View.OnFocusChangeListener passwordConfirmListener = new View.OnFocusChangeListener() {
        private String temp;


        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            EditText _v = (EditText) v;
            temp = _v.getText().toString();
            if (!hasFocus) {
                isPasswordConfirm = false;
                String temp2 = ((TextView) findViewById(R.id.change_password_new)).getText().toString();
                if (!temp.equals(temp2)) {
                    Toast.makeText(ChangePassword.this, "两次密码输入不一致", Toast.LENGTH_SHORT).show();
                } else {
                    isPasswordConfirm = true;
                }
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        init();
        initView();
        bindView();
    }

    public void initView() {
        btnReturn = (Button) findViewById(R.id.btn_header_left);
        btnSubmit = (Button) findViewById(R.id.btn_modify_submit);
        ((TextView) findViewById(R.id.change_password_old)).setOnFocusChangeListener(oldPasswordListener);
        ((TextView) findViewById(R.id.change_password_new)).setOnFocusChangeListener(passwordListener);
        ((TextView) findViewById(R.id.change_password_new_confirm)).setOnFocusChangeListener(passwordConfirmListener);
        findViewById(R.id.btn_header_right).setVisibility(View.INVISIBLE);
        ((TextView) findViewById(R.id.text_title)).setText("修改密码");
    }

    public void bindView() {
        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ChangePassword.this.finish();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((TextView) findViewById(R.id.change_password_new)).clearFocus();
                ((TextView) findViewById(R.id.change_password_new_confirm)).clearFocus();
                ((TextView) findViewById(R.id.change_password_old)).clearFocus();

                boolean isCorrect = isPassword && isPasswordConfirm && isOldPassword;
                if(!isCorrect){
                    Toast.makeText(ChangePassword.this, "请检查密码", Toast.LENGTH_SHORT).show();
                }
                else {

                    String newPassword = ((TextView) findViewById(R.id.change_password_new)).getText().toString();
                    String oldPassword = ((TextView) findViewById(R.id.change_password_old)).getText().toString();
                    String token = db.querySettingValue("token");

                    myHttpService.modifyPassword(token, oldPassword, newPassword);
                }
            }
        });
    }

    private void init() {
        db = FruitDB.getInstance(getApplicationContext());
        HashMap<String, String> config = (HashMap<String, String>) ConfigUtil.loadServerConfig(ConfigUtil.SERVER_CONFIG_FILENAME, getAssets());
        myHttpService = new MyHttpService(handler, config);

    }
}
