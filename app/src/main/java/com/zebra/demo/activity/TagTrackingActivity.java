package com.zebra.demo.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.zebra.demo.R;
import com.zebra.demo.base.Constants;
import com.zebra.demo.base.ResponseHandlerInterface;
import com.zebra.demo.tools.StringUtils;
import com.zebra.demo.tools.TxtFileOperator;
import com.zebra.demo.view.VerticalProgressBar;
import com.zebra.rfid.api3.*;

/*********************************************************************************
Zebra RFID SDKのTagLocationing機能を利用して、タグの位置情報を基にタグの追跡を行う。
TagLocationingは、タグのRSSI値だけでなく、RFIDリーダーのビーム方向（方向性）も考慮
して、特定のタグの位置をより正確に特定することができます。
*********************************************************************************/

public class TagTrackingActivity extends BaseActivity implements ResponseHandlerInterface {
    final static String TAG = "ZEBRA-DEMO";

    private EditText etEPCCode;
    private ProgressBar progressBar;
    private Button btnStartTracking, btnStopTracking;
    private TextView textView;
    private String targetEPC;  // 指定されたEPCコード

    private VerticalProgressBar vpProgressBar;
    private RFIDReader reader;
    private EventHandler eventHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_tracking);

        // UI要素の初期化
        etEPCCode = findViewById(R.id.etEPCCode);
        //progressBar = findViewById(R.id.progressBar);
        btnStartTracking = findViewById(R.id.btnStartTracking);
        btnStopTracking = findViewById(R.id.btnStopTracking);
        //TEST用
        textView = findViewById(R.id.textView);
        //etEPCCode = findViewById(R.id.editTextText);

        vpProgressBar = (VerticalProgressBar) findViewById(R.id.vp_progress);

        // SettingsActivityで接続されたリーダーを取得
        reader = RFIDHandler.reader;

        // 開始ボタンのクリックイベント
        btnStartTracking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                targetEPC = etEPCCode.getText().toString().trim();
                startInventory();
                btnStartTracking.setEnabled(false);
                btnStopTracking.setEnabled(true);

            }
        });

        // 終了ボタンのクリックイベント
        btnStopTracking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopTagLocationing();
                stopInventory();
                btnStartTracking.setEnabled(true);
                btnStopTracking.setEnabled(false);

            }
        });

