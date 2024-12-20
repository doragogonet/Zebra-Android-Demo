package com.zebra.demo.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.zebra.demo.R;
import com.zebra.demo.adapter.FilterDataAdapter;
import com.zebra.demo.base.RFIDReaderManager;
import com.zebra.demo.tools.FilterInfo;
import com.zebra.demo.tools.TxtFileOperator;
import com.zebra.demo.tools.UtilsZebra;
import com.zebra.rfid.api3.FILTER_ACTION;
import com.zebra.rfid.api3.MEMORY_BANK;
import com.zebra.rfid.api3.PreFilters;
import com.zebra.rfid.api3.RFIDReader;
import com.zebra.rfid.api3.STATE_AWARE_ACTION;
import com.zebra.rfid.api3.TARGET;

import java.util.ArrayList;
import java.util.List;

public class FilterSettingsActivity extends BaseActivity {

    private EditText etNumber, etData, etOffset, etLength;
    private Spinner spMemoryBank, spAction, spTarget;
    private Button btnAddFilter, btnSetFilter;
    private ListView lvFilters;
    private List<FilterInfo> filterList = new ArrayList<>();   // フィルタ情報のリスト
    private FilterDataAdapter filterAdapter;
    private int memoryBankSelection;
    private int actionSelection;
    private int targetBankSelection;
    private int itemIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_settings);

        // UI要素の初期化
        etNumber = findViewById(R.id.etNumber);
        etData = findViewById(R.id.etData);
        etOffset = findViewById(R.id.etOffset);
        etLength = findViewById(R.id.etLength);
        spMemoryBank = findViewById(R.id.spMemoryBank);
        spAction = findViewById(R.id.spAction);
        spTarget = findViewById(R.id.spTarget);
        btnAddFilter = findViewById(R.id.btnAddFilter);
        btnSetFilter = findViewById(R.id.btnSetFilter);
        lvFilters = findViewById(R.id.lvFilters);

        this.filterList = TxtFileOperator.readJsonFromFile(getApplicationContext(), TxtFileOperator.FILTER_FILE_NAME, FilterInfo.class);

        // フィルタリストの初期化
        filterAdapter = new FilterDataAdapter(this, this.filterList);
        lvFilters.setAdapter(filterAdapter);

        this.setSetBtnEnable();

        // 追加ボタンのクリックイベント
        btnAddFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFilter();
            }
        });

        // 追加ボタンのクリックイベント
        btnSetFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //リーダーに設定する
                applyPreFilters();
            }
        });

