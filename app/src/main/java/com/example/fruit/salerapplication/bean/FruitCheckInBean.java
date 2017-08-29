package com.example.fruit.salerapplication.bean;

/**
 * Created by fruit on 2017/7/14.
 */

public class FruitCheckInBean {
    int typeid;
    int amount;

    public FruitCheckInBean(int typeid, int amount){
        this.typeid = typeid;
        this.amount = amount;
    }

    public int getTypeid() {
        return typeid;
    }

    public void setTypeid(int typeid) {
        this.typeid = typeid;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