//        // プログレスバーの進行状況を更新するスレッド
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while (progressStatus < 100) {
//                    progressStatus++;
//                    handler.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            verticalProgressBar.setProgress(progressStatus);
//                        }
//                    });
//                    try {
//                        Thread.sleep(100); // 進行スピードを調整
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }).start();
       // run();
    }
    // インベントリの開始
    private void startInventory() {
        try {
            // リーダーが接続されているかの確認
            if (reader != null && reader.isConnected()) {
                // イベントリスナーの追加
                eventHandler = new EventHandler();
                reader.Events.addEventsListener(eventHandler);
                Constants.initRFIDConfig(reader);
                Constants.SettingData2Config(reader);

            } else {
                Toast.makeText(this, "リーダーが接続されていません", Toast.LENGTH_SHORT).show();
            }
            Toast.makeText(this, "インベントリ開始", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "インベントリ開始に失敗しました", Toast.LENGTH_SHORT).show();
        }
    }
    // インベントリの終了
    private void stopInventory() {
        try {
            if (reader != null && reader.isConnected()) {
                // reader.Actions.Inventory.stop();
                if(eventHandler!= null) {
                    reader.Events.removeEventsListener(eventHandler);
                    eventHandler = null;
                }
            }
            TxtFileOperator.closeFile();
            Toast.makeText(this, "インベントリ終了", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "インベントリ終了に失敗しました", Toast.LENGTH_SHORT).show();
        } finally {
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTagLocationing();
        stopInventory();
    }


    // タグロケーションの開始
    synchronized void startTagLocationing() {

        if (targetEPC.isEmpty()) {
            Toast.makeText(this, "EPCコードを入力してください", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            if (reader != null && reader.isConnected()) {
                // タグロケーション設定
                reader.Actions.Inventory.perform();

            } else {
                Toast.makeText(this, "リーダーが接続されていません", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG,"startTagLocationing ERR=" + e.getMessage());
        }
    }

    // タグロケーションの終了
    synchronized void stopTagLocationing() {
        try {
            if (reader != null && reader.isConnected()) {
                reader.Actions.Inventory.stop();  // タグロケーションを停止
            } else {
                Toast.makeText(this, "リーダーが接続されていません", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG,"stopTagLocationing ERR=" + e.getMessage());
        }
    }

    //----------------------------------------------------
    //
    //----------------------------------------------------
    public class EventHandler implements RfidEventsListener {

        // Read Event Notification
        @Override
        public void eventReadNotify(RfidReadEvents e) {
            // Recommended to use new method getReadTagsEx for better performance in case of large tag population
            TagData[] myTags = reader.Actions.getReadTags(100);
            if (myTags != null)  {
                for (TagData myTag : myTags) {
                    Log.d(TAG, "Tag ID " + myTag.getTagID());
                    if (myTag.getOpStatus() == ACCESS_OPERATION_STATUS.ACCESS_SUCCESS) {
                        if (myTag.getMemoryBankData().length() > 0) {
                            Log.d(TAG, " Mem Bank Data " + myTag.getMemoryBankData());
                        }
                    }
                    if (myTag.isContainsLocationInfo() && myTag.getTagID().equals(targetEPC)) {
                        short dist = myTag.LocationInfo.getRelativeDistance();
                        Log.d(TAG, "Tag relative distance " + dist);
                    }
                }
                // possibly if operation was invoked from async task and still busy
                // handle tag data responses on parallel thread thus THREAD_POOL_EXECUTOR
                new AsyncLocationInfo().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, myTags);
            }
        }

        // Status Event Notification
        @Override
        public void eventStatusNotify(RfidStatusEvents rfidStatusEvents) {
            Log.d(TAG, "Status Notification: " + rfidStatusEvents.StatusEventData.getStatusEventType());
            if (rfidStatusEvents.StatusEventData.getStatusEventType() == STATUS_EVENT_TYPE.HANDHELD_TRIGGER_EVENT) {
                if (rfidStatusEvents.StatusEventData.HandheldTriggerEventData.getHandheldEvent() == HANDHELD_TRIGGER_EVENT_TYPE.HANDHELD_TRIGGER_PRESSED) {
                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... voids) {
                            handleTriggerPress(true);
                            return null;
                        }
                    }.execute();
                }
                if (rfidStatusEvents.StatusEventData.HandheldTriggerEventData.getHandheldEvent() == HANDHELD_TRIGGER_EVENT_TYPE.HANDHELD_TRIGGER_RELEASED) {
                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... voids) {
                            handleTriggerPress(false);
                            return null;
                        }
                    }.execute();
                }
            }
        }
    }


    class AsyncLocationInfo extends AsyncTask<TagData[], Void, Void> {
        @Override
        protected Void doInBackground(TagData[]... params) {
            handleTagdata(params[0]);
            return null;
        }

    }

    //LocationInfoデータ処理
    @Override
    public void handleTagdata(TagData[] tagData) {

        runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //画面表示
                    for (TagData tag : tagData) {
                        if (targetEPC.equals(tag.getTagID())) {
                            // 指定されたEPCタグのみを処理
                            // -100dBm～0dBmを0～100に変換
                            int rssi =Math.max(0, Math.min(100, tag.getPeakRSSI() + 100));
                            vpProgressBar.setProgress(rssi);
                        }
                    }
                }
            });


    }


    public void handleTriggerPress (boolean pressed){
        if (pressed) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //statusTextViewRFID.setTex
                    // 画面クリア処理
                    textView.setText("");
                    vpProgressBar.setProgress(0);

                }
            });
            startTagLocationing();
        } else
            stopTagLocationing();
    }




}
