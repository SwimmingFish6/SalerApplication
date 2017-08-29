package com.example.fruit.salerapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fruit.salerapplication.commontool.ConfigUtil;
import com.example.fruit.salerapplication.commontool.FruitDB;
import com.example.fruit.salerapplication.testhttpapi.service.MyHttpService;
import com.example.fruit.salerapplication.testhttpapi.service.constants.RequestType;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by fruit on 2017/7/12.
 */

public class Order extends AppCompatActivity {
    Button btnReturn, btnAccept, btnScan;
    ListView listView;
    ArrayList<HashMap<String, String>> listItems = new ArrayList<HashMap<String, String>>();
    String oid, buyer, date, address, price, status, priceString, typeString, amountString, typeIdString;
    ArrayList<String> priceList, typeList, amountList;
    FruitDB db;
    MyHttpService myHttpService;
    String token;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Intent intent = null;
            switch (msg.what) {
                case RequestType.SALER_ORDERS_ACCEPT:
                    if(msg.getData().getBoolean("success")) {
                        Toast.makeText(Order.this, "接收订单成功！", Toast.LENGTH_SHORT).show();
                        btnAccept.setVisibility(View.INVISIBLE);
                        findViewById(R.id.btn_header_right).setVisibility(View.VISIBLE);
                    }
                    else{
                        Toast.makeText(Order.this, "接收订单失败！", Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:
                    break;
                //TODO: other type of requst
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        Intent intent = getIntent();

        oid = intent.getStringExtra("oid");
        buyer = intent.getStringExtra("buyer");
        date = intent.getStringExtra("date");
        address = intent.getStringExtra("address");
        price = intent.getStringExtra("price");
        status = intent.getStringExtra("status");
        priceString = intent.getStringExtra("priceList");
        typeString = intent.getStringExtra("typeList");
        amountString = intent.getStringExtra("amountList");
        typeIdString = intent.getStringExtra("typeIdList");

        String typeTemp[] = typeString.split(",");
        typeList = new ArrayList<String>();
        for (int i = 0; i < typeTemp.length; i++)
            typeList.add(typeTemp[i]);

        typeTemp = priceString.split(",");
        priceList = new ArrayList<String>();
        for (int i = 0; i < typeTemp.length; i++)
            priceList.add(typeTemp[i]);

        typeTemp = amountString.split(",");
        amountList = new ArrayList<String>();
        for (int i = 0; i < typeTemp.length; i++)
            amountList.add(typeTemp[i]);

        init();
        initView();
        initData();
        bindView();
    }

    private void init() {
        db = new  FruitDB(getApplicationContext());
        HashMap<String, String> config = (HashMap<String, String>) ConfigUtil.loadServerConfig(ConfigUtil.SERVER_CONFIG_FILENAME, getAssets());
        myHttpService = new MyHttpService(handler, config);
        token = db.querySettingValue("token");
    }

    public void initView () {
        btnReturn = (Button) findViewById(R.id.btn_header_left);
        btnAccept = (Button) findViewById(R.id.btn_order_accept);

        ((TextView) findViewById(R.id.text_title)).setText("订单详情");
        listView = (ListView) findViewById(R.id.fruit_management_list);

        ((TextView) findViewById(R.id.order_detail_id)).setText(oid);
        ((TextView) findViewById(R.id.order_detail_buyer)).setText(buyer);
        ((TextView) findViewById(R.id.order_detail_date)).setText(date);
        ((TextView) findViewById(R.id.order_detail_address)).setText(address);
        ((TextView) findViewById(R.id.order_detail_price)).setText(price);

        if (!status.equals("NOTACCEPTED")) {
            findViewById(R.id.btn_order_accept).setVisibility(View.INVISIBLE);
            //findViewById(R.id.btn_order_reject).setVisibility(View.INVISIBLE);
            findViewById(R.id.border2).setVisibility(View.INVISIBLE);
        }

        if (!status.equals("ACCEPTED_NOTPACKED")) {
            findViewById(R.id.btn_header_right).setVisibility(View.INVISIBLE);
        }
        else {
            btnScan = ((Button) findViewById(R.id.btn_header_right));
            btnScan.setText("出库");
        }
    }

    public void initData () {
        for (int i = 0 ; i < typeList.size(); i++) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("name", typeList.get(i));
            map.put("price", priceList.get(i));
            map.put("amount", amountList.get(i));
            listItems.add(map);
        }
    }

    public void bindView () {
        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                intent.putExtra("updateSignal", true);
                setResult(0, intent);
                Order.this.finish();
            }
        });

        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myHttpService.acceptOrder(token, oid);
            }
        });

        final OrderAdapter orderAdapter = new OrderAdapter(this, listItems);
        listView.setAdapter(orderAdapter);

        if (btnScan != null)
            btnScan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent;
                    intent = new Intent(Order.this, ScanOut.class);
                    intent.putExtra("oid", oid);
                    intent.putExtra("typeList", typeString);
                    intent.putExtra("amountList", amountString);
                    intent.putExtra("typeIdList", typeIdString);
                    startActivityForResult(intent, 0);
                }
            });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 0 && data.getBooleanExtra("scanFinished", false)){
            findViewById(R.id.btn_header_right).setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onBackPressed () {
        Intent intent = getIntent();
        intent.putExtra("updateSignal", true);
        setResult(0, intent);
        Order.this.finish();
    }

    private class OrderAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private ArrayList<HashMap<String, String>> listData;

        public OrderAdapter (Context context,
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
            ((TextView) convertView.findViewById(R.id.basic_listitem_right)).setText(listData.get(position).get("amount") + " * ￥" + listData.get(position).get("price"));

            return convertView;
        }
    }
}
