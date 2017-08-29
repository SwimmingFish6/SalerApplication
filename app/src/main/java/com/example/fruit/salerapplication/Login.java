package com.example.fruit.salerapplication;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fruit.salerapplication.bean.SystemSettingBean;
import com.example.fruit.salerapplication.commontool.ConfigUtil;
import com.example.fruit.salerapplication.commontool.FruitDB;
import com.example.fruit.salerapplication.commontool.FruitTypeInfo;
import com.example.fruit.salerapplication.commontool.MacInfo;
import com.example.fruit.salerapplication.testhttpapi.bean.FruitTypeBean;
import com.example.fruit.salerapplication.testhttpapi.service.MyHttpService;
import com.example.fruit.salerapplication.testhttpapi.service.constants.RequestType;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class Login extends AppCompatActivity {
    private Button btnLogin, btnForgetPassword, btnRegister;
    private MyHttpService myHttpService;
    FruitDB db;
    boolean isUsername = false;
    long[] drawList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


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
                    Toast.makeText(Login.this, "用户名不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    if (!temp.matches(regex)) {
                        Toast.makeText(Login.this, "用户名只能包含大小写字母与数字", Toast.LENGTH_SHORT).show();
                    } else {
                        if (temp.length() > 20) {
                            Toast.makeText(Login.this, "用户名输入过长", Toast.LENGTH_SHORT).show();
                        } else {
                            isUsername = true;
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
                case RequestType.USER_LOGIN:
                    if (msg.getData().getBoolean("success")) {
                        intent = new Intent(Login.this, MainActivity.class);
                        String token = msg.getData().getString("token");
                        db.insertSetting("token", token);
                        db.insertSetting("autotake", "false");
                        db.insertSetting("autoremind", "false");
                        Login.this.startActivity(intent);
                        Toast.makeText(Login.this, "登录成功", Toast.LENGTH_SHORT).show();
                        Login.this.finish();
                    }
                    else {
                        Toast.makeText(Login.this, msg.getData().getString("message"), Toast.LENGTH_SHORT).show();
                    }

                    break;
                case RequestType.GET_FRUIT_TYPES:
                    ArrayList<FruitTypeBean> result = (ArrayList<FruitTypeBean>) msg.getData().get("types");

                    HashMap<Long, String> nameMap = new HashMap<Long, String>();
                    for (int i = 0; i < result.size(); i++) {
                        nameMap.put(result.get(i).getTypeId(),
                                result.get(i).getTypeName() + result.get(i).getClassName());
                    }

                    HashMap<Long, Long> picMap = new HashMap<Long, Long>();
                    for(int i=1; i<=41;i++){
                        picMap.put((long) i+10000, drawList[i]);
                    }

                    FruitTypeInfo.initialize(nameMap, picMap);

                    if(checkForSetting()){
                        Intent intent2 = new Intent(Login.this, MainActivity.class);
                        Login.this.startActivity(intent2);
                        Login.this.finish();
                    }
                    break;
                default:
                    Toast.makeText(Login.this, "未知请求类型", Toast.LENGTH_SHORT).show();
                    break;
                //TODO: other type of requst
            }
            super.handleMessage(msg);
        }
    };

    public void initView() {
        findViewById(R.id.btn_header_left).setVisibility(View.INVISIBLE);
        findViewById(R.id.btn_header_right).setVisibility(View.INVISIBLE);
        ((TextView) findViewById(R.id.text_title)).setText("修鲜水果管家");
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnForgetPassword = (Button) findViewById(R.id.btn_forget_password);
        btnRegister = (Button) findViewById(R.id.btn_register);
    }

    public void bindView() {
        ((TextView) findViewById(R.id.input_username)).setOnFocusChangeListener(userNameListener);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = ((TextView) findViewById(R.id.input_username)).getText().toString();
                String password = ((TextView) findViewById(R.id.input_password)).getText().toString();
                String mac_id = MacInfo.getMachineHardwareAddress();

                myHttpService.login("saler", username, password, mac_id);
            }
        });
        btnForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, ForgetPassword.class);
                Login.this.startActivity(intent);
            }
        });
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, Register.class);
                Login.this.startActivity(intent);
            }
        });
    }

    private void init(){
        drawList = new long[42];

        drawList[0] = 0;
        drawList[1] = (long) R.drawable.a10001;
        drawList[2] = (long) R.drawable.a10002;
        drawList[3] = (long) R.drawable.a10003;
        drawList[4] = (long) R.drawable.a10004;
        drawList[5] = (long) R.drawable.a10005;
        drawList[6] = (long) R.drawable.a10006;
        drawList[7] = (long) R.drawable.a10007;
        drawList[8] = (long) R.drawable.a10008;
        drawList[9] = (long) R.drawable.a10009;
        drawList[10] = (long) R.drawable.a10010;
        drawList[11] = (long) R.drawable.a10011;
        drawList[12] = (long) R.drawable.a10012;
        drawList[13] = (long) R.drawable.a10013;
        drawList[14] = (long) R.drawable.a10014;
        drawList[15] = (long) R.drawable.a10015;
        drawList[16] = (long) R.drawable.a10016;
        drawList[17] = (long) R.drawable.a10017;
        drawList[18] = (long) R.drawable.a10018;
        drawList[19] = (long) R.drawable.a10019;
        drawList[20] = (long) R.drawable.a10020;
        drawList[21] = (long) R.drawable.a10021;
        drawList[22] = (long) R.drawable.a10022;
        drawList[23] = (long) R.drawable.a10023;
        drawList[24] = (long) R.drawable.a10024;
        drawList[25] = (long) R.drawable.a10025;
        drawList[26] = (long) R.drawable.a10026;
        drawList[27] = (long) R.drawable.a10027;
        drawList[28] = (long) R.drawable.a10028;
        drawList[29] = (long) R.drawable.a10029;
        drawList[30] = (long) R.drawable.a10030;
        drawList[31] = (long) R.drawable.a10031;
        drawList[32] = (long) R.drawable.a10032;
        drawList[33] = (long) R.drawable.a10033;
        drawList[34] = (long) R.drawable.a10034;
        drawList[35] = (long) R.drawable.a10035;
        drawList[36] = (long) R.drawable.a10036;
        drawList[37] = (long) R.drawable.a10037;
        drawList[38] = (long) R.drawable.a10038;
        drawList[39] = (long) R.drawable.a10039;
        drawList[40] = (long) R.drawable.a10040;
        drawList[41] = (long) R.drawable.a10041;


        db = FruitDB.getInstance(getApplicationContext());
        db.createPreferenceTable();
        db.createStorageTable();
        db.createSysytemSettingTable();
        initHttpConnect();

        myHttpService.getFruitTypes();
    }

    private void initHttpConnect() {
        Map<String, String> serverConfigMap = ConfigUtil.loadServerConfig(ConfigUtil.SERVER_CONFIG_FILENAME, getAssets());
        if (serverConfigMap != null) {
            myHttpService = new MyHttpService(handler, serverConfigMap);
        }
    }



    private boolean checkForSetting(){
        List<SystemSettingBean> settings = db.querySetting();
        for(SystemSettingBean setting : settings){
            if (setting.getKey().equals("token")) {
                return true;
            }
        }
        return false;
    }
}
