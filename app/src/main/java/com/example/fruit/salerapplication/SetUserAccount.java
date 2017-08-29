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

import com.example.fruit.salerapplication.bean.SystemSettingBean;
import com.example.fruit.salerapplication.commontool.ConfigUtil;
import com.example.fruit.salerapplication.commontool.FruitDB;
import com.example.fruit.salerapplication.testhttpapi.bean.UserBean;
import com.example.fruit.salerapplication.testhttpapi.service.MyHttpService;
import com.example.fruit.salerapplication.testhttpapi.service.constants.RequestType;

import org.w3c.dom.Text;

import java.util.HashMap;

/**
 * Created by fruit on 2017/7/11.
 */

public class SetUserAccount extends AppCompatActivity {
    Button btnReturn, btnChangePassword, btnSave, btnLogout;
    FruitDB db;
    MyHttpService myHttpService;
    String token;
    boolean isRealName = false, isPhone = false;


    View.OnFocusChangeListener realNameListener = new View.OnFocusChangeListener() {
        private String temp;
        String regex = "^[\u4e00-\u9fa5]+$";

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            EditText _v = (EditText) v;
            temp = _v.getText().toString();
            if (!hasFocus) {
                isRealName = false;
                if (temp.isEmpty()) {
                    Toast.makeText(SetUserAccount.this, "真实姓名不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    if (!temp.matches(regex)) {
                        Toast.makeText(SetUserAccount.this, "真实姓名只能包含汉字", Toast.LENGTH_SHORT).show();
                    } else {
                        if (temp.length() > 20) {
                            Toast.makeText(SetUserAccount.this, "真实姓名输入过长", Toast.LENGTH_SHORT).show();
                        } else {
                            isRealName = true;
                        }
                    }
                }
            }
        }

    };

    View.OnFocusChangeListener phoneListener = new View.OnFocusChangeListener() {
        private String temp;
        String regex = "^((13[0-9])|(15[^4])|(18[0,2,3,5-9])|(17[0-8])|(147))\\d{8}$";

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            EditText _v = (EditText) v;
            temp = _v.getText().toString();
            if (!hasFocus) {
                isPhone = false;
                if (temp.isEmpty()) {
                    Toast.makeText(SetUserAccount.this, "手机号码不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    if (!temp.matches(regex)) {
                        Toast.makeText(SetUserAccount.this, "手机格式错误", Toast.LENGTH_SHORT).show();
                    } else {
                        isPhone = true;
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
                case RequestType.USER_GET_INFO:
                    UserBean data = (UserBean) msg.getData().get("userInfo");
                    ((TextView) findViewById(R.id.setting_account_management_name)).setText(data.getUsername());
                    ((TextView) findViewById(R.id.setting_account_management_realname)).setText(data.getRealname());
                    ((TextView) findViewById(R.id.setting_account_management_phone)).setText(data.getPhone());
                    ((TextView) findViewById(R.id.setting_account_management_email)).setText(data.getEmail());
                    break;
                case RequestType.USER_LOGOUT:
                    intent = new Intent(SetUserAccount.this, Login.class);
                    if(msg.getData().getBoolean("success")) {
                        db.deleteSetting("token");
                        db.deleteSetting("autotake");
                        db.deleteSetting("autoremind");
                        Toast.makeText(SetUserAccount.this, "退出登录成功", Toast.LENGTH_SHORT).show();
                        SetUserAccount.this.startActivity(intent);
                        SetUserAccount.this.finish();
                    }
                    else{
                        Toast.makeText(SetUserAccount.this, "退出登录失败", Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:
                    Toast.makeText(SetUserAccount.this, "未知请求类型", Toast.LENGTH_SHORT).show();
                    break;
                //TODO: other type of requst
            }
            super.handleMessage(msg);
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_management);

        init();
        initView();
        bindView();
    }

    private void init() {
        db = FruitDB.getInstance(getApplicationContext());
        HashMap<String, String> config = (HashMap<String, String>) ConfigUtil.loadServerConfig(ConfigUtil.SERVER_CONFIG_FILENAME, getAssets());
        myHttpService = new MyHttpService(handler, config);

        token = db.querySettingValue("token");
    }

    public void initView () {
        myHttpService.getUserInfo(token);

        btnReturn = (Button) findViewById(R.id.btn_header_left);
        btnLogout = (Button) findViewById(R.id.setting_account_management_logout);
        btnSave = (Button) findViewById(R.id.setting_account_management_save);
        btnChangePassword = (Button) findViewById(R.id.setting_account_management_change_password);
        findViewById(R.id.btn_header_right).setVisibility(View.INVISIBLE);

        ((TextView) findViewById(R.id.text_title)).setText("账户管理");

        ((TextView) findViewById(R.id.setting_account_management_realname)).setOnFocusChangeListener(realNameListener);
        ((TextView) findViewById(R.id.setting_account_management_phone)).setOnFocusChangeListener(phoneListener);
    }

    public void bindView () {
        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetUserAccount.this.finish();
            }
        });
        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SetUserAccount.this, ChangePassword.class);
                SetUserAccount.this.startActivity(intent);
            }
        });
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SetUserAccount.this, Login.class);
                myHttpService.logout(token);
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((TextView) findViewById(R.id.setting_account_management_phone)).clearFocus();
                ((TextView) findViewById(R.id.setting_account_management_realname)).clearFocus();

                boolean isCorrect = isPhone && isRealName;

                if(isCorrect){
                    String newPhone = ((TextView) findViewById(R.id.setting_account_management_phone)).getText().toString();
                    String newRealName = ((TextView) findViewById(R.id.setting_account_management_realname)).getText().toString();

                }
            }
        });
    }
}
