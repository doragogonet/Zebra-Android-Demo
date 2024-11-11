package com.zebra.demo.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ProgressBar;

import com.zebra.demo.R;
import com.zebra.demo.adapter.InventoryDataAdapter;
import com.zebra.demo.view.BarChartView;
import com.zebra.demo.base.Constants;
import com.zebra.demo.bean.HistoryData;
import com.zebra.demo.tools.TxtFileOperator;
import com.zebra.rfid.api3.TagData;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InventoryHistoryActivity extends BaseActivity {

    ListView LvTags;

    private ProgressBar progressBar;

    private List<HistoryData> tagList = new ArrayList<>();

    InventoryDataAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_history);

        LvTags = findViewById(R.id.LvTags);
        progressBar = findViewById(R.id.progressBar);
        showLoading();

        this.tagList = TxtFileOperator.readJsonFromFile(getApplicationContext(),TxtFileOperator.HISTORY_RFID_FILE_NAME, HistoryData.class);

        adapter = new InventoryDataAdapter(this, this.tagList);
        LvTags.setAdapter(adapter);

        hideLoading();

        LvTags.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                adapter.notifyDataSetInvalidated();

                Intent intent = new Intent(InventoryHistoryActivity.this, DetailActivity.class);
                intent.putExtra(Constants.HISTORY_DATA_KEY,(Serializable) tagList.get(position));
                startActivity(intent);
            }
        });

    }

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideLoading() {
        progressBar.setVisibility(View.GONE);
    }

}
