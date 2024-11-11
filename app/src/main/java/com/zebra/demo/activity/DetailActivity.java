package com.zebra.demo.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.content.Intent;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import com.zebra.demo.R;
import com.zebra.demo.base.Constants;
import com.zebra.demo.bean.HistoryData;

import java.util.HashMap;
import java.util.List;

public class DetailActivity extends BaseActivity {

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        HistoryData history = (HistoryData) intent.getSerializableExtra(Constants.HISTORY_DATA_KEY);

        //タグID（Hex）
        TextView tvTagId = (TextView) findViewById(R.id.TvDetailTagId);
        tvTagId.setText("タグID（Hex）：" + history.getTagID());

        //TID
        TextView tvTid = (TextView) findViewById(R.id.TvDetailTagTid);
        tvTid.setText("TID：" + history.getTID());

        //EPC
        TextView tvEpc = (TextView) findViewById(R.id.TvDetailTagEpc);
        tvEpc.setText("EPC：" + history.getMemoryBankData());

        //USER
        TextView tvUser = (TextView) findViewById(R.id.TvDetailTagUser);
        tvUser.setText("USER：" + history.getUser());

        TextView tvMemory = (TextView) findViewById(R.id.TvDetailTagMemoryBank);
        tvMemory.setText("MEMORY BANK：" + super.getActionLabel(history.getMemoryBankValue()));

        TextView tvAntennaId = (TextView) findViewById(R.id.TvDetailTagAntennaId);
        tvAntennaId.setText("ANTENNA ID：" + history.getAntennaID());

        TextView tvCrc = (TextView) findViewById(R.id.TvDetailTagCrc);
        tvCrc.setText("CRC：" + String.valueOf(history.getCRC()));

        TextView tvEventTime = (TextView) findViewById(R.id.TvDetailTagEventTime);
        tvEventTime.setText("EVENT TIME：" + history.getTagCurrentTime());

    }
}