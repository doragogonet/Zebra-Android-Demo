package com.zebra.demo.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import com.zebra.demo.R;
import com.zebra.demo.adapter.InventoryDataAdapter;
import com.zebra.demo.base.Constants;
import com.zebra.demo.base.ResponseHandlerInterface;
import com.zebra.demo.base.SettingsUtl;
import com.zebra.demo.bean.HistoryData;
import com.zebra.demo.tools.FilterInfo;
import com.zebra.demo.tools.TxtFileOperator;
import com.zebra.demo.tools.UtilsZebra;
import com.zebra.rfid.api3.*;

import java.util.ArrayList;
import java.util.List;

public  class InventoryActivity extends BaseActivity implements ResponseHandlerInterface{
    final static String TAG = "ZEBRA-DEMO";
    private TextView tvTagCount;
    private ListView lvEPC;
    private Button btnStart, btnStop, btnFilter;
    private SeekBar inSbPower;
//    private TextView inTvPower,textView;      //sxt 20241230 del
    private TextView inTvPower;                     //sxt 20241230 add
    private int tagCount = 0;
    private List<HistoryData> tagList = new ArrayList<>();
    private RFIDReader reader;
    EventHandlerSet eventHandler;
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
        //TEST
//        textView = findViewById(R.id.textView);       //sxt 20241230 del

        this.setButtonsEnable(false);

