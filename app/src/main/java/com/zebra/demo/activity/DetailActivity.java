package com.zebra.demo.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.content.Intent;
import android.widget.TextView;


import com.alibaba.fastjson.JSON;
import com.zebra.demo.R;
import com.zebra.demo.base.Constants;
import com.zebra.demo.bean.HistoryData;


public class DetailActivity extends BaseActivity {

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Intent intent = getIntent();
        String receivedData = intent.getStringExtra(Constants.HISTORY_DATA_KEY);
        HistoryData history = JSON.parseObject(receivedData, HistoryData.class);

        TextView textView = findViewById(R.id.textViewdata);

        final StringBuilder sb = new StringBuilder();

        sb.append("タグID(Hex)：" + history.getTagID() + "\n");
        sb.append("PC(Hex)：" + String.valueOf(history.getPC()) + "\n");
        sb.append("SEEN：" + history.getTagSeenCount() + "\n");
        sb.append("RSSI：" + history.getPeakRSSI() + "\n");

        textView.append(sb.toString());

    }
}