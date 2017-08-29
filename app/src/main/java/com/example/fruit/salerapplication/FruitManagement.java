package com.example.fruit.salerapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewDebug;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fruit.salerapplication.bean.FruitCheckInBean;
import com.example.fruit.salerapplication.commontool.ConfigUtil;
import com.example.fruit.salerapplication.commontool.FruitDB;
import com.example.fruit.salerapplication.commontool.FruitTypeInfo;
import com.example.fruit.salerapplication.testhttpapi.service.MyHttpService;
import com.example.fruit.salerapplication.testhttpapi.service.constants.RequestType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by fruit on 2017/7/12.
 */

public class FruitManagement extends AppCompatActivity {
    private Spinner spinnerType;
    //private List<String> listType = new ArrayList<String>();
    Button btnReturn, btnSave;
    String token;
    FruitDB db;
    MyHttpService myHttpService;
    String typeId;
    Boolean priceChangedFlag = false;
    boolean isPrice = false;
    //private long typeIdList[];

    View.OnFocusChangeListener priceListener = new View.OnFocusChangeListener() {
        private String temp;
        private String regex_1 = "^[0-9]+$";
        private String regex_2 = "^[0-9]+[.][0-9]+$";

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            EditText _v = (EditText) v;
            temp = _v.getText().toString();
            if (!hasFocus) {
                isPrice = false;
                if (temp.isEmpty()) {
                    Toast.makeText(FruitManagement.this, "价格不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    if (!temp.matches(regex_1) && !temp.matches(regex_2)) {
                        Toast.makeText(FruitManagement.this, "价格必须为数字", Toast.LENGTH_SHORT).show();
                    } else {
                        if (temp.length() > 6) {
                            Toast.makeText(FruitManagement.this, "价格输入过长", Toast.LENGTH_SHORT).show();
                        } else {
                                isPrice = true;
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
                case RequestType.SALER_STORE_MODIFY_PRICE:
                    if (msg.getData().getBoolean("success")) {
                        Toast.makeText(FruitManagement.this, "价格修改成功！", Toast.LENGTH_SHORT).show();
                        priceChangedFlag = true;
                    }
                    else{
                        Toast.makeText(FruitManagement.this, msg.getData().getString("message"), Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:
                    Toast.makeText(FruitManagement.this, "未知请求类型", Toast.LENGTH_SHORT).show();
                    break;
                //TODO: other type of requst
            }
            super.handleMessage(msg);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fruit_management);

        init();
        initView();
        bindView();
    }

    public void initView () {
        //btnDelete = (Button) findViewById(R.id.btn_delete);
        btnSave = (Button) findViewById(R.id.btn_save);
        btnReturn = (Button) findViewById(R.id.btn_header_left);
        findViewById(R.id.btn_header_right).setVisibility(View.INVISIBLE);
        ((TextView) findViewById(R.id.text_title)).setText("水果管理");

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String price = intent.getStringExtra("price");
        typeId = intent.getStringExtra("typeId");

        ((TextView) findViewById(R.id.fruit_management_type)).setText(name);
        ((EditText) findViewById(R.id.fruit_management_price)).setText(price);

        ((TextView) findViewById(R.id.fruit_management_price)).setOnFocusChangeListener(priceListener);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((TextView) findViewById(R.id.fruit_management_price)).clearFocus();

                if(isPrice) {
                    String price = ((TextView) findViewById(R.id.fruit_management_price)).getText().toString();

                    myHttpService.modifyStoreGoodsPrice(token, typeId, price);
                }
                else {
                    Toast.makeText(FruitManagement.this, "请检查输入价格格式", Toast.LENGTH_SHORT).show();
                }
            }
        });
        /*
        spinnerType = (Spinner) findViewById(R.id.fruit_management_spinner_type);

        typeIdList = new long[FruitTypeInfo.fruitTypeNameMap.size()];
        Iterator iter = FruitTypeInfo.fruitTypeNameMap.entrySet().iterator();
        int i = 0;
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            typeIdList[i] = (long) entry.getKey();
            i++;
            listType.add(entry.getValue().toString());
        }

        ArrayAdapter adapterType = new ArrayAdapter<String>(this, R.layout.fruit_type_spinneritem, listType);
        adapterType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerType.setAdapter(adapterType);
        */
    }

    private void init() {
        db = new FruitDB(getApplicationContext());
        HashMap<String, String> config = (HashMap<String, String>) ConfigUtil.loadServerConfig(ConfigUtil.SERVER_CONFIG_FILENAME, getAssets());
        myHttpService = new MyHttpService(handler, config);
        token = db.querySettingValue("token");
    }


    public void bindView () {
        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                intent.putExtra("priceChangedFlag", priceChangedFlag);
                setResult(0, intent);
                FruitManagement.this.finish();
            }
        });
    }

    @Override
    public void onBackPressed () {
        Intent intent = getIntent();
        intent.putExtra("priceChangedFlag", priceChangedFlag);
        setResult(0, intent);
        FruitManagement.this.finish();
    }
}
