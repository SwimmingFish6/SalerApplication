package com.example.fruit.salerapplication;

import android.content.Context;
import android.content.Intent;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fruit.salerapplication.commontool.ConfigUtil;
import com.example.fruit.salerapplication.commontool.FruitDB;
import com.example.fruit.salerapplication.testhttpapi.bean.OrderBean;
import com.example.fruit.salerapplication.testhttpapi.service.MyHttpService;
import com.example.fruit.salerapplication.testhttpapi.service.constants.RequestType;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by fruit on 2017/7/9.
 */

public class FragmentOrder extends Fragment {
    private ArrayList<HashMap<String, String>> statusList;
    private ArrayList<HashMap<String, String>> listUnaccepted;
    private ArrayList<HashMap<String, String>> listNotdelivered;
    private ArrayList<HashMap<String, String>> listNotreceived;
    private ArrayList<HashMap<String, String>> listFinished;
    private ArrayList<HashMap<String, String>> listData;
    ListView listView;
    private boolean fold[] = new boolean[4];
    MyHttpService myHttpService;
    FruitDB db;
    String token;

    private ArrayList<OrderBean> orders;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Intent intent = null;
            switch (msg.what) {
                case RequestType.USER_GET_ORDERS:
                    orders = (ArrayList<OrderBean>) msg.getData().get("orders");

                    initData(orders);
                    initListView();
                    break;
                default:
                    break;
                //TODO: other type of requst
            }
            super.handleMessage(msg);
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order,container,false);

        init();
        initView(view);
        //initData();
        //initListView();

        return view;
    }

    private void init() {
        db = FruitDB.getInstance(getActivity());
        HashMap<String, String> config = (HashMap<String, String>) ConfigUtil.loadServerConfig(ConfigUtil.SERVER_CONFIG_FILENAME, getActivity().getAssets());
        myHttpService = new MyHttpService(handler, config);

        token = db.querySettingValue("token");
        myHttpService.getOrders(token);
    }

    public void initData (ArrayList<OrderBean> orders) {
        statusList = new ArrayList<HashMap<String, String>>();
        listUnaccepted = new ArrayList<HashMap<String, String>>();
        listNotdelivered = new ArrayList<HashMap<String, String>>();
        listNotreceived = new ArrayList<HashMap<String, String>>();
        listFinished = new ArrayList<HashMap<String, String>>();
        listData = new ArrayList<HashMap<String, String>>();

        for (int i = 0; i < fold.length; i++)
            fold[i] = false;
        HashMap<String, String> map;

        map = new HashMap<String, String>();
        map.put("title", getActivity().getString(R.string.status_unaccepted));
        statusList.add(map);
        map = new HashMap<String, String>();
        map.put("title", getActivity().getString(R.string.status_notdelivered));
        statusList.add(map);
        map = new HashMap<String, String>();
        map.put("title", getActivity().getString(R.string.status_notreceived));
        statusList.add(map);
        map = new HashMap<String, String>();
        map.put("title", getActivity().getString(R.string.status_finished));
        statusList.add(map);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        for (int i = 0; i < orders.size(); i++) {
            map = new HashMap<String, String>();
            map.put("oid", "订单号：" + orders.get(i).getOrderId());
            map.put("buyer", orders.get(i).getBuyerName() + "");
            map.put("date", sdf.format(orders.get(i).getDate()));
            map.put("label", i + "");
            if (orders.get(i).getStatus().equals("NOTACCEPTED"))
                listUnaccepted.add(map);
            else if (orders.get(i).getStatus().equals("ACCEPTED_NOTPACKED"))
                listNotdelivered.add(map);
            else if (orders.get(i).getStatus().equals("ACCEPTED_PACKED_NOTRECEIVED"))
                listNotreceived.add(map);
            else listFinished.add(map);
        }

        listData.add(statusList.get(0));
        listData.addAll(listUnaccepted);
        listData.add(statusList.get(1));
        listData.addAll(listNotdelivered);
        listData.add(statusList.get(2));
        listData.addAll(listNotreceived);
        listData.add(statusList.get(3));
        listData.addAll(listFinished);
    }

    public void initView (View view) {
        listView = (ListView)view.findViewById(R.id.fragment_order_listview);
    }

    public void initListView () {
        final OrderAdapter orderAdapter = new OrderAdapter(getActivity(), listData, statusList);
        listView.setAdapter(orderAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int index = getIndexInStringArray(statusList, listData.get(position), "title");
                if (index == -1) {
                    Intent intent;
                    intent = new Intent(getActivity(), Order.class);
                    OrderBean order = orders.get(Integer.parseInt(listData.get(position).get("label")));
                    intent.putExtra("oid", order.getOrderId() + "");
                    intent.putExtra("buyer", order.getBuyerName());
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    intent.putExtra("date", sdf.format(order.getDate()));
                    intent.putExtra("address", order.getAddress());
                    intent.putExtra("price", String.format("%.2f", order.getPrice()));
                    intent.putExtra("status", order.getStatus());

                    String goodsPriceString = "";
                    String goodsNameString = "";
                    String goodsAmountString = "";
                    String goodsTypeIdString = "";
                    if (order.getGoods() != null && order.getGoods().size() != 0) {
                        for (int i = 0; i < order.getGoods().size() - 1; i++) {
                            goodsNameString += order.getGoods().get(i).getTypeName() + ",";
                            goodsPriceString += String.format("%.2f", order.getGoods().get(i).getPrice()) + ",";
                            goodsAmountString += String.format("%d", order.getGoods().get(i).getNumber()) + ",";
                            goodsTypeIdString += String.format("%d", order.getGoods().get(i).getTypeId()) + ",";
                        }
                        goodsNameString += order.getGoods().get(order.getGoods().size() - 1).getTypeName();
                        goodsPriceString += String.format("%.2f", order.getGoods().get(order.getGoods().size() - 1).getPrice());
                        goodsAmountString += String.format("%d", order.getGoods().get(order.getGoods().size() - 1).getNumber());
                        goodsTypeIdString += String.format("%d", order.getGoods().get(order.getGoods().size() - 1).getTypeId());

                        intent.putExtra("typeList", goodsNameString);
                        intent.putExtra("priceList", goodsPriceString);
                        intent.putExtra("amountList", goodsAmountString);
                        intent.putExtra("typeIdList", goodsTypeIdString);
                    }

                    startActivityForResult(intent, 0);
                }
                else {
                    int beginIndex;
                    switch (index) {
                        case 0:
                            beginIndex = index + 1;
                            if (fold[index]) {
                                orderAdapter.addMult(beginIndex, listUnaccepted);
                            }
                            else {
                                orderAdapter.deleteMult(beginIndex, listUnaccepted.size());
                            }
                            fold[index] = !fold[index];
                            break;
                        case 1:
                            beginIndex = index + 1 + (!fold[0] ? listUnaccepted.size() : 0);
                            if (fold[index]) {
                                orderAdapter.addMult(beginIndex, listNotdelivered);
                            }
                            else {
                                orderAdapter.deleteMult(beginIndex, listNotdelivered.size());
                            }
                            fold[index] = !fold[index];
                            break;
                        case 2:
                            beginIndex = index + 1 + (!fold[0] ? listUnaccepted.size() : 0) + (!fold[1] ? listNotdelivered.size() : 0);
                            if (fold[index]) {
                                orderAdapter.addMult(beginIndex, listNotreceived);
                            }
                            else {
                                orderAdapter.deleteMult(beginIndex, listNotreceived.size());
                            }
                            fold[index] = !fold[index];
                            break;
                        case 3:
                            beginIndex = index + 1 + (!fold[0] ? listUnaccepted.size() : 0) + (!fold[1] ? listNotdelivered.size() : 0) + (!fold[2] ? listNotreceived.size() : 0);
                            if (fold[index]) {
                                orderAdapter.addMult(beginIndex, listFinished);
                            }
                            else {
                                orderAdapter.deleteMult(beginIndex, listFinished.size());
                            }
                            fold[index] = !fold[index];
                            break;
                    }
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 0 && data.getBooleanExtra("updateSignal", false)){
            myHttpService.getOrders(token);
        }
    }

    private int getIndexInStringArray (ArrayList<HashMap<String, String>> list, HashMap<String, String> map, String key) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).get(key).equals(map.get(key))) {
                return i;
            }
        }
        return -1;
    }

    private class OrderAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private ArrayList<HashMap<String, String>> listHead;
        private ArrayList<HashMap<String, String>> listData;

        public OrderAdapter (Context context,
                             ArrayList<HashMap<String, String>> listData,
                             ArrayList<HashMap<String, String>> listHead) {
            this.inflater = LayoutInflater.from(context);
            this.listData = listData;
            this.listHead = listHead;
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

        public void add(int index, HashMap<String, String> map) {
        }

        public void delete (int position) {
        }

        public void addMult(int beginIndex, ArrayList<HashMap<String, String>> list) {
            if (listData == null)
                listData = new ArrayList<HashMap<String, String>>();
            listData.addAll(beginIndex, list);
            notifyDataSetChanged();
        }

        public void deleteMult(int beginIndex, int count) {
            if (listData != null && listData.get(beginIndex + count - 1) != null) {
                for (int i = 0; i < count; i++) {
                    listData.remove(beginIndex);
                }
            }
            notifyDataSetChanged();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (listHead.contains(listData.get(position))) {
                convertView = inflater.inflate(R.layout.fragment_order_listitem_head, null);
                ((TextView) convertView.findViewById(R.id.fragment_order_listitem_head)).setText(listData.get(position).get("title"));
            }
            else {
                convertView = inflater.inflate(R.layout.fragment_order_listitem, null);
                ((TextView) convertView.findViewById(R.id.fragment_order_listitem_oid)).setText(listData.get(position).get("oid"));
                ((TextView) convertView.findViewById(R.id.fragment_order_listitem_buyer)).setText("买家：" + listData.get(position).get("buyer"));
                ((TextView) convertView.findViewById(R.id.fragment_order_listitem_date)).setText("时间：" + listData.get(position).get("date"));
            }

            return convertView;
        }
    }
}
