package com.zebra.demo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.zebra.demo.R;
import com.zebra.demo.activity.FilterSettingsActivity;
import com.zebra.demo.bean.HistoryData;
import com.zebra.demo.tools.FilterInfo;
import com.zebra.demo.tools.TxtFileOperator;
import com.zebra.demo.view.BarChartView;

import java.util.ArrayList;
import java.util.List;

public class FilterDataAdapter extends ArrayAdapter<FilterInfo> {
    final static String TAG = "ZEBRA-DEMO";

    public List<FilterInfo> filterList;
    private LayoutInflater inflater;

    private FilterSettingsActivity context;

    public FilterDataAdapter(FilterSettingsActivity context, List<FilterInfo> filterList) {
        super(context, 0, filterList);
        this.context = context;
        this.filterList = filterList;
        this.inflater = LayoutInflater.from(context);
    }

    // ViewHolderパターンの定義
    private static class ViewHolder {
        TextView tvFilterNumber;
        TextView tvFilterData;
        TextView tvFilterMemoryBank;
        TextView tvFilterOffset;
        TextView tvFilterLength;
        TextView tvFilterAction;
        TextView tvFilterTarget;
        TextView tvFilterDelete;
        TextView tvFilterItemId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        // ビューの再利用を確認
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.filter_listtag_items, parent, false);

            // ViewHolderの設定
            holder = new ViewHolder();
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
            // 既存のViewHolderを再利用
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
                TxtFileOperator.writeJsonToFile(filterList, context, TxtFileOperator.FILTER_FILE_NAME);
                context.setSetBtnEnable();
                context.initInput();
            }
        });

        return convertView;
    }

    // 特定のアイテムを更新するメソッド
    public void updateItem(int position, FilterInfo data) {
        filterList.set(position, data);
        notifyDataSetChanged();
    }

    // 新しいアイテムを追加するメソッド
    public void addItem(FilterInfo data) {
        filterList.add(data);
        notifyDataSetChanged();
    }

    public void clearFilterList() {
        filterList.clear();
        notifyDataSetChanged();
    }
}
