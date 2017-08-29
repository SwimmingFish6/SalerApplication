package com.example.fruit.salerapplication.commontool;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;

import com.example.fruit.salerapplication.MainActivity;
import com.example.fruit.salerapplication.R;
import com.example.fruit.salerapplication.testhttpapi.bean.OrderBean;
import com.example.fruit.salerapplication.testhttpapi.bean.StoreReverseBean;
import com.example.fruit.salerapplication.testhttpapi.service.MyHttpService;
import com.example.fruit.salerapplication.testhttpapi.service.constants.RequestType;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by luxuhui on 2017/7/15.
 */

public class SalerService extends Service {
    MyHttpService myHttpService;
    FruitDB db;
    String token;
    int orderCount;
    ServiceThread serviceThread;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Intent intent = null;
            boolean autoRemind = Boolean.valueOf(db.querySettingValue("autoremind")).booleanValue();
            boolean autoTake = Boolean.valueOf(db.querySettingValue("autotake")).booleanValue();

            switch (msg.what) {
                case RequestType.USER_GET_ORDERS:
                    ArrayList<OrderBean> orders = (ArrayList<OrderBean>) msg.getData().get("orders");

                    if (autoRemind && orderCount != orders.size()) {
                        orderCount = orders.size();
                        showNotification();
                    }

                    if (autoTake) {
                        for (OrderBean each:orders) {
                            if (each.getStatus().equals("NOTACCEPTED")) {
                                myHttpService.acceptOrder(token, each.getOrderId() + "");
                            }
                        }
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
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        init();
        serviceThread = new ServiceThread();
        serviceThread.start();

        return super.onStartCommand(intent, flags, startId);
    }

    private void init() {
        db = FruitDB.getInstance(this);
        HashMap<String, String> config = (HashMap<String, String>) ConfigUtil.loadServerConfig(ConfigUtil.SERVER_CONFIG_FILENAME, getAssets());
        myHttpService = new MyHttpService(handler, config);

        token = db.querySettingValue("token");
        myHttpService.getOrders(token);
    }

    private void showNotification () {
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentTitle("你有一条新的订单")
                .setContentText("请转到水果管家中进行处理")
                .setContentIntent(pendingIntent)
                .setTicker("新的订单")
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setOngoing(false)
                .setDefaults(Notification.DEFAULT_ALL);

        manager.notify(0, mBuilder.build());
    }

    private class ServiceThread extends Thread {
        @Override
        public void run() {
            try {
                Thread.sleep(30000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            myHttpService.getOrders(token);
        }
    }
}
