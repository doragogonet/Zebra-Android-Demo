package com.example.ebskk.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AutoStartReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //开机启动
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Intent thisIntent = new Intent(context, UHFMainActivity.class);//设置要启动的app
//            thisIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            thisIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(thisIntent);

        }
    }
}
