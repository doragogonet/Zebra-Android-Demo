package com.zebra.demo.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.zebra.demo.R;
import com.zebra.demo.base.Constants;
import com.zebra.demo.bean.HistoryData;
import com.zebra.demo.tools.StringUtils;
import com.zebra.demo.view.BarChartView;


public class DetailActivity extends BaseActivity {

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Intent intent = getIntent();
        String receivedData = intent.getStringExtra(Constants.HISTORY_DATA_KEY);
        HistoryData history = JSON.parseObject(receivedData, HistoryData.class);

        //タグID（Hex）
        TextView tvTagId = (TextView) findViewById(R.id.TvDetailTagId);
        tvTagId.setText("タグID（Hex）：" + history.getTagID());

        //TID
      //  TextView tvTid = (TextView) findViewById(R.id.TvDetailTagTid);
       // tvTid.setText("TID：" + history.getTID());

        //EPC
       // TextView tvEpc = (TextView) findViewById(R.id.TvDetailTagEpc);
       // tvEpc.setText("EPC：" + history.getMemoryBankData());

        //USER
       // TextView tvUser = (TextView) findViewById(R.id.TvDetailTagUser);
       // tvUser.setText("USER：" + history.getUser());

        TextView tvMemory = (TextView) findViewById(R.id.TvDetailTagMemoryBank);
        tvMemory.setText("MEMORY BANK：" + super.getActionLabel(history.getMemoryBankValue()));

        TextView tvTime = (TextView) findViewById(R.id.TvDetailTagCurrentTime);
        tvTime.setText("TIME：" + history.getTagCurrentTime());

       // TextView tvAntennaId = (TextView) findViewById(R.id.TvDetailTagAntennaId);
      //  tvAntennaId.setText("ANTENNA ID：" + history.getAntennaID());

        TextView tvCrc = (TextView) findViewById(R.id.TvDetailTagPc);
        tvCrc.setText("PC：" + String.valueOf(history.getPC()));

        TextView tvSeenCount = (TextView) findViewById(R.id.TvDetailTagSeenCount);
        tvSeenCount.setText("SEEN：" + history.getTagSeenCount());

        int rssi = 100;
        String rssiStr = "0";
        if(StringUtils.isNotEmpty(history.getPeakRSSI())) {
            rssi = ((int) Float.parseFloat(history.getPeakRSSI()) * -1);
            rssiStr = history.getPeakRSSI();
        }
        BarChartView tvTagRssi = (BarChartView) findViewById(R.id.TvInventoryTagRssi);
        tvTagRssi.setData((100 - rssi) * 8, rssiStr);

    }
}