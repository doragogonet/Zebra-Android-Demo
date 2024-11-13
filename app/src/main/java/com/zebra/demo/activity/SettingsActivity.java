package com.zebra.demo.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
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
import com.zebra.demo.base.Constants;
import com.zebra.demo.base.RFIDReaderManager;
import com.zebra.demo.bean.SettingData;
import com.zebra.rfid.api3.*;

import java.util.List;

public class SettingsActivity extends BaseActivity {

    private static final String TAG = "SettingsActivity";
    private Spinner spConnectionType, spBeeperVolume;
    private SeekBar sbPower;
    private TextView tvPower;
    private Spinner spStartTrigger, spStopTrigger;
    private Button btnConnect, btnDisconnect;
    private Readers readers;
    private Switch swHandheldEvent,swTagReadEvent,swAttachTagDataWithReadEvent,swReaderDisconnectEvent,swInfoEvent,swCradleEvent,swBatteryEvent,swFirmwareUpdateEvent,swHeartBeatEvent;

    private SettingData settingData = new SettingData();

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
        swTagReadEvent = findViewById(R.id.swTagReadEvent);
        swAttachTagDataWithReadEvent = findViewById(R.id.swAttachTagDataWithReadEvent);
        swReaderDisconnectEvent = findViewById(R.id.swReaderDisconnectEvent);
        swInfoEvent = findViewById(R.id.swInfoEvent);
        swCradleEvent = findViewById(R.id.swCradleEvent);
        swBatteryEvent = findViewById(R.id.swBatteryEvent);
        swFirmwareUpdateEvent = findViewById(R.id.swFirmwareUpdateEvent);
        swHeartBeatEvent = findViewById(R.id.swHeartBeatEvent);

        this.setBtnEnable();
        this.setViewValue();

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
                settingData.setPowerIndex(String.valueOf(progress));
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
                settingData.setBeeperVolume(selectedVolume);
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

        spStartTrigger.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedValue = parentView.getItemAtPosition(position).toString();
                settingData.setStartTrigger(selectedValue);
                StartTrigger tempStartTrigger = getDefaultStartTrigger();
                if (tempStartTrigger == null) {
                    return;
                }
                switch (selectedValue) {
                    case "IMMEDIATE":
                        tempStartTrigger.setTriggerType(START_TRIGGER_TYPE.START_TRIGGER_TYPE_IMMEDIATE);
                        break;
                    case "PERIODIC":
                        tempStartTrigger.setTriggerType(START_TRIGGER_TYPE.START_TRIGGER_TYPE_PERIODIC);
                        break;
                    case "GPI":
                        tempStartTrigger.setTriggerType(START_TRIGGER_TYPE.START_TRIGGER_TYPE_GPI);
                        break;
                    case "HANDHELD":
                        tempStartTrigger.setTriggerType(START_TRIGGER_TYPE.START_TRIGGER_TYPE_HANDHELD);
                        break;
                    default: // デフォルトの処理
                        tempStartTrigger.setTriggerType(START_TRIGGER_TYPE.START_TRIGGER_TYPE_IMMEDIATE);
                        break;
                }
                setStartTrigger(tempStartTrigger);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }
        });

        spStopTrigger.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // 選択されたボリュームの値を取得
                String selectedValue = parentView.getItemAtPosition(position).toString();
                settingData.setStopTrigger(selectedValue);
                StopTrigger tempStopTrigger = getDefaultStopTrigger();
                if (tempStopTrigger == null) {
                    return;
                }
                switch (selectedValue) {
                    case "IMMEDIATE":
                        tempStopTrigger.setTriggerType(STOP_TRIGGER_TYPE.STOP_TRIGGER_TYPE_IMMEDIATE);
                        break;
                    case "DURATION":
                        tempStopTrigger.setTriggerType(STOP_TRIGGER_TYPE.STOP_TRIGGER_TYPE_DURATION);
                        break;
                    case "GPI_WITH_TIMEOUT":
                        tempStopTrigger.setTriggerType(STOP_TRIGGER_TYPE.STOP_TRIGGER_TYPE_GPI_WITH_TIMEOUT);
                        break;
                    case "TAG_OBSERVATION_WITH_TIMEOUT":
                        tempStopTrigger.setTriggerType(STOP_TRIGGER_TYPE.STOP_TRIGGER_TYPE_TAG_OBSERVATION_WITH_TIMEOUT);
                        break;
                    case "N_ATTEMPTS_WITH_TIMEOUT":
                        tempStopTrigger.setTriggerType(STOP_TRIGGER_TYPE.STOP_TRIGGER_TYPE_N_ATTEMPTS_WITH_TIMEOUT);
                        break;
                    case "HANDHELD_WITH_TIMEOUT":
                        tempStopTrigger.setTriggerType(STOP_TRIGGER_TYPE.STOP_TRIGGER_TYPE_HANDHELD_WITH_TIMEOUT);
                        break;
                    case "ACCESS_N_ATTEMPTS_WITH_TIMEOUT":
                        tempStopTrigger.setTriggerType(STOP_TRIGGER_TYPE.STOP_TRIGGER_TYPE_ACCESS_N_ATTEMPTS_WITH_TIMEOUT);
                        break;
                    default: // デフォルトの処理
                        tempStopTrigger.setTriggerType(STOP_TRIGGER_TYPE.STOP_TRIGGER_TYPE_IMMEDIATE);
                        break;
                }
                setStopTrigger(tempStopTrigger);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }
        });
        swHandheldEvent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                setInventoryEvent("HandheldEvent", b);
                settingData.setHandheldEvent(String.valueOf(b));
