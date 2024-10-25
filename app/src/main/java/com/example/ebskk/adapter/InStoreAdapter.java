package com.example.ebskk.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.ebskk.R;
import com.example.ebskk.bean.InStore;
import com.example.ebskk.bean.MgRegion;

import java.util.List;

public class InStoreAdapter extends BaseAdapter {
    private List<InStore> list;
    private Context context;
    private LayoutInflater inflater;

    public InStoreAdapter(Context context, List<InStore> list) {
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
        InStoreAdapter.ViewHolder holder;
        if (convertView==null) {
            //加载布局
            convertView = inflater.inflate(R.layout.item_list,null);
            holder = new InStoreAdapter.ViewHolder();
            //获取控件
            holder.itemName = convertView.findViewById(R.id.itemName);
            holder.individualID = convertView.findViewById(R.id.individualID);
            holder.locationID = convertView.findViewById(R.id.locationID);
            holder.quantity = convertView.findViewById(R.id.quantity);

            convertView.setTag(holder);
        } else {
            holder = (InStoreAdapter.ViewHolder) convertView.getTag();
        }

        InStore entity = list.get(position);
        holder.itemName.setText(entity.getItemName());
        holder.individualID.setText(entity.getIndividualID());
        holder.locationID.setText(entity.getLocationID());
        holder.quantity.setText(String.valueOf(entity.getQuantity()));

        holder.locationID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (locationIDClickListener!=null){
                    locationIDClickListener.onLocationIDClick(entity,position);
                }
            }
        });

        return convertView;
    }

    private class ViewHolder {
//        RelativeLayout itemLayout;
        TextView itemName;
        TextView individualID;
        Button locationID;
        EditText quantity;
    }

    private InStoreAdapter.LocationIDClickListener locationIDClickListener;
    public void setLocationIDClickListener(InStoreAdapter.LocationIDClickListener locationIDClickListener) {
        this.locationIDClickListener = locationIDClickListener;
    }

    public interface LocationIDClickListener {
        void onLocationIDClick(InStore entity,int position);
    }
}
