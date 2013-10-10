package com.mattkula.hackmitapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class PebbleConnectionReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        final String pebbleAddress = intent.getStringExtra("address");
        Log.v("DEBUG", String.format("Pebble (%s) connected bitch", pebbleAddress));
        Toast.makeText(context, String.format("Pebble (%s) connected bitch", pebbleAddress), Toast.LENGTH_LONG).show();
    }
}
