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
        tvTagId.setText("タグID（Hex）：" + map.get(InventoryHistoryActivity.TAG_ID).toString());

        //パスワード（Hex）
        TextView tvPasswrod = (TextView) findViewById(R.id.TvDetailTagPassword);
        tvPasswrod.setText("パスワード（Hex）：" + map.get(InventoryHistoryActivity.TAG_PASSWORD).toString());

        //TID
        TextView tvTid = (TextView) findViewById(R.id.TvDetailTagTid);
        tvTid.setText("TID：" + map.get(InventoryHistoryActivity.TAG_TID).toString());

        //RESERVED
        TextView tvReserved = (TextView) findViewById(R.id.TvDetailTagReserved);
        tvReserved.setText("RESERVED：" + map.get(InventoryHistoryActivity.TAG_RESERVED).toString());

        //EPC
        TextView tvEpc = (TextView) findViewById(R.id.TvDetailTagEpc);
        tvEpc.setText("EPC：" + map.get(InventoryHistoryActivity.TAG_EPC).toString());

        //USER
        TextView tvUser = (TextView) findViewById(R.id.TvDetailTagUser);
        tvUser.setText("USER：" + map.get(InventoryHistoryActivity.TAG_USER).toString());

    }
}