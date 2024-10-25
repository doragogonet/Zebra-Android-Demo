package com.zebra.demo.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.zebra.demo.R;
import com.zebra.demo.tools.UtilsZebra;
import com.zebra.rfid.api3.FILTER_ACTION;
import com.zebra.rfid.api3.MEMORY_BANK;
import com.zebra.rfid.api3.PreFilters;
import com.zebra.rfid.api3.RFIDReader;
import com.zebra.rfid.api3.STATE_AWARE_ACTION;
import com.zebra.rfid.api3.TARGET;

import java.util.ArrayList;

public class FilterSettingsActivity extends AppCompatActivity {

    private EditText etNumber, etData, etOffset, etLength;
    private Spinner spMemoryBank, spAction, spTarget;
    private Button btnAddFilter;
    private ListView lvFilters;
	private ArrayList<String> filterList;  // フィルタ情報のリスト
    private ArrayAdapter<String> filterAdapter;

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
        lvFilters = findViewById(R.id.lvFilters);

		filterList = new ArrayList<>();
        // フィルタリストの初期化
        filterAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, filterList);
        lvFilters.setAdapter(filterAdapter);

        // 追加ボタンのクリックイベント
        btnAddFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFilter();
				applyPreFilters();
            }
        });

        // リスト項目の長押しで削除
        lvFilters.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                filterList.remove(position);
                return true;
            }
        });
    }

    // フィルタの追加
    private void addFilter() {
        String number = etNumber.getText().toString().trim();
        String data = etData.getText().toString().trim();
        String offset = etOffset.getText().toString().trim();
        String length = etLength.getText().toString().trim();
        String memoryBank = spMemoryBank.getSelectedItem().toString();
        String action = spAction.getSelectedItem().toString();
        String target = spTarget.getSelectedItem().toString();

        if (number.isEmpty() || data.isEmpty() || offset.isEmpty() || length.isEmpty()) {
            Toast.makeText(this, "すべてのフィールドを入力してください", Toast.LENGTH_SHORT).show();
            return;
        }

        // フィルタ情報の追加
        String filter = "番号: " + number + ", データ: " + data + ", メモリバンク: " + memoryBank +
                        ", オフセット: " + offset + ", 長さ: " + length +
                        ", アクション: " + action + ", ターゲット: " + target;
		
		filterList.add(filter);
        filterAdapter.notifyDataSetChanged();
		

        // 入力フィールドのクリア
        etNumber.setText("");
        etData.setText("");
        etOffset.setText("");
        etLength.setText("");
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
            for (String filter : filterList) {
                // フィルタ情報をパース
                String[] parts = filter.split(", ");
                String data = parts[1].split(": ")[1];
                String memoryBankIndex = parts[2].split(": ")[1];
                int offset = Integer.parseInt(parts[3].split(": ")[1]);
                int length = Integer.parseInt(parts[4].split(": ")[1]);
                String actionIndex = parts[5].split(": ")[1];
                String targetIndex = parts[6].split(": ")[1];

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


}
