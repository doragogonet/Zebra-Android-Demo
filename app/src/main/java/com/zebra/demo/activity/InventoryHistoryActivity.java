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

import androidx.appcompat.app.AppCompatActivity;

import com.zebra.demo.R;
import com.zebra.demo.bean.HistoryData;
import com.zebra.demo.tools.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class InventoryHistoryActivity extends AppCompatActivity {

    ListView LvTags;

    public ArrayList<HashMap<String, String>> tagList = new ArrayList<HashMap<String, String>>();
    private HashMap<String, String> map;
    MyAdapter adapter;

    public static final String TAG_RSSI = "tagRssi";
    public static final String TAG_COUNT = "tagCount";
    public static final String TAG_ID = "tagId";
    public static final String TAG_PASSWORD = "tagPassword";
    public static final String TAG_EPC = "tagEpc";
    public static final String TAG_TID = "tagTid";
    public static final String TAG_RESERVED = "tagReserved";
    public static final String TAG_USER = "tagUser";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_history);

        LvTags = findViewById(R.id.LvTags);
        adapter = new MyAdapter(getApplicationContext());
        LvTags.setAdapter(adapter);

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

    private void addDataToList(HistoryData data) {
        if (StringUtils.isNotEmpty(data.getTagEPC())) {

            map = new HashMap<String, String>();
            map.put(TAG_EPC, data.getTagEPC());
            map.put(TAG_TID, data.getTagTID());
            map.put(TAG_RSSI, data.getTagRssi());
            map.put(TAG_COUNT,data.getTagCount());
            map.put(TAG_ID,data.getTagID());
            map.put(TAG_PASSWORD,data.getTagPssword());
            map.put(TAG_RESERVED,data.getTagReserved());
            map.put(TAG_USER,data.getTagUser());

            tagList.add(map);

            adapter.notifyDataSetChanged();

        }
    }

    private int  selectItem = -1;
    public final class ViewHolder {
        public TextView tvTagID;
        public TextView tvTagPssword;
        public TextView tvTagEPC;
        public TextView tvTagTID;
        public TextView tvTagReserved;
        public TextView tvTagUser;
        public TextView tvTagCount;
        public TextView tvTagRssi;
        public TextView tvItemId;
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
                holder.tvTagPssword = (TextView) convertView.findViewById(R.id.TvTagPassword);
                holder.tvTagEPC = (TextView) convertView.findViewById(R.id.TvTagEPC);
                holder.tvTagTID = (TextView) convertView.findViewById(R.id.TvTagTID);
                holder.tvTagReserved = (TextView) convertView.findViewById(R.id.TvTagReserved);
                holder.tvTagUser = (TextView) convertView.findViewById(R.id.TvTagUser);
                holder.tvTagCount = (TextView) convertView.findViewById(R.id.TvTagCount);
                holder.tvTagRssi = (TextView) convertView.findViewById(R.id.TvTagRssi);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.tvItemId.setText(String.valueOf((position + 1)));
            holder.tvTagID.setText((String) tagList.get(position).get(TAG_ID));
            holder.tvTagPssword.setText((String) tagList.get(position).get(TAG_PASSWORD));
            holder.tvTagEPC.setText((String) tagList.get(position).get(TAG_EPC));
            holder.tvTagTID.setText((String) tagList.get(position).get(TAG_TID));
            holder.tvTagReserved.setText((String) tagList.get(position).get(TAG_RESERVED));
            holder.tvTagUser.setText((String) tagList.get(position).get(TAG_USER));
            holder.tvTagCount.setText((String) tagList.get(position).get(TAG_COUNT));
            holder.tvTagRssi.setText((String) tagList.get(position).get(TAG_RSSI));

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
                selectItem=-1;
            }else {
                selectItem = select;
            }

        }
    }

}
