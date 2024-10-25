package com.example.ebskk.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.ebskk.R;
import com.example.ebskk.bean.MgRegion;

import java.util.List;

public class SpinnerAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private List<MgRegion> list;
    private Context context;
    public SpinnerAdapter(Context context, List<MgRegion> list) {
        this.inflater = LayoutInflater.from(context);
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        if (list != null) return list.size();
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (list != null) return list.get(position);
        return null;
    }

    @Override
    public long getItemId(int position) {
        if (list != null) return list.size();
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView==null) {
            holder = new ViewHolder();

            //加载布局
            convertView = inflater.inflate(R.layout.mm_spinner_item,parent,false);


            //获取控件
            holder.itemText = convertView.findViewById(R.id.spinnerName);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        MgRegion entity = list.get(position);
        holder.itemText.setText(entity.getName());
        holder.itemText.setTextColor(entity.isSelection()?
                context.getResources().getColor(R.color.primary):context.getResources().getColor(R.color.dark));

        return convertView;
    }

    private class ViewHolder {
        TextView itemText;
    }

}