        adapter = new InventoryDataAdapter(this, this.tagList);
        lvEPC.setAdapter(adapter);
        reader = RFIDHandler.reader;


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
                inTvPower.setText(String.valueOf((float) progress / 10));
                SettingsUtl.setReaderPower(reader,progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(reader != null && reader.isConnected()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //画面表示
                    if (RFIDHandler.settingData.getPowerIndex() != null) {
                        inSbPower.setProgress(Integer.parseInt(RFIDHandler.settingData.getPowerIndex()));
                        inTvPower.setText(String.valueOf(Integer.parseInt(RFIDHandler.settingData.getPowerIndex()) / 10));
                        //  下面执行的话 系统崩溃?
                        changeRadioColor(Color.GREEN);
                    } else {
                        changeRadioColor(Color.DKGRAY);

                    }
                }
            });
        }
        return true;
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        //メニュー更新の通知
        if(super.imgMenuItem == null ){
            invalidateOptionsMenu();
        }
        if (reader != null && reader.isConnected()) {
            super.changeRadioColor(Color.GREEN);
        }else{
            super.changeRadioColor(Color.DKGRAY);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopInventory();
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        this.stopInventory();
    }

    // RFIDリーダーの初期化
    private void initializeRFIDReader() {
        // SettingsActivityで接続されたリーダーを取得
        try {
            // リーダーが接続されているかの確認
            if (reader != null && reader.isConnected()) {

                eventHandler =  new EventHandlerSet();
                reader.Events.addEventsListener(eventHandler);
                Constants.initRFIDConfig(reader);
                Constants.SettingData2Config(reader);

            } else {
                Toast.makeText(this, "リーダーが接続されていません", Toast.LENGTH_SHORT).show();
            }
        } catch (InvalidUsageException | OperationFailureException e) {
            Log.e("ConfigureReader", "InvalidUsageException" + e.getMessage());
        }

    }


    // インベントリの開始
    private void startInventory() {
        try {

            this.clearTagList();
            // RFIDリーダーのインベントリー初期化
            initializeRFIDReader();
            this.setButtonsEnable(true);
            Toast.makeText(this, "インベントリ開始", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "インベントリ開始に失敗しました", Toast.LENGTH_SHORT).show();
        }
    }

    // インベントリの終了
    private void stopInventory() {
        try {
            if (reader != null && reader.isConnected()) {
                //reader.Actions.Inventory.stop();
                if( eventHandler != null ) {
                    reader.Events.removeEventsListener(eventHandler);
                    eventHandler = null;
                    TxtFileOperator.writeFile(getApplicationContext(), TxtFileOperator.HISTORY_RFID_FILE_NAME, this.tagList);
                }

            }

            Toast.makeText(this, "インベントリ終了", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "インベントリ終了に失敗しました", Toast.LENGTH_SHORT).show();
        } finally {
            this.setButtonsEnable(false);
        }
    }


    private void setDataToList(TagData data) {
        if (data != null) {
            if (this.tagList.stream().noneMatch(tag -> tag.getTagID().equals(data.getTagID()))) {
                //HistoryData history = JSON.parseObject(JSON.toJSONString(data), HistoryData.class);
                HistoryData history = new HistoryData();
                history.setTagID(data.getTagID());
                if(data.getMemoryBank() != null)
                    history.setMemoryBankValue(data.getMemoryBank().getValue());
               // history.setMemoryBankValue(data.getMemoryBankDataAllocatedSize());
                if (data.getTagEventTimeStamp() !=null)
                    history.setTagCurrentTime(data.getTagEventTimeStamp().GetCurrentTime());

                history.setPeakRSSI(String.valueOf(data.getPeakRSSI()));

                history.setTagSeenCount(String.valueOf(data.getTagSeenCount()));
                history.setPC(String.valueOf(data.getPC()));

                this.addData(history);
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

    public void updateData(int position, HistoryData data) {
        // 特定の位置のアイテムのみ更新する
        adapter.updateItem(position, data);
    }

    public void addData(HistoryData data) {
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


    /// -------------------------------------------------
    ///　　イベント作成
    ///
    /// ------------------------------------------------
// Read/Status Notify handler
// Implement the RfidEventsLister class to receive event notifications
    public class EventHandlerSet implements RfidEventsListener {
        // Read Event Notification
        @Override
        public void eventReadNotify(RfidReadEvents e) {
            // Recommended to use new method getReadTagsEx for better performance in case of large tag population
            TagData[] myTags = reader.Actions.getReadTags(100);
            if (myTags != null) {
                for (TagData myTag : myTags) {
                    Log.d(TAG, "Tag ID " + myTag.getTagID());
                    if (myTag.getOpCode() == ACCESS_OPERATION_CODE.ACCESS_OPERATION_READ &&
                            myTag.getOpStatus() == ACCESS_OPERATION_STATUS.ACCESS_SUCCESS) {
                        if (myTag.getMemoryBankData().length() > 0) {
                            Log.d(TAG, " Mem Bank Data " + myTag.getMemoryBankData());
                        }
                    }
                    if (myTag.isContainsLocationInfo()) {
                        short dist = myTag.LocationInfo.getRelativeDistance();
                        Log.d(TAG, "Tag relative distance " + dist);
                    }
                }
                // possibly if operation was invoked from async task and still busy
                // handle tag data responses on parallel thread thus THREAD_POOL_EXECUTOR
                new AsyncDataUpdate().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, myTags);
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
    class AsyncDataUpdate extends AsyncTask<TagData[], Void, Void> {
        @Override
        protected Void doInBackground(TagData[]... params) {
            handleTagdata(params[0]);
            return null;
        }

    }

    //リーダから受信したデータ処理インタフェース実装
    @Override
    public void handleTagdata(TagData[] tagData) {

        //
        final StringBuilder sb = new StringBuilder();

        for (int index = 0; index < tagData.length; index++) {
            sb.append(tagData[index].getTagID() + "\n");
            tagCount ++;

        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //画面表示
                tvTagCount.setText(String.valueOf(tagCount));
//                textView.append(sb.toString());       //sxt 20241230 del

                for (int index = 0; index < tagData.length; index++) {
                    setDataToList(tagData[index]);
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
//                        textView.setText("");     //sxt 20241230 del
                        tagCount = 0;
                        clearTagList();
                    }
                });
                //kaku ADD 2025-1-1
                applyPreFilters();
                SettingsActivity.rf.performInventory();
            } else
                SettingsActivity.rf.stopInventory();
        }


    // PreFiltersの適用
    private void applyPreFilters() {

        List<FilterInfo> filterList = new ArrayList<>();   // フィルタ情報のリスト
        filterList = TxtFileOperator.readJsonFromFile(getApplicationContext(), TxtFileOperator.FILTER_FILE_NAME, FilterInfo.class);
        if(filterList.isEmpty()) return;

        // SettingsActivityで接続されたリーダーを取得
        if (reader == null || !reader.isConnected()) {
            Toast.makeText(this, "リーダーが接続されていません", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // PreFiltersをクリア
            reader.Actions.PreFilters.deleteAll();

            // 各フィルタをリーダーのPreFiltersに設定
            for (FilterInfo filter : filterList) {
                // フィルタ情報
                String data = filter.getFilterData();
               // String memoryBankIndex = String.valueOf(filter.getFilterMemoryBankSelection());
                int offset = Integer.parseInt(filter.getFilterOffset());
                int length = Integer.parseInt(filter.getFilterLength());
                String actionIndex = String.valueOf(filter.getFilterActionSelection());
                String targetIndex = String.valueOf(filter.getFilterTargetSelection());

                // PreFilterオブジェクトの作成
                PreFilters preFilters = new PreFilters();
                PreFilters.PreFilter preFilter = preFilters.new PreFilter();

                MEMORY_BANK memoryBank = MEMORY_BANK.GetMemoryBankValue(filter.getFilterMemoryBankSelection());
                byte[] bydata = com.zebra.demo.activity.SettingsUtl.hexStringToByteArray(data);
                preFilter.setTagPattern(bydata);
                preFilter.setMemoryBank(memoryBank);
                preFilter.setBitOffset(offset);
                preFilter.setTagPatternBitCount(length);

                // アクションとターゲットを設定
                preFilter.setFilterAction(FILTER_ACTION.FILTER_ACTION_STATE_AWARE);
                TARGET target = UtilsZebra.getStateAwareTarget(targetIndex);
                preFilter.StateAwareAction.setTarget(target);

                STATE_AWARE_ACTION stateAwareAction = UtilsZebra.getStateAwareAction(actionIndex);
                preFilter.StateAwareAction.setStateAwareAction(stateAwareAction);

                // PreFilterをリーダーに追加
                reader.Actions.PreFilters.add(preFilter);
            }


        } catch (Exception e) {
            Toast.makeText(this, "プリフィルタの設定に失敗しました: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


}