//        // リスト項目の長押しで削除
//        lvFilters.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                filterList.remove(position);
//                return true;
//            }
//        });

        lvFilters.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                btnAddFilter.setText("変更");
                itemIndex = position;
                FilterInfo info = filterList.get(position);
                etNumber.setText(info.getFilterNumber());
                etData.setText(info.getFilterData());
                spMemoryBank.setSelection(info.getFilterMemoryBankSelection());
                etOffset.setText(info.getFilterOffset());
                etLength.setText(info.getFilterLength());
                spAction.setSelection(info.getFilterActionSelection());
                spTarget.setSelection(info.getFilterTargetSelection());
            }
        });

        spMemoryBank.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                memoryBankSelection = position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spAction.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                actionSelection = position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spTarget.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                targetBankSelection = position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    // フィルタの追加
    private void addFilter() {
        String number = etNumber.getText().toString().trim();
        String data = etData.getText().toString().trim();
        String offset = etOffset.getText().toString().trim();
        String length = etLength.getText().toString().trim();

        if (number.isEmpty() || data.isEmpty() || offset.isEmpty() || length.isEmpty()) {
            Toast.makeText(this, "すべてのフィールドを入力してください", Toast.LENGTH_SHORT).show();
            return;
        }

        // フィルタ情報の追加
//        String filter = "番号: " + number + ", データ: " + data + ", メモリバンク: " + this.memoryBankSelection +
//                        ", オフセット: " + offset + ", 長さ: " + length +
//                        ", アクション: " + this.actionSelection + ", ターゲット: " + this.targetBankSelection;

        if (itemIndex == -1) {
            FilterInfo info = new FilterInfo();
            info.setFilterNumber(number);
            info.setFilterData(data);
            info.setFilterMemoryBank(this.getMemoryBankLabel(this.memoryBankSelection));
            info.setFilterMemoryBankSelection(this.memoryBankSelection);
            info.setFilterOffset(offset);
            info.setFilterLength(length);
            info.setFilterAction(this.getActionLabel(this.actionSelection));
            info.setFilterActionSelection(this.actionSelection);
            info.setFilterTarget(this.getTargetLabel(this.targetBankSelection));
            info.setFilterTargetSelection(this.targetBankSelection);
            filterAdapter.addItem(info);
        } else {
            FilterInfo info = this.filterList.get(itemIndex);
            info.setFilterNumber(number);
            info.setFilterData(data);
            info.setFilterMemoryBank(this.getMemoryBankLabel(this.memoryBankSelection));
            info.setFilterMemoryBankSelection(this.memoryBankSelection);
            info.setFilterOffset(offset);
            info.setFilterLength(length);
            info.setFilterAction(this.getActionLabel(this.actionSelection));
            info.setFilterActionSelection(this.actionSelection);
            info.setFilterTarget(this.getTargetLabel(this.targetBankSelection));
            info.setFilterTargetSelection(this.targetBankSelection);
            filterAdapter.updateItem(itemIndex, info);
        }
        TxtFileOperator.writeJsonToFile(this.filterList, getApplicationContext(), TxtFileOperator.FILTER_FILE_NAME);
        this.setSetBtnEnable();

        // 入力フィールドのクリア
        etNumber.setText("");
        etData.setText("");
        etOffset.setText("");
        etLength.setText("");
        spMemoryBank.setSelection(0);
        spAction.setSelection(0);
        spTarget.setSelection(0);

        btnAddFilter.setText("追加");
        itemIndex = -1;
    }

    public void initInput() {
        // 入力フィールドのクリア
        etNumber.setText("");
        etData.setText("");
        etOffset.setText("");
        etLength.setText("");
        spMemoryBank.setSelection(0);
        spAction.setSelection(0);
        spTarget.setSelection(0);

        btnAddFilter.setText("追加");
        itemIndex = -1;
    }

    // PreFiltersの適用
    private void applyPreFilters() {

        // SettingsActivityで接続されたリーダーを取得
        RFIDReader reader = RFIDReaderManager.getInstance().getReader();

        if (reader == null || !reader.isConnected()) {
            Toast.makeText(this, "リーダーが接続されていません", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // PreFiltersをクリア
            reader.Actions.PreFilters.deleteAll();

            // 各フィルタをリーダーのPreFiltersに設定
            for (FilterInfo filter : filterList) {
                // フィルタ情報をパース
                String data = filter.getFilterData();
                String memoryBankIndex = String.valueOf(filter.getFilterMemoryBankSelection());
                int offset = Integer.parseInt(filter.getFilterOffset());
                int length = Integer.parseInt(filter.getFilterLength());
                String actionIndex = String.valueOf(filter.getFilterActionSelection());
                String targetIndex = String.valueOf(filter.getFilterTargetSelection());

                // PreFilterオブジェクトの作成
                PreFilters preFilters = new PreFilters();
                PreFilters.PreFilter preFilter = null;
                preFilter = preFilters.new PreFilter();
                preFilter.setTagPattern(data);
                MEMORY_BANK memoryBank = UtilsZebra.getMemoryBankEnum(memoryBankIndex);
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

            Toast.makeText(this, "プリフィルタが設定されました", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(this, "プリフィルタの設定に失敗しました: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void setSetBtnEnable() {
        btnSetFilter.setEnabled(!this.filterList.isEmpty());
    }

}
