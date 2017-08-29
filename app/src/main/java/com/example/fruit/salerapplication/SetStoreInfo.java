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
import com.example.fruit.salerapplication.testhttpapi.bean.UserBean;
import com.example.fruit.salerapplication.testhttpapi.service.MyHttpService;
import com.example.fruit.salerapplication.testhttpapi.service.constants.RequestType;

import java.util.HashMap;

/**
 * Created by fruit on 2017/7/11.
 */

public class SetStoreInfo extends AppCompatActivity {
    Button btnReturn, btnSave;
    MyHttpService myHttpService;
    FruitDB db;
    String token;
    boolean isPhone = false, isChargeMan = false, isAddress = false, isStoreName = false, isDesc = false;

    View.OnFocusChangeListener chargeManListener = new View.OnFocusChangeListener() {
        private String temp;
        String regex = "^[\u4e00-\u9fa5]+$";

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            EditText _v = (EditText) v;
            temp = _v.getText().toString();
            if (!hasFocus) {
                isChargeMan = false;
                if (temp.isEmpty()) {
                    Toast.makeText(SetStoreInfo.this, "负责人姓名不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    if (!temp.matches(regex)) {
                        Toast.makeText(SetStoreInfo.this, "负责人姓名只能包含汉字", Toast.LENGTH_SHORT).show();
                    } else {
                        if (temp.length() > 20) {
                            Toast.makeText(SetStoreInfo.this, "负责人姓名输入过长", Toast.LENGTH_SHORT).show();
                        } else {
                            isChargeMan = true;
                        }
                    }
                }
            }
        }

    };


    View.OnFocusChangeListener storeNameListener = new View.OnFocusChangeListener() {
        private String temp;

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            EditText _v = (EditText) v;
            temp = _v.getText().toString();
            if (!hasFocus) {
                isStoreName = false;
                if (temp.isEmpty()) {
                    Toast.makeText(SetStoreInfo.this, "商店名不能为空", Toast.LENGTH_SHORT).show();
                } else {
                        if (temp.length() > 20) {
                            Toast.makeText(SetStoreInfo.this, "商店名输入过长", Toast.LENGTH_SHORT).show();
                        } else {
                            isStoreName = true;
                        }
                }
            }
        }

    };

    View.OnFocusChangeListener addressListener = new View.OnFocusChangeListener() {
        private String temp;

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            EditText _v = (EditText) v;
            temp = _v.getText().toString();
            if (!hasFocus) {
                isAddress = false;
                if (temp.isEmpty()) {
                    Toast.makeText(SetStoreInfo.this, "商店地址不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    if (temp.length() > 50) {
                        Toast.makeText(SetStoreInfo.this, "商店地址输入过长", Toast.LENGTH_SHORT).show();
                    } else {
                        isAddress = true;
                    }
                }
            }
        }

    };

    View.OnFocusChangeListener descListener = new View.OnFocusChangeListener() {
        private String temp;

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            EditText _v = (EditText) v;
            temp = _v.getText().toString();
            if (!hasFocus) {
                isDesc = false;
                if (temp.isEmpty()) {
                    Toast.makeText(SetStoreInfo.this, "商店描述不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    if (temp.length() > 100) {
                        Toast.makeText(SetStoreInfo.this, "商店描述输入过长", Toast.LENGTH_SHORT).show();
                    } else {
                        isDesc = true;
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
                    Toast.makeText(SetStoreInfo.this, "手机号码不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    if (!temp.matches(regex)) {
                        Toast.makeText(SetStoreInfo.this, "手机格式错误", Toast.LENGTH_SHORT).show();
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
                case RequestType.SALER_GET_STOREINFO:
                    if(msg.getData().getBoolean("success")) {
                        StoreBean data = (StoreBean) msg.getData().get("store");
                        ((TextView) findViewById(R.id.setting_store_info_name)).setText(data.getStorename());
                        ((TextView) findViewById(R.id.setting_store_info_address)).setText(data.getAddress());
                        ((TextView) findViewById(R.id.setting_store_info_chargeman)).setText(data.getChargeman());
                        ((TextView) findViewById(R.id.setting_store_info_phone)).setText(data.getPhone());
                        ((TextView) findViewById(R.id.setting_store_info_description)).setText(data.getDescription());
                    }
                    else {
                        Toast.makeText(SetStoreInfo.this, "未绑定商店信息", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case RequestType.SALER_BIND_STORE:
                    if (msg.getData().getBoolean("success")) {
                        Toast.makeText(SetStoreInfo.this, "修改商店信息成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(SetStoreInfo.this, "修改商店信息失败", Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:
                    Toast.makeText(SetStoreInfo.this, "未知请求类型", Toast.LENGTH_SHORT).show();
                    break;
                //TODO: other type of requst
            }
            super.handleMessage(msg);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_info);

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

    public void initView() {
        myHttpService.getStoreInfo(token);

        btnReturn = (Button) findViewById(R.id.btn_header_left);
        btnSave = (Button) findViewById(R.id.btn_store_info_save);
        findViewById(R.id.btn_header_right).setVisibility(View.INVISIBLE);
        ((TextView) findViewById(R.id.text_title)).setText("设置商店信息");

        ((TextView) findViewById(R.id.setting_store_info_name)).setOnFocusChangeListener(storeNameListener);
        ((TextView) findViewById(R.id.setting_store_info_address)).setOnFocusChangeListener(addressListener);
        ((TextView) findViewById(R.id.setting_store_info_phone)).setOnFocusChangeListener(phoneListener);
        ((TextView) findViewById(R.id.setting_store_info_description)).setOnFocusChangeListener(descListener);
        ((TextView) findViewById(R.id.setting_store_info_chargeman)).setOnFocusChangeListener(chargeManListener);
    }

    public void bindView() {
        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetStoreInfo.this.finish();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((TextView) findViewById(R.id.setting_store_info_name)).clearFocus();
                ((TextView) findViewById(R.id.setting_store_info_address)).clearFocus();
                ((TextView) findViewById(R.id.setting_store_info_phone)).clearFocus();
                ((TextView) findViewById(R.id.setting_store_info_description)).clearFocus();
                ((TextView) findViewById(R.id.setting_store_info_chargeman)).clearFocus();

                boolean isCorrect = isAddress && isChargeMan && isStoreName && isDesc && isPhone;

                if (isCorrect) {
                    String storeName = ((TextView) findViewById(R.id.setting_store_info_name)).getText().toString();
                    String storeAddress = ((TextView) findViewById(R.id.setting_store_info_address)).getText().toString();
                    String storePhone = ((TextView) findViewById(R.id.setting_store_info_phone)).getText().toString();
                    String storeDescription = ((TextView) findViewById(R.id.setting_store_info_description)).getText().toString();
                    String storeChargeman = ((TextView) findViewById(R.id.setting_store_info_chargeman)).getText().toString();

                    myHttpService.bindSalerStore(token, storeAddress, storeName, storeChargeman, storePhone, storeDescription);
                }
                else{
                    Toast.makeText(SetStoreInfo.this, "请检查商店信息格式", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
