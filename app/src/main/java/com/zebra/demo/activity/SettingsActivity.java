package com.zebra.demo.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
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

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import com.zebra.demo.R;
import com.zebra.demo.base.SettingsUtl;



public class SettingsActivity extends BaseActivity  {

    private static final int BLUETOOTH_PERMISSION_REQUEST_CODE = 100;
    final static String TAG = "ZEBRA-DEMO";
    public Spinner spConnectionType, spBeeperVolume;
    public SeekBar sbPower;
    public TextView tvPower;
    public Spinner spStartTrigger, spStopTrigger;
    public Button btnConnect, btnDisconnect;
    public Switch swHandheldEvent, swTagReadEvent, swAttachTagDataWithReadEvent, swReaderDisconnectEvent, swInfoEvent, swCradleEvent, swBatteryEvent, swFirmwareUpdateEvent, swHeartBeatEvent;

    public TextView statusTextViewRFID = null;
    //その他画面を使う。
    public static RFIDHandler rf;

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
        statusTextViewRFID = findViewById(R.id.textView);

        rf = new RFIDHandler(this);

        // 手動接続ボタンのクリックイベント
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnConnect.setEnabled(false);
                //Scanner Initializations
                //Handling Runtime BT permissions for Android 12 and higher
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
                    if (ContextCompat.checkSelfPermission(SettingsActivity.this,
                            Manifest.permission.BLUETOOTH_CONNECT)
                            != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(SettingsActivity.this,
                                new String[]{Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT},
                                BLUETOOTH_PERMISSION_REQUEST_CODE);
                    }else{
                        rf.InitSDK();
                    }

                }else{
                    rf.InitSDK();
                }

                //String connectionType = spConnectionType.getSelectedItem().toString();
                //if (connectionType.equals("AUTO")) {
                //    autoConnectToReader();  // 自動接続を試みる
                //} else {
                //    connectToReader(connectionType);  // 手動で選択された接続方式で接続
                //}
            }
        });

        // 切断ボタンのクリックイベント
        btnDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rf.onPause();  // リーダーの切断
            }
        });

        // パワースライダーの変更イベント
        sbPower.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvPower.setText(String.valueOf((float) progress / 10));
                SettingsUtl.setReaderPower(RFIDHandler.reader,progress);  // パワー設定
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        // BeeperVolumeスピナーの選択イベント
        spBeeperVolume.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // 選択されたボリュームの値を取得
                String selectedVolume = parentView.getItemAtPosition(position).toString();
         //       rf.settingData.setBeeperVolume(selectedVolume);
                // 選択されたボリュームに応じた処理を実行
                SettingsUtl.setBeeperVolume(RFIDHandler.reader,selectedVolume);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }
        });


        spStartTrigger.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if(RFIDHandler.reader != null && RFIDHandler.reader.isConnected()) {

                    String selectedValue = parentView.getItemAtPosition(position).toString();
                    SettingsUtl.setTriggerStartType(RFIDHandler.reader, selectedValue);
                }
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
                SettingsUtl.setTriggerStopType(RFIDHandler.reader,selectedValue);
            }
           @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }
        });
        swHandheldEvent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SettingsUtl.setInventoryEvent(RFIDHandler.reader,"HandheldEvent", b);
            }
        });
        swTagReadEvent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SettingsUtl.setInventoryEvent(RFIDHandler.reader,"TagReadEvent", b);
            }
        });
        swAttachTagDataWithReadEvent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SettingsUtl.setInventoryEvent(RFIDHandler.reader,"AttachTagDataWithReadEvent", b);
            }
        });
        swReaderDisconnectEvent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SettingsUtl.setInventoryEvent(RFIDHandler.reader,"ReaderDisconnectEvent", b);
            }
        });
        swInfoEvent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SettingsUtl.setInventoryEvent(RFIDHandler.reader,"InfoEvent", b);
            }
        });
        swCradleEvent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SettingsUtl.setInventoryEvent(RFIDHandler.reader,"CradleEvent", b);
            }
        });
        swBatteryEvent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SettingsUtl.setInventoryEvent(RFIDHandler.reader,"BatteryEvent", b);
            }
        });
        swFirmwareUpdateEvent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SettingsUtl.setInventoryEvent(RFIDHandler.reader,"FirmwareUpdateEvent", b);
            }
        });
        swHeartBeatEvent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SettingsUtl.setInventoryEvent(RFIDHandler.reader,"HeartBeatEvent", b);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
//        rf.onPause();
        rf.saveSetting();
    }


    @Override
    protected void onPostResume() {
        super.onPostResume();

        String status = rf.onResume();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                rf.setBtnEnable();
                rf.setViewValue();
                statusTextViewRFID.setText(status);
            }
        });

//        if (rf.reader != null && rf.reader.isConnected()) {
//            changeRadioColor(Color.BLUE);
 //       }else{
  //          changeRadioColor(-11447983);
   //     }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }



    // 権限リクエストの結果を処理

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == BLUETOOTH_PERMISSION_REQUEST_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                rf.InitSDK();
            }
            else {
                Toast.makeText(this, "Bluetooth Permissions not granted", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }




}    //--------------------



