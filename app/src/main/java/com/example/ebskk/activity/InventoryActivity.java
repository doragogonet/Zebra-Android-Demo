package com.example.ebskk.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ebskk.R;
import com.zebra.rfid.api3.*;

import java.util.ArrayList;

public class InventoryActivity extends AppCompatActivity {

    private RFIDReader reader;
    private TextView tvTagCount;
    private ListView lvEPC, lvRSSI;
    private Button btnStart, btnStop, btnFilter;
    private SeekBar sbPower;
    private int tagCount = 0;
    private ArrayList<String> epcList = new ArrayList<>();
    private ArrayList<String> rssiList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        // UI要素の初期化
        tvTagCount = findViewById(R.id.tvTagCount);
        lvEPC = findViewById(R.id.lvEPC);
        lvRSSI = findViewById(R.id.lvRSSI);
        btnStart = findViewById(R.id.btnStart);
        btnStop = findViewById(R.id.btnStop);
        sbPower = findViewById(R.id.sbPower);
        btnFilter = findViewById(R.id.btnFilter);

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
                            epcList.add(tag.getTagID());
                            rssiList.add(String.valueOf(tag.getPeakRSSI()));
                            tagCount++;
                            runOnUiThread(() -> {
                                tvTagCount.setText(String.valueOf(tagCount));
                                // EPCとRSSIのリストを更新
                                // 適切なアダプタを使用してListViewを更新する必要があります
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
}

