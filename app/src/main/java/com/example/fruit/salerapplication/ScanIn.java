package com.example.fruit.salerapplication;

import android.content.Context;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fruit.salerapplication.bean.FruitCheckInBean;
import com.example.fruit.salerapplication.commontool.BaseNfcActivity;
import com.example.fruit.salerapplication.commontool.ConfigUtil;
import com.example.fruit.salerapplication.commontool.FruitDB;
import com.example.fruit.salerapplication.commontool.FruitTypeInfo;
import com.example.fruit.salerapplication.testhttpapi.bean.StoreBean;
import com.example.fruit.salerapplication.testhttpapi.bean.UserBean;
import com.example.fruit.salerapplication.testhttpapi.service.MyHttpService;
import com.example.fruit.salerapplication.testhttpapi.service.constants.RequestType;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by fruit on 2017/7/12.
 */

public class ScanIn extends BaseNfcActivity {
    private ImageView imgRotate;
    private Button btnReturn;
    private String nfcTagText;
    MyHttpService myHttpService;
    String token;
    String storeId, salerId;
    FruitDB db;
    ArrayList<FruitCheckInBean> fruitCheckInBeanArrayList;
    ArrayList<HashMap<String, String>> listItems;
    private ListView scanListView;


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Intent intent = null;
            switch (msg.what) {
                case RequestType.SALER_GET_STOREINFO:
                    StoreBean bean = (StoreBean) msg.getData().get("store");
                    storeId = String.valueOf(bean.getStoreId());
                    break;
                case RequestType.USER_GET_INFO:
                    UserBean data = (UserBean) msg.getData().get("userInfo");
                    salerId = String.valueOf(data.getUid());
                    break;
                case RequestType.SALER_STORE_ADD_REVERSES:
                    if (msg.getData().getBoolean("success")) {
                        Toast.makeText(ScanIn.this, "入库成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ScanIn.this, "入库失败", Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:
                    Toast.makeText(ScanIn.this, "未知请求类型", Toast.LENGTH_SHORT).show();
                    break;
                //TODO: other type of requst
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanin);

        init();
        initView();
        bindView();
    }

    public void initView() {
        btnReturn = (Button) findViewById(R.id.btn_header_left);
        findViewById(R.id.btn_header_right).setVisibility(View.INVISIBLE);
        ((TextView) findViewById(R.id.text_title)).setText("入库");

        imgRotate = (ImageView) findViewById(R.id.scan_rotate_image);
        Animation operatingAnim = AnimationUtils.loadAnimation(this, R.anim.rotate);
        LinearInterpolator lin = new LinearInterpolator();
        operatingAnim.setInterpolator(lin);
        imgRotate.startAnimation(operatingAnim);

        scanListView = (ListView) findViewById(R.id.scan_listview);
    }

    public void bindView() {
        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = "{'storeId':" + storeId + ",'fruits':[";
                for(FruitCheckInBean bean:fruitCheckInBeanArrayList){
                    data += "{'typeid':" + String.valueOf(bean.getTypeid()) + ",'amount':" + String.valueOf(bean.getAmount()) + "},";
                }
                data = data.substring(0, data.length()-1) + "]}";

                myHttpService.addStoreReverses(token, data);
                ScanIn.this.finish();
            }
        });
    }

    public void initData () {
        listItems = new ArrayList<HashMap<String, String>>();
        for (int i = 0; i < fruitCheckInBeanArrayList.size(); i++) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("name", FruitTypeInfo.getFruitTypeName((long)fruitCheckInBeanArrayList.get(i).getTypeid()));
            map.put("amount", String.format("%d", fruitCheckInBeanArrayList.get(i).getAmount()));
            listItems.add(map);
        }
    }

    public void initListView () {
        GoodsAdapter goodsAdapter = new GoodsAdapter(ScanIn.this, listItems);
        scanListView.setAdapter(goodsAdapter);
    }

    private class GoodsAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private ArrayList<HashMap<String, String>> listData;

        public GoodsAdapter (Context context,
                             ArrayList<HashMap<String, String>> listData) {
            this.inflater = LayoutInflater.from(context);
            this.listData = listData;
        }

        @Override
        public int getCount() {
            return listData.size();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public Object getItem(int position) {
            return listData.get(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null)
                convertView = inflater.inflate(R.layout.basic_listitem, null);

            ((TextView) convertView.findViewById(R.id.basic_listitem_left)).setText(listData.get(position).get("name"));
            ((TextView) convertView.findViewById(R.id.basic_listitem_right)).setText( listData.get(position).get("amount"));

            return convertView;
        }
    }


    @Override
    public void onNewIntent(Intent intent) {
        Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

        Ndef ndef = Ndef.get(detectedTag);
        int fsid, typeid;


        nfcTagText = "";
        readNfcTag(intent);

        JSONObject fruitInfo = null;
        try {
            fruitInfo = new JSONObject(nfcTagText);
            fsid = fruitInfo.getInt("fsid");
            typeid = fruitInfo.getInt("typeid");
            if (fruitInfo.has("isGetIn")) {
                Toast.makeText(this, "该水果已经入库", Toast.LENGTH_SHORT).show();
            } else {
                GetIn(typeid, 1);
                nfcTagText = nfcTagText.substring(0,nfcTagText.length()-1)+", 'isGetIn':'true', 'salerid':" + salerId.toString() +"}";
                NdefMessage ndefMessage = new NdefMessage(
                        new NdefRecord[]{createTextRecord(nfcTagText)});
                boolean result = writeTag(ndefMessage, detectedTag);
                if (result) {
                    Toast.makeText(this, "写入成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "写入失败", Toast.LENGTH_SHORT).show();
                }
            }

        } catch (JSONException e) {
            Toast.makeText(this, "芯片数据格式损坏", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        initData();
        initListView();
    }


    private void GetIn(int typeId, int amount) {
        boolean flag = false;

        for (FruitCheckInBean bean : fruitCheckInBeanArrayList) {
            if(bean.getTypeid() == typeId){
                flag = true;
                bean.setAmount(bean.getAmount()+amount);
                break;
            }
        }


        if(!flag){
            FruitCheckInBean bean = new FruitCheckInBean(typeId, amount);
            fruitCheckInBeanArrayList.add(bean);
        }
    }

    private void readNfcTag(Intent intent) {
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage msgs[] = null;
            int contentSize = 0;
            if (rawMsgs != null) {
                msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                    contentSize += msgs[i].toByteArray().length;
                }
            }

            try {
                if (msgs != null) {
                    NdefRecord record = msgs[0].getRecords()[0];
                    String textRecord = parseTextRecord(record);
                    nfcTagText += textRecord;
                }
            } catch (Exception e) {

            }
        }
    }

    public static String parseTextRecord(NdefRecord ndefRecord) {
        if (ndefRecord.getTnf() != NdefRecord.TNF_WELL_KNOWN) {
            return null;
        }
        if (!Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
            return null;
        }

        try {
            byte[] payload = ndefRecord.getPayload();

            String textEncoding = ((payload[0] & 0x80) == 0) ? "UTF-8" : "UTF-16";

            int languageCodeLength = payload[0] & 0x3f;

            String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");

            String textRecord = new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);

            return textRecord;
        } catch (Exception e) {
            throw new IllegalArgumentException();
        }
    }

    private void init() {
        fruitCheckInBeanArrayList = new ArrayList<FruitCheckInBean>();
        db = new FruitDB(getApplicationContext());
        HashMap<String, String> config = (HashMap<String, String>) ConfigUtil.loadServerConfig(ConfigUtil.SERVER_CONFIG_FILENAME, getAssets());
        myHttpService = new MyHttpService(handler, config);
        token = db.querySettingValue("token");
        myHttpService.getStoreInfo(token);
        myHttpService.getUserInfo(token);

    }

    /**
     * 创建NDEF文本数据
     *
     * @param text
     * @return
     */
    public static NdefRecord createTextRecord(String text) {
        byte[] langBytes = Locale.CHINA.getLanguage().getBytes(Charset.forName("US-ASCII"));
        Charset utfEncoding = Charset.forName("UTF-8");
        //将文本转换为UTF-8格式
        byte[] textBytes = text.getBytes(utfEncoding);
        //设置状态字节编码最高位数为0
        int utfBit = 0;
        //定义状态字节
        char status = (char) (utfBit + langBytes.length);
        byte[] data = new byte[1 + langBytes.length + textBytes.length];
        //设置第一个状态字节，先将状态码转换成字节
        data[0] = (byte) status;
        //设置语言编码，使用数组拷贝方法，从0开始拷贝到data中，拷贝到data的1到langBytes.length的位置
        System.arraycopy(langBytes, 0, data, 1, langBytes.length);
        //设置文本字节，使用数组拷贝方法，从0开始拷贝到data中，拷贝到data的1 + langBytes.length
        //到textBytes.length的位置
        System.arraycopy(textBytes, 0, data, 1 + langBytes.length, textBytes.length);
        //通过字节传入NdefRecord对象
        //NdefRecord.RTD_TEXT：传入类型 读写
        NdefRecord ndefRecord = new NdefRecord(NdefRecord.TNF_WELL_KNOWN,
                NdefRecord.RTD_TEXT, new byte[0], data);
        return ndefRecord;
    }

    /**
     * 写数据
     *
     * @param ndefMessage 创建好的NDEF文本数据
     * @param tag         标签
     * @return
     */
    public static boolean writeTag(NdefMessage ndefMessage, Tag tag) {
        try {
            Ndef ndef = Ndef.get(tag);
            ndef.connect();
            ndef.writeNdefMessage(ndefMessage);
            return true;
        } catch (Exception e) {
        }
        return false;
    }


    private static String bytesToString(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }

        StringBuilder buf = new StringBuilder();
        for (byte b : bytes) {
            buf.append(String.format("%02x:", b));
        }

        if (buf.length() > 0) {
            buf.deleteCharAt(buf.length() - 1);
        }

        return buf.toString();
    }

}
