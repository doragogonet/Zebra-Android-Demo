package com.zebra.demo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.zebra.demo.R;
import com.zebra.demo.adapter.InventoryDataAdapter;
import com.zebra.demo.base.RFIDReaderManager;
import com.zebra.demo.bean.HistoryData;
import com.zebra.demo.tools.TxtFileOperator;
import com.zebra.rfid.api3.*;

import java.util.ArrayList;
import java.util.List;

public class InventoryActivity extends BaseActivity {

    private TextView tvTagCount;
    private ListView lvEPC;
    private Button btnStart, btnStop, btnFilter;
    private SeekBar inSbPower;
    private TextView inTvPower;
    private int tagCount = 0;
    private List<HistoryData> tagList = new ArrayList<>();

    InventoryDataAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        // UI要素の初期化
        tvTagCount = findViewById(R.id.tvTagCount);
        lvEPC = findViewById(R.id.lvEPC);
        btnStart = findViewById(R.id.btnStart);
        btnStop = findViewById(R.id.btnStop);
        inSbPower = findViewById(R.id.inSbPower);
        inTvPower = findViewById(R.id.inTvPower);
        btnFilter = findViewById(R.id.btnFilter);
        this.setButtonsEnable(false);

        adapter = new InventoryDataAdapter(this, this.tagList);
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
        inSbPower.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                inTvPower.setText(String.valueOf((float)progress / 10));
                setReaderPower(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        TxtFileOperator.closeFile();
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
                            setDataToList(tag);
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

            TxtFileOperator.openFile(getApplicationContext(), TxtFileOperator.HISTORY_RFID_FILE_NAME);
            this.clearTagList();
            reader.Actions.Inventory.perform();
            this.setButtonsEnable(true);
            Toast.makeText(this, "インベントリ開始", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "インベントリ開始に失敗しました", Toast.LENGTH_SHORT).show();
        }
    }

    // インベントリの終了
    private void stopInventory() {
        try {
            reader.Actions.Inventory.stop();
            TxtFileOperator.closeFile();
            Toast.makeText(this, "インベントリ終了", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "インベントリ終了に失敗しました", Toast.LENGTH_SHORT).show();
        } finally {
            this.setButtonsEnable(false);
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

    private void setDataToList(TagData data) {
        if (data != null) {
            if (this.tagList.stream().noneMatch(tag -> tag.getTagID().equals(data.getTagID()))) {
                HistoryData history = JSON.parseObject(JSON.toJSONString(data), HistoryData.class);
                history.setMemoryBankValue(data.getMemoryBank().getValue());
                history.setTagCurrentTime(data.getTagEventTimeStamp().GetCurrentTime());
                this.addData(history);
                TxtFileOperator.writeFile(data, true);
            } else {
                //HistoryData history = this.tagList.stream().filter(tag -> tag.getTagID().equals(data.getTagID())).findFirst().orElse(null);
                HistoryData history = null;
                for (int i = 0; i < this.tagList.size(); i++) {
                    if (this.tagList.get(i).getTagID().equals(data.getTagID())) {
                        history = this.tagList.get(i);
                        history.setPeakRSSI(String.valueOf(data.getPeakRSSI()));
                        long count = Long.parseLong(history.getTagSeenCount()) + data.getTagSeenCount();
                        history.setTagSeenCount(String.valueOf(count));
                        this.updateData(i, history);
                        break;
                    }
                }
            }
        }
    }

    private void updateData(int position, HistoryData data) {
        // 特定の位置のアイテムのみ更新する
        adapter.updateItem(position, data);
    }

    private void addData(HistoryData data) {
        // 新しいアイテムを追加する
        adapter.addItem(data);
    }

    private void clearTagList() {
        adapter.clearHistoryDatas();
        this.tagList.clear();
    }

    private void setButtonsEnable(boolean isPerform) {
        btnStart.setEnabled(!isPerform);
        btnFilter.setEnabled(!isPerform);
        btnStop.setEnabled(isPerform);
    }
}

