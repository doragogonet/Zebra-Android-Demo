package com.zebra.demo.activity;

import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.zebra.demo.R;
import com.zebra.demo.base.RFIDReaderManager;
import com.zebra.rfid.api3.*;

import java.util.List;

public class SettingsActivity extends BaseActivity {

    private Spinner spConnectionType, spBeeperVolume;
    private SeekBar sbPower;
    private TextView tvPower;
    private Spinner spStartTrigger, spStopTrigger;
    private Button btnConnect, btnDisconnect;
    private Readers readers;

    private Switch swHandheldEvent;

    private ColorFilter cfThump, cfThrack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // UI要素の初期化
        spConnectionType = findViewById(R.id.spConnectionType);
        spBeeperVolume = findViewById(R.id.spBeeperVolume);
        sbPower = findViewById(R.id.sbPower);
        tvPower = findViewById(R.id.tvPower);
        spStartTrigger = findViewById(R.id.spStartTrigger);
        spStopTrigger = findViewById(R.id.spStopTrigger);
        btnConnect = findViewById(R.id.btnConnect);
        btnDisconnect = findViewById(R.id.btnDisconnect);
        swHandheldEvent = findViewById(R.id.swHandheldEvent);

        cfThump = swHandheldEvent.getThumbDrawable().getColorFilter();
        cfThrack = swHandheldEvent.getTrackDrawable().getColorFilter();

        swHandheldEvent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    swHandheldEvent.getThumbDrawable().setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_IN);
                    swHandheldEvent.getTrackDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
                } else {
                    swHandheldEvent.getThumbDrawable().setColorFilter(cfThump);
                    swHandheldEvent.getTrackDrawable().setColorFilter(cfThrack);
                }
            }
        });
        // 手動接続ボタンのクリックイベント
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String connectionType = spConnectionType.getSelectedItem().toString();
                if (connectionType.equals("AUTO")) {
                    autoConnectToReader();  // 自動接続を試みる
                } else {
                    connectToReader(connectionType);  // 手動で選択された接続方式で接続
                }
            }
        });

        // 切断ボタンのクリックイベント
        btnDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disconnectReader();  // リーダーの切断
            }
        });

        // パワースライダーの変更イベント
        sbPower.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvPower.setText(String.valueOf((float)progress / 10));
                setReaderPower(progress);  // パワー設定
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // BeeperVolumeスピナーの選択イベント
        spBeeperVolume.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // 選択されたボリュームの値を取得
                String selectedVolume = parentView.getItemAtPosition(position).toString();
                // 選択されたボリュームに応じた処理を実行
                switch (selectedVolume) {
                    case "Low": // 低ボリュームに設定する処理
                        setBeeperVolume(BEEPER_VOLUME.LOW_BEEP);
                        break;
                    case "Medium": // 中ボリュームに設定する処理
                        setBeeperVolume(BEEPER_VOLUME.MEDIUM_BEEP);
                        break;
                    case "High": // 高ボリュームに設定する処理
                        setBeeperVolume(BEEPER_VOLUME.HIGH_BEEP);
                        break;
                    default: // デフォルトの処理
                        setBeeperVolume(BEEPER_VOLUME.QUIET_BEEP);
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }
        });
    }

    // 自動接続の実装
    private void autoConnectToReader() {
        // USB, Bluetooth, Serialの順で接続を試行
        if (!connectToReader("USB")) {
            if (!connectToReader("BLUETOOTH")) {
                if (!connectToReader("SERIAL")) {
                    // どの方式でも接続できなかった場合の処理
                    runOnUiThread(() -> Toast.makeText(this, "利用可能なリーダーが見つかりません", Toast.LENGTH_SHORT).show());
                }
            }
        }
    }

    // 指定された接続方法でリーダーに接続する
    private boolean connectToReader(String connectionType) {
        try {
            // ENUM_TRANSPORTの設定
            ENUM_TRANSPORT transportType;
            switch (connectionType) {
                case "USB":
                    transportType = ENUM_TRANSPORT.SERVICE_USB;
                    break;
                case "BLUETOOTH":
                    transportType = ENUM_TRANSPORT.BLUETOOTH;
                    break;
                case "SERIAL":
                    transportType = ENUM_TRANSPORT.SERVICE_SERIAL;
                    break;
                default:
                    transportType = ENUM_TRANSPORT.SERVICE_USB;  // デフォルトはUSB
                    break;
            }

            // Readersインスタンスの作成と接続
            readers = new Readers(this.getApplicationContext(), transportType);
            List<ReaderDevice> availableReaders = readers.GetAvailableRFIDReaderList();

            if (availableReaders != null && !availableReaders.isEmpty()) {
                reader = availableReaders.get(0).getRFIDReader();  // 最初の利用可能なリーダーに接続
                reader.connect();

                btnConnect.setEnabled(false);
                btnDisconnect.setEnabled(true);
				// シングルトンにリーダーを保存
                RFIDReaderManager.getInstance().setReader(reader);

                runOnUiThread(() -> Toast.makeText(this, connectionType + "で接続成功", Toast.LENGTH_SHORT).show());
                return true;  // 接続成功
            }
        } catch (Exception e) {
            runOnUiThread(() -> Toast.makeText(this, connectionType + "で接続に失敗: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
        return false;  // 接続失敗
    }

    // リーダーからの切断
    private void disconnectReader() {
        try {
            if (reader != null && reader.isConnected()) {
                reader.disconnect();
                btnDisconnect.setEnabled(false);
                btnConnect.setEnabled(true);
                Toast.makeText(this, "リーダーが切断されました", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "リーダーが接続されていません", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "切断に失敗しました: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // パワーの設定
    private void setReaderPower(int powerLevel) {
        try {
            if (reader != null && reader.isConnected()) {
                //reader.Config.setTransmitPower(powerLevel);
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
                Toast.makeText(this, "パワー設定: " + powerLevel + "dBm", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "リーダーが接続されていません", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "パワー設定に失敗しました: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // ブザー音量の設定
    private void setBeeperVolume(BEEPER_VOLUME volume) {
        try {
            if (reader != null && reader.isConnected()) {
                reader.Config.setBeeperVolume(volume);
                Toast.makeText(this, "ブザー音量: " + volume, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "リーダーが接続されていません", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "ブザー音量設定に失敗しました: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
