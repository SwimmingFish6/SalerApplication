package com.example.fruit.salerapplication;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fruit.salerapplication.commontool.ConfigUtil;
import com.example.fruit.salerapplication.commontool.FruitDB;
import com.example.fruit.salerapplication.commontool.FruitTypeInfo;
import com.example.fruit.salerapplication.testhttpapi.bean.StoreReverseBean;
import com.example.fruit.salerapplication.testhttpapi.service.MyHttpService;
import com.example.fruit.salerapplication.testhttpapi.service.constants.RequestType;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by fruit on 2017/7/9.
 */

public class FragmentFruit extends Fragment {
    ListView listView;
    ArrayList<HashMap<String, Object>> listItems;
    //FloatingActionButton btnAdd;
    MyHttpService myHttpService;
    FruitDB db;
    String token;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Intent intent = null;
            switch (msg.what) {
                case RequestType.SALER_STORE_GET_REVERSES:
                    ArrayList<StoreReverseBean> reverse = (ArrayList<StoreReverseBean>) msg.getData().get("reverses");

                    initData(reverse);
                    initListView();

                    break;
                case RequestType.USER_BIND_BANKCARD:
                    if(msg.getData().getBoolean("success")) {
                        Toast.makeText(getActivity(), "绑定银行卡成功", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(getActivity(), "修改商店信息失败", Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:
                    Toast.makeText(getActivity(), "未知请求类型", Toast.LENGTH_SHORT).show();
                    break;
                //TODO: other type of requst
            }
            super.handleMessage(msg);
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fruit, null);

        //StrictModeUtil.closeStrictMode();
        init();
        initView(view);
        //initListView();

        return view;
    }

    public void initView (View view) {
        myHttpService.getStoreReverses(token);

        listView = (ListView)view.findViewById(R.id.fragment_fruit_listview);
        //btnAdd = (FloatingActionButton)view.findViewById(R.id.fragment_fruit_add_btn);
    }

    private void init() {
        db = FruitDB.getInstance(getContext());
        HashMap<String, String> config = (HashMap<String, String>) ConfigUtil.loadServerConfig(ConfigUtil.SERVER_CONFIG_FILENAME, getActivity().getResources().getAssets());
        myHttpService = new MyHttpService(handler, config);
        token = db.querySettingValue("token");
    }

    public void initData (ArrayList<StoreReverseBean> bean) {
        listItems = new ArrayList<HashMap<String, Object>>();

        for (StoreReverseBean each:bean) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            //map.put("img", R.drawable.watermelon);
            map.put("img", FruitTypeInfo.getPictureId(each.getTypeId()));
            map.put("typeId", each.getTypeId());
            map.put("name", FruitTypeInfo.getFruitTypeName(each.getTypeId()));
            if(each.getPrice() == -1.0 ) {
                map.put("price", String.format("待定价"));
            }
            else {
                map.put("price", String.format("%.2f", each.getPrice()));
            }
            map.put("reserve", String.format("%d", each.getNum()));
            listItems.add(map);
        }
    }

    public void initListView () {
        FruitAdapter fruitAdapter = new FruitAdapter(getActivity(), listItems);
        listView.setAdapter(fruitAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent;
                intent = new Intent(getActivity(), FruitManagement.class);
                intent.putExtra("name", listItems.get(position).get("name").toString());
                intent.putExtra("price", listItems.get(position).get("price").toString());
                intent.putExtra("typeId", listItems.get(position).get("typeId").toString());
                startActivityForResult(intent, 0);
            }
        });

        /*
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(getActivity(), FruitManagement.class);
                intent.putExtra("source", "add");
                getActivity().startActivity(intent);
            }
        });
        */
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 0 && data.getBooleanExtra("priceChangedFlag", false)){
            myHttpService.getStoreReverses(token);
        }
    }

    private class FruitAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private ArrayList<HashMap<String, Object>> listData;

        public FruitAdapter (Context context,
                             ArrayList<HashMap<String, Object>> listData) {
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
                convertView = inflater.inflate(R.layout.fragment_fruit_listitem, null);

            //(ImageView)convertView.findViewById(R.id.fragment_fruit_listitem_img).set
            //Bitmap bitmap = BitmapFactory.decodeStream(getClass().getResourceAsStream("res/drawable/" + listData.get(position).get("imgName").toString()));
            //((ImageView) convertView.findViewById(R.id.fragment_fruit_listitem_img)).setImageBitmap(bitmap);
            //((ImageView) convertView.findViewById(R.id.fragment_fruit_listitem_img)).setImageBitmap(getRes(listData.get(position).get("imgName").toString()));
            /*
            try {
                InputStream is = getActivity().getResources().getAssets().open(listData.get(position).get("imgName").toString());
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                ((ImageView) convertView.findViewById(R.id.fragment_fruit_listitem_img)).setImageBitmap(bitmap);
            }catch (IOException e){
                e.printStackTrace();
            }*/

            //((ImageView) convertView.findViewById(R.id.fragment_fruit_listitem_img)).setImageDrawable(Integer.parseInt(String.valueOf(listData.get(position).get("img"))));
            ((ImageView) convertView.findViewById(R.id.fragment_fruit_listitem_img)).setImageResource(Integer.parseInt(String.valueOf(listData.get(position).get("img"))));
            ((TextView) convertView.findViewById(R.id.fragment_fruit_listitem_name)).setText(listData.get(position).get("name").toString());
            ((TextView) convertView.findViewById(R.id.fragment_fruit_listitem_price)).setText("价格：" + listData.get(position).get("price").toString());
            ((TextView) convertView.findViewById(R.id.fragment_fruit_listitem_reserve)).setText("库存：" + listData.get(position).get("reserve").toString());

            return convertView;
        }

        public void add(HashMap<String, Object> map) {
            if (listData == null)
                listData = new ArrayList<HashMap<String, Object>>();
            listData.add(map);
            notifyDataSetChanged();
        }

        public void delete (int position) {
            if (getItem(position) != null)
                listData.remove(position);
            notifyDataSetChanged();
        }
    }
}
