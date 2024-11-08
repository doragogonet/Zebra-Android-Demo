package com.zebra.demo.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.zebra.demo.R;
import com.zebra.demo.base.BarChartView;
import com.zebra.demo.tools.FilterInfo;
import com.zebra.demo.tools.UtilsZebra;
import com.zebra.rfid.api3.FILTER_ACTION;
import com.zebra.rfid.api3.MEMORY_BANK;
import com.zebra.rfid.api3.PreFilters;
import com.zebra.rfid.api3.RFIDReader;
import com.zebra.rfid.api3.STATE_AWARE_ACTION;
import com.zebra.rfid.api3.TARGET;

import java.util.ArrayList;
import java.util.HashMap;

public class FilterSettingsActivity extends BaseActivity {

    private EditText etNumber, etData, etOffset, etLength;
    private Spinner spMemoryBank, spAction, spTarget;
    private Button btnAddFilter;
    private ListView lvFilters;
    public ArrayList<FilterInfo> filterList = new ArrayList<>();   // フィルタ情報のリスト
    private MyAdapter filterAdapter;
    private int memoryBankSelection;
    private int actionSelection;
    private int targetBankSelection;
    private int itemIndex = -1;
    private String[] memoryBankArr;
    private String[] actionArr;
    private String[] targetArr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_settings);

        memoryBankArr = getResources().getStringArray(R.array.memory_bank_options);
        actionArr = getResources().getStringArray(R.array.action_options);
        targetArr = getResources().getStringArray(R.array.target_options);

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

        // フィルタリストの初期化
        filterAdapter = new MyAdapter(getApplicationContext());
        lvFilters.setAdapter(filterAdapter);

        // 追加ボタンのクリックイベント
        btnAddFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFilter();
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
                spMemoryBank.setSelection(Integer.parseInt(info.getFilterMemoryBank()));
                etOffset.setText(info.getFilterOffset());
                etLength.setText(info.getFilterLength());
                spAction.setSelection(Integer.parseInt(info.getFilterAction()));
                spTarget.setSelection(Integer.parseInt(info.getFilterTarget()));
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
        String filter = "番号: " + number + ", データ: " + data + ", メモリバンク: " + this.memoryBankSelection +
                        ", オフセット: " + offset + ", 長さ: " + length +
                        ", アクション: " + this.actionSelection + ", ターゲット: " + this.targetBankSelection;

        if (itemIndex == -1) {
            FilterInfo info = new FilterInfo();
            info.setFilterNumber(number);
            info.setFilterData(data);
            info.setFilterMemoryBank(this.getMemoryBankLabel(this.memoryBankSelection));
            info.setFilterOffset(offset);
            info.setFilterLength(length);
            info.setFilterAction(this.getActionLabel(this.actionSelection));
            info.setFilterTarget(this.getTargetLabel(this.targetBankSelection));
            this.filterList.add(info);
        } else {
            FilterInfo info = this.filterList.get(itemIndex);
            info.setFilterNumber(number);
            info.setFilterData(data);
            info.setFilterMemoryBank(this.getMemoryBankLabel(this.memoryBankSelection));
            info.setFilterOffset(offset);
            info.setFilterLength(length);
            info.setFilterAction(this.getActionLabel(this.actionSelection));
            info.setFilterTarget(this.getTargetLabel(this.targetBankSelection));
        }
        filterAdapter.notifyDataSetChanged();

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
                String memoryBankIndex = filter.getFilterMemoryBank();
                int offset = Integer.parseInt(filter.getFilterOffset());
                int length = Integer.parseInt(filter.getFilterLength());
                String actionIndex = filter.getFilterAction();
                String targetIndex = filter.getFilterTarget();

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

    public final class ViewHolder {
        public TextView tvFilterNumber;
        public TextView tvFilterData;
        public TextView tvFilterMemoryBank;
        public TextView tvFilterOffset;
        public TextView tvFilterLength;
        public TextView tvFilterAction;
        public TextView tvFilterTarget;
        public TextView tvFilterDelete;
        public TextView tvFilterItemId;
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
            return filterList.size();
        }
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return filterList.get(arg0);
        }
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return arg0;
        }
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.filter_listtag_items, null);
                holder.tvFilterItemId = (TextView) convertView.findViewById(R.id.TvFilterItemId);
                holder.tvFilterNumber = (TextView) convertView.findViewById(R.id.TvFilterNumber);
                holder.tvFilterData = (TextView) convertView.findViewById(R.id.TvFilterData);
                holder.tvFilterMemoryBank = (TextView) convertView.findViewById(R.id.TvFilterMemoryBank);
                holder.tvFilterOffset = (TextView) convertView.findViewById(R.id.TvFilterOffset);
                holder.tvFilterLength = (TextView) convertView.findViewById(R.id.TvFilterLength);
                holder.tvFilterAction = (TextView) convertView.findViewById(R.id.TvFilterAction);
                holder.tvFilterTarget = (TextView) convertView.findViewById(R.id.TvFilterTarget);
                holder.tvFilterDelete = (TextView) convertView.findViewById(R.id.TvFilterDelete);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.tvFilterItemId.setText(String.valueOf((position + 1)));
            holder.tvFilterNumber.setText(filterList.get(position).getFilterNumber());
            holder.tvFilterData.setText(filterList.get(position).getFilterData());
            holder.tvFilterMemoryBank.setText(filterList.get(position).getFilterMemoryBank());
            holder.tvFilterOffset.setText(filterList.get(position).getFilterOffset());
            holder.tvFilterLength.setText(filterList.get(position).getFilterLength());
            holder.tvFilterAction.setText(filterList.get(position).getFilterAction());
            holder.tvFilterTarget.setText(filterList.get(position).getFilterTarget());

            holder.tvFilterDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    filterList.remove(position);
                    notifyDataSetChanged();
                }
            });

            return convertView;
        }

    }

    private String getMemoryBankLabel(int index) {
        String label = "";

        if (index < memoryBankArr.length) {
            label = memoryBankArr[index];
        }

        return label;
    }

    private String getActionLabel(int index) {
        String label = "";

        if (index < actionArr.length) {
            label = actionArr[index];
        }

        return label;
    }

    private String getTargetLabel(int index) {
        String label = "";

        if (index < targetArr.length) {
            label = targetArr[index];
        }

        return label;
    }
}
