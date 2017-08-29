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
import com.example.fruit.salerapplication.testhttpapi.bean.StoreBean;
import com.example.fruit.salerapplication.testhttpapi.service.MyHttpService;
import com.example.fruit.salerapplication.testhttpapi.service.constants.RequestType;

import java.util.HashMap;

/**
 * Created by fruit on 2017/7/11.
 */

public class SetBankAccount extends AppCompatActivity {
    Button btnReturn, btnBind;
    FruitDB db;
    MyHttpService myHttpService;
    String token;
    boolean isCardId = false, isPassword = false;

    View.OnFocusChangeListener cardIdListener = new View.OnFocusChangeListener() {
        private String temp;
        private String regex = "^[0-9]+$";

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            EditText _v = (EditText) v;
            temp = _v.getText().toString();
            if (!hasFocus) {
                isCardId = false;
                if (temp.isEmpty()) {
                    Toast.makeText(SetBankAccount.this, "卡号不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    if (!temp.matches(regex)) {
                        Toast.makeText(SetBankAccount.this, "卡号只能包含数字", Toast.LENGTH_SHORT).show();
                    } else {
                        if (temp.length() == 17) {
                            Toast.makeText(SetBankAccount.this, "卡号长度需要符合规则", Toast.LENGTH_SHORT).show();
                        } else {
                            isCardId = true;
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
                    Toast.makeText(SetBankAccount.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    if (!temp.matches(regex)) {
                        Toast.makeText(SetBankAccount.this, "密码只能包含大小写字母与数字", Toast.LENGTH_SHORT).show();
                    } else {
                        if (temp.length() > 20) {
                            Toast.makeText(SetBankAccount.this, "密码输入过长", Toast.LENGTH_SHORT).show();
                        } else {
                            isPassword = true;
                        }
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
                case RequestType.USER_GET_BANKCARDINFO:
                    String cardId = msg.getData().getString("cardId");
                    ((TextView) findViewById(R.id.setting_bank_account_number)).setText(cardId);
                    break;
                case RequestType.USER_BIND_BANKCARD:
                    if(msg.getData().getBoolean("success")) {
                        Toast.makeText(SetBankAccount.this, "绑定银行卡成功", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(SetBankAccount.this, "修改商店信息失败", Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:
                    Toast.makeText(SetBankAccount.this, "未知请求类型", Toast.LENGTH_SHORT).show();
                    break;
                //TODO: other type of requst
            }
            super.handleMessage(msg);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_account);

        init();
        initView();
        bindView();
    }

    private void init() {
        db = new  FruitDB(getApplicationContext());
        HashMap<String, String> config = (HashMap<String, String>) ConfigUtil.loadServerConfig(ConfigUtil.SERVER_CONFIG_FILENAME, getAssets());
        myHttpService = new MyHttpService(handler, config);
        token = db.querySettingValue("token");
    }

    public void initView () {
        myHttpService.getBankAccountInfo(token);

        btnReturn = (Button) findViewById(R.id.btn_header_left);
        btnBind = (Button) findViewById(R.id.btn_bank_account_bind);
        findViewById(R.id.btn_header_right).setVisibility(View.INVISIBLE);
        ((TextView) findViewById(R.id.text_title)).setText("绑定银行卡");
        ((TextView) findViewById(R.id.setting_bank_account_number)).setOnFocusChangeListener(cardIdListener);
        ((TextView) findViewById(R.id.setting_bank_account_password)).setOnFocusChangeListener(passwordListener);

    }

    public void bindView () {
        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetBankAccount.this.finish();
            }
        });

        btnBind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((TextView) findViewById(R.id.setting_bank_account_number)).clearFocus();
                ((TextView) findViewById(R.id.setting_bank_account_password)).clearFocus();

                boolean isCorrect = isCardId && isPassword;

                if(isCorrect) {
                    String cardID = ((TextView) findViewById(R.id.setting_bank_account_number)).getText().toString();
                    String cardPwd = ((TextView) findViewById(R.id.setting_bank_account_password)).getText().toString();

                    myHttpService.bindBankAccount(token, cardID, cardPwd);
                }
                else{
                    Toast.makeText(SetBankAccount.this, "请检查输入格式", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