//                if (b) {
//                    swHandheldEvent.getThumbDrawable().setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_IN);
//                    swHandheldEvent.getTrackDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
//                } else {
//                    swHandheldEvent.getThumbDrawable().setColorFilter(cfThump);
//                    swHandheldEvent.getTrackDrawable().setColorFilter(cfThrack);
//                }
            }
        });
        swTagReadEvent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                settingData.setTagReadEvent(String.valueOf(b));
                setInventoryEvent("TagReadEvent", b);
            }
        });
        swAttachTagDataWithReadEvent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                settingData.setAttachTagDataWithReadEvent(String.valueOf(b));
                setInventoryEvent("AttachTagDataWithReadEvent", b);
            }
        });
        swReaderDisconnectEvent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                settingData.setReaderDisconnectEvent(String.valueOf(b));
                setInventoryEvent("ReaderDisconnectEvent", b);
            }
        });
        swInfoEvent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                settingData.setInfoEvent(String.valueOf(b));
                setInventoryEvent("InfoEvent", b);
            }
        });
        swCradleEvent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                settingData.setCradleEvent(String.valueOf(b));
                setInventoryEvent("CradleEvent", b);
            }
        });
        swBatteryEvent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                settingData.setBatteryEvent(String.valueOf(b));
                setInventoryEvent("BatteryEvent", b);
            }
        });
        swFirmwareUpdateEvent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                settingData.setFirmwareUpdateEvent(String.valueOf(b));
                setInventoryEvent("FirmwareUpdateEvent", b);
            }
        });
        swHeartBeatEvent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                settingData.setHeartBeatEvent(String.valueOf(b));
                setInventoryEvent("HeartBeatEvent", b);
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
                this.saveSetting();
                this.setBtnEnable();
                super.changeRadioColor(Color.GREEN);
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
                this.setBtnEnable();
                super.changeRadioColor(-11447983);
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
                this.saveSetting();
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
                this.saveSetting();
                Toast.makeText(this, "ブザー音量: " + volume, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "リーダーが接続されていません", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "ブザー音量設定に失敗しました: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void setStartTrigger(StartTrigger trigger) {
        try {
            if (reader != null && reader.isConnected()) {
                reader.Config.setStartTrigger(trigger);
                this.saveSetting();
                Toast.makeText(this, "スタートトリガ: " + trigger, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "リーダーが接続されていません", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "スタートトリガ設定に失敗しました: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void setStopTrigger(StopTrigger trigger) {
        try {
            if (reader != null && reader.isConnected()) {
                reader.Config.setStopTrigger(trigger);
                this.saveSetting();
                Toast.makeText(this, "ストップトリガ: " + trigger, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "リーダーが接続されていません", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "ストップトリガ設定に失敗しました: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void setInventoryEvent(String eventFlag, boolean status) {
        try {
            if (reader != null && reader.isConnected()) {
                switch (eventFlag) {
                    case "HandheldEvent" :
                        reader.Events.setHandheldEvent(status);
                        break;
                    case "TagReadEvent" :
                        reader.Events.setTagReadEvent(status);
                        break;
                    case "AttachTagDataWithReadEvent" :
                        reader.Events.setAttachTagDataWithReadEvent(status);
                        break;
                    case "ReaderDisconnectEvent" :
                        reader.Events.setReaderDisconnectEvent(status);
                        break;
                    case "InfoEvent" :
                        reader.Events.setInfoEvent(status);
                        break;
                    case "CradleEvent" :
                        reader.Events.setCradleEvent(status);
                        break;
                    case "BatteryEvent" :
                        reader.Events.setBatteryEvent(status);
                        break;
                    case "FirmwareUpdateEvent" :
                        reader.Events.setFirmwareUpdateEvent(status);
                        break;
                    case "HeartBeatEvent" :
                        reader.Events.setHeartBeatEvent(status);
                        break;
                }
                this.saveSetting();
                Toast.makeText(this, eventFlag + ": " + status, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "リーダーが接続されていません", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, eventFlag + "設定に失敗しました: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void setBtnEnable() {
        boolean isConn = false;
        if (super.reader != null && reader.isConnected()) {
            isConn = true;
        }
        btnConnect.setEnabled(!isConn);
        btnDisconnect.setEnabled(isConn);
        spConnectionType.setEnabled(!isConn);
        spBeeperVolume.setEnabled(!isConn);
        sbPower.setEnabled(!isConn);
        tvPower.setEnabled(!isConn);
        spStartTrigger.setEnabled(!isConn);
        spStopTrigger.setEnabled(!isConn);
        swHandheldEvent.setEnabled(!isConn);
        swAttachTagDataWithReadEvent.setEnabled(!isConn);
        swReaderDisconnectEvent.setEnabled(!isConn);
        swInfoEvent.setEnabled(!isConn);
        swCradleEvent.setEnabled(!isConn);
        swBatteryEvent.setEnabled(!isConn);
        swFirmwareUpdateEvent.setEnabled(!isConn);
        swHeartBeatEvent.setEnabled(!isConn);

    }

    private StartTrigger getDefaultStartTrigger() {
        StartTrigger tempStartTrigger = null;
        try {
            if (super.reader == null || !reader.isConnected()) {
                return null;
            }
            tempStartTrigger = reader.Config.getStartTrigger();
        } catch (InvalidUsageException e) {
            if( e!= null && e.getStackTrace().length>0){ Log.e(TAG, e.getStackTrace()[0].toString()); }
        } catch (OperationFailureException e) {
            if( e!= null && e.getStackTrace().length>0){ Log.e(TAG, e.getStackTrace()[0].toString()); }
        }
        return tempStartTrigger;
    }

    private StopTrigger getDefaultStopTrigger() {
        StopTrigger tempStopTrigger = null;
        try {
            if (super.reader == null || !reader.isConnected()) {
                return null;
            }
            tempStopTrigger = reader.Config.getStopTrigger();
        } catch (InvalidUsageException e) {
            if( e!= null && e.getStackTrace().length>0){ Log.e(TAG, e.getStackTrace()[0].toString()); }
        } catch (OperationFailureException e) {
            if( e!= null && e.getStackTrace().length>0){ Log.e(TAG, e.getStackTrace()[0].toString()); }
        }
        return tempStopTrigger;
    }

    private void saveSetting() {
        if (super.reader == null || !reader.isConnected()) {
            return;
        }

        this.saveValue(Constants.ZEBRA_EBS_STORAGE_SETTING, settingData);
    }

    private void setViewValue() {
        if (super.reader == null || !reader.isConnected()) {
            return;
        }
        try {
            settingData = this.getValue(Constants.ZEBRA_EBS_STORAGE_SETTING);
            spBeeperVolume.setSelection(Integer.parseInt(settingData.getBeeperVolume()));
            sbPower.setProgress(Integer.parseInt(settingData.getPowerIndex()));
            tvPower.setText(String.valueOf(Integer.parseInt(settingData.getPowerIndex()) / 10));
            spStartTrigger.setSelection(Integer.parseInt(settingData.getStartTrigger()));
            spStopTrigger.setSelection(Integer.parseInt(settingData.getStopTrigger()));
            swHandheldEvent.setChecked(Boolean.parseBoolean(settingData.getHandheldEvent()));
            swTagReadEvent.setChecked(Boolean.parseBoolean(settingData.getTagReadEvent()));
            swAttachTagDataWithReadEvent.setChecked(Boolean.parseBoolean(settingData.getAttachTagDataWithReadEvent()));
            swReaderDisconnectEvent.setChecked(Boolean.parseBoolean(settingData.getReaderDisconnectEvent()));
            swInfoEvent.setChecked(Boolean.parseBoolean(settingData.getInfoEvent()));
            swCradleEvent.setChecked(Boolean.parseBoolean(settingData.getCradleEvent()));
            swBatteryEvent.setChecked(Boolean.parseBoolean(settingData.getBatteryEvent()));
            swFirmwareUpdateEvent.setChecked(Boolean.parseBoolean(settingData.getFirmwareUpdateEvent()));
            swHeartBeatEvent.setChecked(Boolean.parseBoolean(settingData.getHeartBeatEvent()));
        } catch (Exception ignored) {

        }
    }
}
