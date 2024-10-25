package com.example.ebskk.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ebskk.R;
import com.example.ebskk.fragment.UHFReadTagFragment;
import com.example.ebskk.tools.EpcData;
import com.example.ebskk.tools.EpcFormat;
import com.example.ebskk.tools.StringUtils;
import com.zebra.rfid.api3.BEEPER_VOLUME;
import com.zebra.rfid.api3.ENUM_TRANSPORT;
import com.zebra.rfid.api3.RFIDReader;
import com.zebra.rfid.api3.Readers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InventoryHistoryActivity extends AppCompatActivity {

    ListView LvTags;

    public ArrayList<HashMap<String, String>> tagList = new ArrayList<HashMap<String, String>>();
    private HashMap<String, String> map;
    MyAdapter adapter;

    public static final String TAG_EPC_TID = "tagEpcTID";
    public static final String TAG_RSSI = "tagRssi";
    public static final String TAG_EPC = "tagEPC";

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
                // 处理点击事件逻辑，position表示点击的位置
                adapter.setSelectItem(position);
                adapter.notifyDataSetInvalidated();

                Intent intent = new Intent(InventoryHistoryActivity.this, DetailActivity.class);
//                int index = mContext.uhfInfo.getSelectIndex();
                intent.putExtra("map",tagList.get(position));
                startActivity(intent);
            }
        });

    }

    private void addDataToList(String epc,String epcAndTidUser, String rssi) {
        if (StringUtils.isNotEmpty(epc)) {

            map = new HashMap<String, String>();
            map.put(TAG_EPC, epc);
            map.put(TAG_EPC_TID, epcAndTidUser);
            map.put(TAG_RSSI, rssi);

            String tempCode = "";
            String tempName = "";
            if (epc.length()>6) {
                tempCode = epc.substring(0,6);
            }
            map.put(TAG_EPC_TID, "");
            map.put(TAG_EPC, "");
            map.put(TAG_RSSI, "");
            tagList.add(map);

            adapter.notifyDataSetChanged();

        }
    }

    private int  selectItem = -1;
    public final class ViewHolder {
        public TextView tvEPCTID;
        public TextView tvTagCount;
        public TextView tvTagRssi;
        //sxt add start
        public TextView tvItemId;           //索引
        public TextView tvProCode;          //号型代码
        public TextView tvProName;          //物资名称
        public TextView tvProModel;         //物资号型
        public TextView tvProCount;         //装箱数量
        public TextView tvProBatch;         //批次编号
        public TextView tvProType;          //种类
        public TextView tvProCreateTime;    //生产日期
        public TextView tvProCreateCompany;     //生产企业
        public TextView tvProBox;               //箱
        //sxt add end
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
                holder.tvEPCTID = (TextView) convertView.findViewById(R.id.TvTagUii);
                holder.tvTagCount = (TextView) convertView.findViewById(R.id.TvTagCount);
                holder.tvTagRssi = (TextView) convertView.findViewById(R.id.TvTagRssi);

                holder.tvItemId = (TextView) convertView.findViewById(R.id.TvItemId);   //找到布局
                holder.tvProCode = (TextView) convertView.findViewById(R.id.TvProCode);
                holder.tvProName = (TextView) convertView.findViewById(R.id.TvProName);
                holder.tvProModel = (TextView) convertView.findViewById(R.id.TvProModel);
                holder.tvProCount = (TextView) convertView.findViewById(R.id.TvProCount);
                holder.tvProBatch = (TextView) convertView.findViewById(R.id.TvProBatch);
                holder.tvProType = (TextView) convertView.findViewById(R.id.TvProType);
                holder.tvProCreateTime = (TextView) convertView.findViewById(R.id.TvProCreateTime);
                holder.tvProCreateCompany = (TextView) convertView.findViewById(R.id.TvProCreateCompany);
                holder.tvProBox = (TextView) convertView.findViewById(R.id.TvProBox);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.tvEPCTID.setText((String) tagList.get(position).get(TAG_EPC_TID));
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
