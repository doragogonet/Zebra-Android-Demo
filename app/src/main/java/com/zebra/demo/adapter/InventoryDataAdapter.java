package com.zebra.demo.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.zebra.demo.R;
import com.zebra.demo.tools.StringUtils;
import com.zebra.demo.view.BarChartView;
import com.zebra.demo.bean.HistoryData;

import java.util.List;

public class InventoryDataAdapter extends ArrayAdapter<HistoryData> {
    final static String TAG = "ZEBRA-DEMO";

    private List<HistoryData> historyDatas;
    private LayoutInflater inflater;

    public InventoryDataAdapter(Context context, List<HistoryData> historyDatas) {
        super(context, 0, historyDatas);
        this.historyDatas = historyDatas;
        this.inflater = LayoutInflater.from(context);
    }

    // ViewHolderパターンの定義
    private static class ViewHolder {
        TextView tvItemId;
        TextView tvTagEPC;
        TextView tvTagCount;
        BarChartView tvTagRssi;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        // ビューの再利用を確認
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.inventory_listtag_items, parent, false);

            // ViewHolderの設定
            viewHolder = new ViewHolder();
            viewHolder.tvItemId = convertView.findViewById(R.id.TvInventoryItemId);
            viewHolder.tvTagEPC = convertView.findViewById(R.id.TvInventoryTagEPC);
            viewHolder.tvTagCount = convertView.findViewById(R.id.TvInventoryTagCount);
            viewHolder.tvTagRssi = convertView.findViewById(R.id.TvInventoryTagRssi);

            convertView.setTag(viewHolder);
        } else {
            // 既存のViewHolderを再利用
            viewHolder = (ViewHolder) convertView.getTag();
        }

        try {
            // データを設定
            HistoryData data = historyDatas.get(position);
            viewHolder.tvItemId.setText(String.valueOf((position + 1)));
            viewHolder.tvTagEPC.setText(data.getTagID());
            viewHolder.tvTagCount.setText(data.getTagSeenCount());
            int rssi = 100;
            String rssiStr = "0";
            if(StringUtils.isNotEmpty(data.getPeakRSSI())) {
                rssi = ((int) Float.parseFloat(data.getPeakRSSI()) * -1);
                rssiStr = data.getPeakRSSI();
            }
            viewHolder.tvTagRssi.setData((100 - rssi) * 5, rssiStr);
        } catch (Exception ex) {
            Log.d(TAG, ex.getMessage());
        }

        return convertView;
    }

    // 特定のアイテムを更新するメソッド
    public void updateItem(int position, HistoryData data) {
        historyDatas.set(position, data);
        notifyDataSetChanged();
    }

    // 新しいアイテムを追加するメソッド
    public void addItem(HistoryData data) {
        historyDatas.add(data);
        notifyDataSetChanged();
    }

    public void clearHistoryDatas() {
        historyDatas.clear();
        notifyDataSetChanged();
    }
}
