package com.zebra.demo.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.zebra.demo.R;
import com.zebra.demo.base.BarChartView;
import com.zebra.demo.base.LineChartData;
import com.zebra.demo.tools.CSVOperator;
import com.zebra.rfid.api3.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventoryActivity extends AppCompatActivity {

    private RFIDReader reader;
    private TextView tvTagCount;
    private ListView lvEPC;
    private Button btnStart, btnStop, btnFilter;
    private SeekBar sbPower;
    private int tagCount = 0;
    private ArrayList<String> epcList = new ArrayList<>();
    private List<TagData> historyTagList = new ArrayList<TagData>();
    public ArrayList<HashMap<String, String>> tagList = new ArrayList<HashMap<String, String>>();

    public static final String TAG_RSSI = "tagRssi";
    public static final String TAG_COUNT = "tagCount";
    public static final String TAG_EPC = "tagEpc";

    private HashMap<String, String> map;
    InventoryActivity.MyAdapter adapter;

    String[] nameList = {"№", "EPC", "COUNT", "RSSI"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        // UI要素の初期化
        tvTagCount = findViewById(R.id.tvTagCount);
        lvEPC = findViewById(R.id.lvEPC);
        btnStart = findViewById(R.id.btnStart);
        btnStop = findViewById(R.id.btnStop);
        sbPower = findViewById(R.id.sbPower);
        btnFilter = findViewById(R.id.btnFilter);

        adapter = new InventoryActivity.MyAdapter(getApplicationContext());
        lvEPC.setAdapter(adapter);

        // RFIDリーダーの初期化
        initializeRFIDReader();

        // 開始ボタンのクリックイベント
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startInventory();
            }
        });
		

        // 終了ボタンのクリックイベント
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopInventory();
            }
        });

        // フィルタボタンのクリックイベント
        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(InventoryActivity.this, FilterSettingsActivity.class);
                startActivity(intent);
            }
        });

        // パワースライダーの変更イベント
        sbPower.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setReaderPower(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    // RFIDリーダーの初期化
    private void initializeRFIDReader() {
        // SettingsActivityで接続されたリーダーを取得
        reader = RFIDReaderManager.getInstance().getReader();

        // リーダーが接続されているかの確認
        if (reader != null && reader.isConnected()) {
            // イベントリスナーの追加
            addReaderEventListener();
        } else {
            Toast.makeText(this, "リーダーが接続されていません", Toast.LENGTH_SHORT).show();
        }
			
    }

    // リーダーイベントリスナーの追加
    private void addReaderEventListener() {
        try {
            reader.Events.addEventsListener(new RfidEventsListener() {
                @Override
                public void eventReadNotify(RfidReadEvents rfidReadEvents) {
                    TagData[] tags = reader.Actions.getReadTags(100);
                    if (tags != null) {
                        for (TagData tag : tags) {
                            if (setDataToList(tag)) {
                                historyTagList.add(tag);    //履歴リストに追加
                                // EPCのリストを更新
                                epcList.add(tag.getTagID());
                            }
                            tagCount++;
                            runOnUiThread(() -> {
                                tvTagCount.setText(String.valueOf(tagCount));
                            });
                        }
                    }
                }

                @Override
                public void eventStatusNotify(RfidStatusEvents rfidStatusEvents) {}
            });
        } catch (Exception e) {
            Toast.makeText(this, "イベントリスナーの追加に失敗しました: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

	}


    // インベントリの開始
    private void startInventory() {
        try {
            this.epcList.clear();
            this.historyTagList.clear();
            reader.Actions.Inventory.perform();
            Toast.makeText(this, "インベントリ開始", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "インベントリ開始に失敗しました", Toast.LENGTH_SHORT).show();
        }
    }

    // インベントリの終了
    private void stopInventory() {
        try {
            reader.Actions.Inventory.stop();
            Toast.makeText(this, "インベントリ終了", Toast.LENGTH_SHORT).show();
            if (!this.historyTagList.isEmpty()) {
                CSVOperator.writeToCsvFile(this.historyTagList, getApplicationContext());
            }
            this.historyTagList.clear();
            this.epcList.clear();
        } catch (Exception e) {
            Toast.makeText(this, "インベントリ終了に失敗しました", Toast.LENGTH_SHORT).show();
        }
    }

    // リーダーのパワーを設定
    private void setReaderPower(int powerLevel) {
        try {
            // power levels are index based so maximum power supported get the last one
            int MAX_POWER = reader.ReaderCapabilities.getTransmitPowerLevelValues().length - 1;
            // set antenna configurations
            Antennas.AntennaRfConfig config = reader.Config.Antennas.getAntennaRfConfig(1);
            if ( MAX_POWER < powerLevel) {
                config.setTransmitPowerIndex(MAX_POWER);
            } else{
                config.setTransmitPowerIndex(MAX_POWER);
            }
            config.setrfModeTableIndex(0);
            config.setTari(0);
            reader.Config.Antennas.setAntennaRfConfig(1, config);
        } catch (Exception e) {
            Toast.makeText(this, "パワー設定に失敗しました", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean setDataToList(TagData data) {
        if (data != null) {
            if (!this.epcList.contains(data.getTagID())) {
                map = new HashMap<String, String>();
                map.put(TAG_EPC, data.getMemoryBankData());
                map.put(TAG_RSSI, String.valueOf(data.getPeakRSSI()));
                map.put(TAG_COUNT, String.valueOf(data.getTagSeenCount()));

                this.tagList.add(map);
                return true;
            } else {
                for (Map<String, String> iMap : this.tagList) {
                    if (data.getTagID().equals(iMap.get(TAG_EPC))) {
                        long count = Long.parseLong(String.valueOf(iMap.get(TAG_COUNT))) + data.getTagSeenCount();
                        iMap.put(TAG_COUNT, String.valueOf(count));
                        iMap.put(TAG_RSSI, String.valueOf(data.getPeakRSSI()));
                        break;
                    }
                }
            }
            // 適切なアダプタを使用してListViewを更新する必要があります
            adapter.notifyDataSetChanged();
        }
        return false;
    }

    //test用
//    private void setDataToList(TagData td) {
//        if (td == null) {
//            map = new HashMap<String, String>();
//            map.put(TAG_EPC, "1234567890");
//            map.put(TAG_RSSI, String.valueOf(-40));
//            map.put(TAG_COUNT, String.valueOf(1));
//
//            this.tagList.add(map);
//
//            map = new HashMap<String, String>();
//            map.put(TAG_EPC, "1234567891");
//            map.put(TAG_RSSI, String.valueOf(-30));
//            map.put(TAG_COUNT, String.valueOf(22));
//
//            this.tagList.add(map);
//            map = new HashMap<String, String>();
//            map.put(TAG_EPC, "1234567892");
//            map.put(TAG_RSSI, String.valueOf(-20));
//            map.put(TAG_COUNT, String.valueOf(66));
//
//            this.tagList.add(map);
//        } else {
//            for (Map<String, String> iMap : this.tagList) {
//                if ("1234567890".equals(iMap.get(TAG_EPC))) {
//                    long count = Long.parseLong(String.valueOf(iMap.get(TAG_COUNT))) + 1;
//                    iMap.put(TAG_COUNT, String.valueOf(count));
//                    iMap.put(TAG_RSSI, String.valueOf(-30));
//                    break;
//                }
//            }
//        }
//        // 適切なアダプタを使用してListViewを更新する必要があります
//        adapter.notifyDataSetChanged();
//    }

    public final class ViewHolder {
        public TextView tvTagEPC;
        public TextView tvTagCount;
        public BarChartView tvTagRssi;
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

            InventoryActivity.ViewHolder holder = null;
            if (convertView == null) {
                holder = new InventoryActivity.ViewHolder();
                convertView = mInflater.inflate(R.layout.inventory_listtag_items, null);
                holder.tvItemId = (TextView) convertView.findViewById(R.id.TvInventoryItemId);
                holder.tvTagEPC = (TextView) convertView.findViewById(R.id.TvInventoryTagEPC);
                holder.tvTagCount = (TextView) convertView.findViewById(R.id.TvInventoryTagCount);
                holder.tvTagRssi = (BarChartView) convertView.findViewById(R.id.TvInventoryTagRssi);

                convertView.setTag(holder);
            } else {
                holder = (InventoryActivity.ViewHolder) convertView.getTag();
            }
            holder.tvItemId.setText(String.valueOf((position + 1)));
            holder.tvTagEPC.setText((String) tagList.get(position).get(TAG_EPC));
            holder.tvTagCount.setText((String) tagList.get(position).get(TAG_COUNT));

//            LineChartData line = new LineChartData();
//            line.setRecover_complete(100);
            int rssi = ((int)Float.parseFloat(tagList.get(position).get(TAG_RSSI)) * -1);
//            line.setRecover_uncomplete(100 - rssi);
            holder.tvTagRssi.setData((100 - rssi) * 5, rssi);

            return convertView;
        }

    }
}

