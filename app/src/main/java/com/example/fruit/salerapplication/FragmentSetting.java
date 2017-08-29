package com.example.fruit.salerapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.example.fruit.salerapplication.commontool.FruitDB;

/**
 * Created by fruit on 2017/7/9.
 */

public class FragmentSetting extends Fragment {
    private View viewStoreInfo, viewBankAccount, viewAccountManagement;
    private Switch switchAutoTake, switchMessageReminder;
    private FruitDB db;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting,container,false);

        init(view);

        initView(view);
        bindView();

        return view;
    }

    private void init(View view) {
        db = new FruitDB(getContext());
        boolean autotake = Boolean.valueOf(db.querySettingValue("autotake")).booleanValue();
        boolean autoremind = Boolean.valueOf(db.querySettingValue("autoremind")).booleanValue();

        ((Switch) view.findViewById(R.id.setting_message_reminder_switch)).setChecked(autoremind);
        ((Switch) view.findViewById(R.id.setting_auto_take_switch)).setChecked(autotake);
    }

    public void initView (View view) {
        viewStoreInfo = view.findViewById(R.id.setting_store_info_view);
        viewBankAccount = view.findViewById(R.id.setting_bank_account_view);
        viewAccountManagement = view.findViewById(R.id.setting_account_management_view);
        switchAutoTake = (Switch) view.findViewById(R.id.setting_auto_take_switch);
        switchMessageReminder = (Switch) view.findViewById(R.id.setting_message_reminder_switch);
    }

    public void bindView () {
        viewStoreInfo.setOnClickListener(new SettingClickListener());
        viewBankAccount.setOnClickListener(new SettingClickListener());
        viewAccountManagement.setOnClickListener(new SettingClickListener());
        switchAutoTake.setOnCheckedChangeListener(new SettingCheckedChangeListener());
        switchMessageReminder.setOnCheckedChangeListener(new SettingCheckedChangeListener());
    }

    private class SettingClickListener implements View.OnClickListener {
        Intent intent;
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.setting_store_info_view:
                    intent = new Intent(getActivity(), SetStoreInfo.class);
                    getActivity().startActivity(intent);
                    break;
                case R.id.setting_bank_account_view:
                    intent = new Intent(getActivity(), SetBankAccount.class);
                    getActivity().startActivity(intent);
                    break;
                case R.id.setting_account_management_view:
                    intent = new Intent(getActivity(), SetUserAccount.class);
                    getActivity().startActivity(intent);
                    break;
            }
        }
    }

    private class SettingCheckedChangeListener implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            switch (buttonView.getId()) {
                case R.id.setting_auto_take_switch:
                    db.deleteSetting("autotake");
                    db.insertSetting("autotake", String.valueOf(isChecked));
                    break;
                case R.id.setting_message_reminder_switch:
                    db.deleteSetting("autoremind");
                    db.insertSetting("autoremind", String.valueOf(isChecked));
                    break;
            }
        }
    }
}
