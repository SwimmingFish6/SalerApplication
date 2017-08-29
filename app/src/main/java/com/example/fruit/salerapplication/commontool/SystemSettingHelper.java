package com.example.fruit.salerapplication.commontool;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by luxuhui on 2017/7/13.
 */

public class SystemSettingHelper extends SQLiteOpenHelper {
    private static final int dbVersion = 1;
    private static final String dbName = "fruit.db";
    private static final String tableName = "SystemSetting";

    public SystemSettingHelper(Context context){
        super(context, dbName, null, dbVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        //TODO delete this sql
//        boolean flag = true;
//        if(flag){
//            String drop_table = "DROP TABLE IF EXISTS "+ tableName;
//            db.execSQL(drop_table);
//        }
        String sql = "CREATE TABLE IF NOT EXISTS " + tableName + " (key text primary key, value text)";
        Log.i("DB write", sql);
        db.execSQL(sql);
        Log.i("DB write", "end");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        String sql = "DROP TABLE IF EXISTS " + tableName;
        db.execSQL(sql);
        onCreate(db);
    }

    public static String getTableName(){
        return tableName;
    }
}
