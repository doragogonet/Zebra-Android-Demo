package com.zebra.demo.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.content.Intent;
import android.widget.TextView;


import com.alibaba.fastjson.JSON;
import com.zebra.demo.R;
import com.zebra.demo.base.Constants;
import com.zebra.demo.base.SettingsUtl;
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

        sb.append("EPC(Hex) = " + history.getTagID() + "\n");
        String pc =  hexToBinary( history.getPC(), 16);
        sb.append("PC(bits) = " + pc + "\n");
        sb.append("SEEN = " + history.getTagSeenCount() + "\n");
        sb.append("RSSI = " + history.getPeakRSSI() + "\n");

        textView.append(sb.toString());

    }
    public static String hexToBinary(String hex, int bitLength) {
        // 16進数文字列を整数に変換
        int decimalValue = Integer.parseInt(hex, 16);
        // 整数を2進数文字列に変換
        String binary = Integer.toBinaryString(decimalValue);
        // 必要に応じて先頭にゼロを追加または切り詰め
        if (binary.length() > bitLength) {
            // 長すぎる場合は右端を切り取る
            binary = binary.substring(binary.length() - bitLength);
        } else if (binary.length() < bitLength) {
            // 短い場合はゼロで埋める
            binary = "0".repeat(bitLength - binary.length()) + binary;
        }
        return binary;
    }
}