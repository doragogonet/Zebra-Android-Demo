package com.zebra.demo.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.content.Intent;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import com.zebra.demo.R;

import java.util.HashMap;

public class DetailActivity extends FragmentActivity {

    public static final int VISIBLE = 0;        
    public static final int INVISIBILITY = 4;   
    public static final int GONE = 8;           
    public static final String LABEL_NAME1 = "";
    public static final String LABEL_NAME2 = "";

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        HashMap map = (HashMap) intent.getSerializableExtra("map");

        //タグID（Hex）
        TextView tvTagId = (TextView) findViewById(R.id.TvDetailTagId);
        tvTagId.setText("タグID（Hex）：" + String.valueOf(map.get(InventoryHistoryActivity.TAG_ID)));

        //TID
        TextView tvTid = (TextView) findViewById(R.id.TvDetailTagTid);
        tvTid.setText("TID：" + String.valueOf(map.get(InventoryHistoryActivity.TAG_TID)));

        //EPC
        TextView tvEpc = (TextView) findViewById(R.id.TvDetailTagEpc);
        tvEpc.setText("EPC：" + String.valueOf(map.get(InventoryHistoryActivity.TAG_EPC)));

        //USER
        TextView tvUser = (TextView) findViewById(R.id.TvDetailTagUser);
        tvUser.setText("USER：" + String.valueOf(map.get(InventoryHistoryActivity.TAG_USER)));

        TextView tvMemory = (TextView) findViewById(R.id.TvDetailTagMemoryBank);
        tvMemory.setText("MEMORY BANK：" + String.valueOf(map.get(InventoryHistoryActivity.TAG_MEMORY_BANK)));

        TextView tvAntennaId = (TextView) findViewById(R.id.TvDetailTagAntennaId);
        tvAntennaId.setText("ANTENNA ID：" + String.valueOf(map.get(InventoryHistoryActivity.TAG_ANTENNA_ID)));

        TextView tvCrc = (TextView) findViewById(R.id.TvDetailTagCrc);
        tvCrc.setText("CRC：" + String.valueOf(map.get(InventoryHistoryActivity.TAG_CRC)));

        TextView tvEventTime = (TextView) findViewById(R.id.TvDetailTagEventTime);
        tvEventTime.setText("EVENT TIME：" + String.valueOf(map.get(InventoryHistoryActivity.TAG_EVENT_TIME)));

    }
}