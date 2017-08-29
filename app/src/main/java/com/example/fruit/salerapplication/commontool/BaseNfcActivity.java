package com.example.fruit.salerapplication.commontool;

import android.app.Activity;

/**
 * Created by fruit on 2017/7/14.
 */

import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by luxuhui on 2017/7/9.
 */

public class BaseNfcActivity extends AppCompatActivity {
    private NfcAdapter baseNfcAdapter;
    private PendingIntent basePendingIntent;

    @Override
    protected void onStart() {
        super.onStart();
        baseNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        basePendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()), PendingIntent.FLAG_CANCEL_CURRENT);
    }


    @Override
    public void onResume() {
        super.onResume();

        if (baseNfcAdapter != null)
            baseNfcAdapter.enableForegroundDispatch(this, basePendingIntent, null, null);

    }

    @Override
    public void onPause() {
        super.onPause();
        if (baseNfcAdapter != null)
            baseNfcAdapter.disableForegroundDispatch(this);
    }
}
