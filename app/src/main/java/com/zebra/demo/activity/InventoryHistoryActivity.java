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

import androidx.appcompat.app.AppCompatActivity;

import com.zebra.demo.R;
import com.zebra.demo.bean.HistoryData;
import com.zebra.demo.tools.CSVOperator;
import com.zebra.demo.tools.StringUtils;
import com.zebra.rfid.api3.TagData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InventoryHistoryActivity extends AppCompatActivity {

    ListView LvTags;

    private ProgressBar progressBar;

    public ArrayList<HashMap<String, String>> tagList = new ArrayList<HashMap<String, String>>();
    private HashMap<String, String> map;
    MyAdapter adapter;

    public static final String TAG_RSSI = "tagRssi";
    public static final String TAG_COUNT = "tagCount";
    public static final String TAG_ID = "tagId";
    public static final String TAG_EPC = "tagEpc";
    public static final String TAG_TID = "tagTid";
    public static final String TAG_USER = "tagUser";
    public static final String TAG_MEMORY_BANK = "tagMemoryBank";
    public static final String TAG_ANTENNA_ID = "tagAntennaId";
    public static final String TAG_CRC = "tagCrc";
    public static final String TAG_EVENT_TIME = "tagEventTime";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_history);

        progressBar = findViewById(R.id.progressBar);
        showLoading();

        List<TagData> csvTagList = CSVOperator.readCsvFile();

        LvTags = findViewById(R.id.LvTags);
        adapter = new MyAdapter(getApplicationContext());
        LvTags.setAdapter(adapter);

        for (TagData tag : csvTagList) {
            this.addDataToList(tag);
        }

        hideLoading();

        LvTags.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                adapter.setSelectItem(position);
                adapter.notifyDataSetInvalidated();

                Intent intent = new Intent(InventoryHistoryActivity.this, DetailActivity.class);
//                int index = mContext.uhfInfo.getSelectIndex();
                intent.putExtra("map",tagList.get(position));
                startActivity(intent);
            }
        });

    }

    private void addDataToList(TagData data) {
        if (data != null) {

            map = new HashMap<String, String>();
            map.put(TAG_EPC, data.getMemoryBankData());
            map.put(TAG_TID, data.getTID());
            map.put(TAG_RSSI, String.valueOf(data.getPeakRSSI()));
            map.put(TAG_COUNT,String.valueOf(data.getTagSeenCount()));
            map.put(TAG_ID,data.getTagID());
            map.put(TAG_USER,data.getUser());
            map.put(TAG_MEMORY_BANK,data.getMemoryBank().toString());
            map.put(TAG_ANTENNA_ID,String.valueOf(data.getAntennaID()));
            map.put(TAG_CRC,data.getStringCRC());
            map.put(TAG_EVENT_TIME,data.getTagEventTimeStamp().ConvertTimetoDate());

            tagList.add(map);

            adapter.notifyDataSetChanged();

        }
    }

    private int  selectItem = -1;
    public final class ViewHolder {
        public TextView tvTagID;
        public TextView tvTagEPC;
        public TextView tvTagTID;
        public TextView tvTagUser;
        public TextView tvTagCount;
        public TextView tvTagRssi;
        public TextView tvItemId;
        public TextView tvTagMemoryBank;
        public TextView tvTagAntennaId;
        public TextView tvTagCrc;
        public TextView tvTagEventTime;
    }
    public class MyAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        Context myContext;
        public MyAdapter(Context context) {
            myContext = context;
            this.mInflater = LayoutInflater.from(context);
        }
        public int getCount() {
            // TODO Auto-generated method stub
            return tagList.size();
        }
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return tagList.get(arg0);
        }
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return arg0;
        }
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.listtag_items, null);
                holder.tvItemId = (TextView) convertView.findViewById(R.id.TvItemId);
                holder.tvTagID = (TextView) convertView.findViewById(R.id.TvTagID);
                holder.tvTagEPC = (TextView) convertView.findViewById(R.id.TvTagEPC);
                holder.tvTagTID = (TextView) convertView.findViewById(R.id.TvTagTID);
                holder.tvTagUser = (TextView) convertView.findViewById(R.id.TvTagUser);
                holder.tvTagCount = (TextView) convertView.findViewById(R.id.TvTagCount);
                holder.tvTagRssi = (TextView) convertView.findViewById(R.id.TvTagRssi);
                holder.tvTagMemoryBank = (TextView) convertView.findViewById(R.id.TvTagMemoryBank);
                holder.tvTagAntennaId = (TextView) convertView.findViewById(R.id.TvTagAntennaId);
                holder.tvTagCrc = (TextView) convertView.findViewById(R.id.TvTagCrc);
                holder.tvTagEventTime = (TextView) convertView.findViewById(R.id.TvTagEventTime);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.tvItemId.setText(String.valueOf((position + 1)));
            holder.tvTagID.setText((String) tagList.get(position).get(TAG_ID));
            holder.tvTagEPC.setText((String) tagList.get(position).get(TAG_EPC));
            holder.tvTagTID.setText((String) tagList.get(position).get(TAG_TID));
            holder.tvTagUser.setText((String) tagList.get(position).get(TAG_USER));
            holder.tvTagCount.setText((String) tagList.get(position).get(TAG_COUNT));
            holder.tvTagRssi.setText((String) tagList.get(position).get(TAG_RSSI));
            holder.tvTagMemoryBank.setText((String) tagList.get(position).get(TAG_MEMORY_BANK));
            holder.tvTagAntennaId.setText((String) tagList.get(position).get(TAG_ANTENNA_ID));
            holder.tvTagCrc.setText((String) tagList.get(position).get(TAG_CRC));
            holder.tvTagEventTime.setText((String) tagList.get(position).get(TAG_EVENT_TIME));

            if (position == selectItem) {
                convertView.setBackgroundColor(myContext.getResources().getColor(R.color.lfile_colorPrimary));
            }
            else {
                convertView.setBackgroundColor(Color.TRANSPARENT);
            }
            return convertView;
        }
        public  void setSelectItem(int select) {
            if(selectItem == select){
                selectItem = -1;
            }else {
                selectItem = select;
            }

        }
    }

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideLoading() {
        progressBar.setVisibility(View.GONE);
    }

}
