package com.example.fruit.salerapplication;

import android.content.Intent;
import android.content.res.AssetManager;
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

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by fruit on 2017/7/12.
 */

public class Register extends AppCompatActivity {
    Button btnReturn, btnRegister, btnSendCode;
    private MyHttpService myHttpService;
    boolean isUsername = false;
    boolean isRealName = false;
    boolean isPhone = false;
    boolean isEmail = false;
    boolean isCode = false;
    boolean isPassword = false;
    boolean isPasswordConfirm = false;


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
                    Toast.makeText(Register.this, "用户名不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    if (!temp.matches(regex)) {
                        Toast.makeText(Register.this, "用户名只能包含大小写字母与数字", Toast.LENGTH_SHORT).show();
                    } else {
                        if (temp.length() > 20) {
                            Toast.makeText(Register.this, "用户名输入过长", Toast.LENGTH_SHORT).show();
                        } else {
                            isUsername = true;
                        }
                    }
                }
            }
        }

    };

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
                    Toast.makeText(Register.this, "真实姓名不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    if (!temp.matches(regex)) {
                        Toast.makeText(Register.this, "真实姓名只能包含汉字", Toast.LENGTH_SHORT).show();
                    } else {
                        if (temp.length() > 20) {
                            Toast.makeText(Register.this, "真实姓名输入过长", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(Register.this, "手机号码不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    if (!temp.matches(regex)) {
                        Toast.makeText(Register.this, "手机格式错误", Toast.LENGTH_SHORT).show();
                    } else {
                        isPhone = true;
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
                    Toast.makeText(Register.this, "邮箱不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    if (!temp.matches(regex)) {
                        Toast.makeText(Register.this, "邮箱格式错误", Toast.LENGTH_SHORT).show();
                    } else {
                        isEmail = true;
                    }
                }
            }
        }

    };

    View.OnFocusChangeListener codeListener = new View.OnFocusChangeListener() {
        private String temp;
        String regex = "^[0-9a-zA-z][0-9a-zA-z][0-9a-zA-z][0-9a-zA-z]$";


        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            EditText _v = (EditText) v;
            temp = _v.getText().toString();
            if (!hasFocus) {
                isCode = false;
                if (temp.isEmpty()) {
                    Toast.makeText(Register.this, "验证码不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    if (!temp.matches(regex)) {
                        Toast.makeText(Register.this, "验证码格式错误", Toast.LENGTH_SHORT).show();
                    } else {
                        isCode = true;
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
                    Toast.makeText(Register.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    if (!temp.matches(regex)) {
                        Toast.makeText(Register.this, "密码只能包含大小写字母与数字", Toast.LENGTH_SHORT).show();
                    } else {
                        if (temp.length() > 20) {
                            Toast.makeText(Register.this, "密码输入过长", Toast.LENGTH_SHORT).show();
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
                String temp2 = ((TextView) findViewById(R.id.register_password)).getText().toString();
                if (!temp.equals(temp2)) {
                    Toast.makeText(Register.this, "两次密码输入不一致", Toast.LENGTH_SHORT).show();
                } else {
                    isPasswordConfirm = true;
                }
            }
        }

    };

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Intent intent = null;
            switch (msg.what) {
                case RequestType.USER_REGISTER:
                    if (msg.getData().getBoolean("success")) {
                        intent = new Intent(Register.this, Login.class);
                        Register.this.startActivity(intent);
                        Register.this.finish();
                        Toast.makeText(Register.this, "注册成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(Register.this, msg.getData().getString("message"), Toast.LENGTH_SHORT).show();
                    }
                    break;
                case RequestType.USER_REGISTER_VERIFY:
                    if (msg.getData().getBoolean("success")) {
                        Toast.makeText(Register.this, "验证码已发送", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(Register.this, "验证码已发送", Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:
                    Toast.makeText(Register.this, "未知请求类型", Toast.LENGTH_SHORT).show();
                    break;
                //TODO: other type of requst
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();

        initView();
        bindView();
    }

    public void initView() {
        btnReturn = (Button) findViewById(R.id.btn_header_left);
        btnRegister = (Button) findViewById(R.id.btn_register);
        btnSendCode = (Button) findViewById(R.id.btn_send_code);
        findViewById(R.id.btn_header_right).setVisibility(View.INVISIBLE);
        ((TextView) findViewById(R.id.text_title)).setText("注册");

        ((TextView) findViewById(R.id.register_name)).setOnFocusChangeListener(userNameListener);
        ((TextView) findViewById(R.id.register_realname)).setOnFocusChangeListener(realNameListener);
        ((TextView) findViewById(R.id.register_phone)).setOnFocusChangeListener(phoneListener);
        ((TextView) findViewById(R.id.register_email)).setOnFocusChangeListener(emailListener);
        ((TextView) findViewById(R.id.register_verify_code)).setOnFocusChangeListener(codeListener);
        ((TextView) findViewById(R.id.register_password)).setOnFocusChangeListener(passwordListener);
        ((TextView) findViewById(R.id.register_password_confirm)).setOnFocusChangeListener(passwordConfirmListener);
    }

    public void bindView() {
        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Register.this.finish();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((TextView) findViewById(R.id.register_name)).clearFocus();
                ((TextView) findViewById(R.id.register_realname)).clearFocus();
                ((TextView) findViewById(R.id.register_phone)).clearFocus();
                ((TextView) findViewById(R.id.register_email)).clearFocus();
                ((TextView) findViewById(R.id.register_verify_code)).clearFocus();
                ((TextView) findViewById(R.id.register_password)).clearFocus();
                ((TextView) findViewById(R.id.register_password_confirm)).clearFocus();


                boolean isCorrect = isCode && isUsername && isPassword && isPasswordConfirm && isEmail && isPhone && isRealName;
                if (!isCorrect) {
                    Toast.makeText(Register.this, "请检查您的格式", Toast.LENGTH_SHORT).show();
                } else {
                    String registerName = ((TextView) findViewById(R.id.register_name)).getText().toString();
                    String registerRealName = ((TextView) findViewById(R.id.register_realname)).getText().toString();
                    String registerPhone = ((TextView) findViewById(R.id.register_phone)).getText().toString();
                    String registaerEmail = ((TextView) findViewById(R.id.register_email)).getText().toString();
                    String registerVerifyCode = ((TextView) findViewById(R.id.register_verify_code)).getText().toString();
                    String registerPassword = ((TextView) findViewById(R.id.register_password)).getText().toString();

                    myHttpService.register(registerName, registerPassword, registerPhone, registaerEmail, registerRealName, "saler", registerVerifyCode);
                }
            }
        });

        btnSendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isEmail) {
                    Toast.makeText(Register.this, "邮箱格式有问题", Toast.LENGTH_SHORT).show();
                }
                else {
                    String email = ((TextView) findViewById(R.id.register_email)).getText().toString();
                    myHttpService.verifyEmail(email);
                }
            }
        });
    }


    private void init() {
        Map<String, String> serverConfigMap = ConfigUtil.loadServerConfig(ConfigUtil.SERVER_CONFIG_FILENAME, getAssets());
        if (serverConfigMap != null) {
            myHttpService = new MyHttpService(handler, serverConfigMap);
        }
    }

}
