package com.example.ebskk.widget;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSON;
import com.example.ebskk.R;
import com.example.ebskk.adapter.RegionListAdapter;
import com.example.ebskk.bean.AjaxResult;
import com.example.ebskk.bean.MgRegion;
import com.example.ebskk.controller.RegionController;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.widget.QMUIEmptyView;

import java.util.ArrayList;
import java.util.List;

public class VNItemDialog extends Dialog implements View.OnClickListener {
    private ImageView imageClose;
    private ListView itemListView;
    private QMUIEmptyView emptyView;
    private Context context;

    private List<MgRegion> itemList;
    private RegionListAdapter itemAdapter;

    public VNItemDialog(@NonNull Context context) {
        super(context);
        this.context = context;

        itemList = new ArrayList<>();
        itemAdapter = new RegionListAdapter(context, itemList);
        itemAdapter.setItemClickListener(new itemClick());

        init();
    }

    private void init(){
        setContentView(R.layout.dialog_item_selector);
        Window dialogWindow = this.getWindow();
        dialogWindow.setBackgroundDrawableResource(R.color.transparent);    //背景透明
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.BOTTOM);                        //布局位置是底部
        lp.width = QMUIDisplayHelper.getScreenWidth(context);           //宽度
        lp.height = QMUIDisplayHelper.getScreenHeight(context)/10*9;    //高度
        dialogWindow.setAttributes(lp);

        imageClose = findViewById(R.id.imageClose);
        imageClose.setOnClickListener(this::onClick);

        itemListView = findViewById(R.id.storeListView);
        itemListView.setAdapter(itemAdapter);

        emptyView = findViewById(R.id.emptyView);

        loadList();
    }

    private void loadList() {
        emptyView.setVisibility(View.VISIBLE);
        new RegionController(){

            @Override
            public void onResult(AjaxResult result) {
                if (!result.isSuccess()){
                    emptyView.setLoadingShowing(false);
                    emptyView.setDetailText("ロードに失敗しました");
                    emptyView.setButton("もう一度ロード", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            loadList();
                        }
                    });
                    return;
                }
                List<MgRegion> temp = JSON.parseArray(JSON.toJSONString(result.getData()),MgRegion.class);
                Log.i("abc","temp" + JSON.toJSONString(temp));
                if (temp==null || temp.size()<1) return;

                //把第一条数据设置上去
                MgRegion entity = temp.get(0);
                entity.setSelection(true);

                itemList.clear();
                itemList.addAll(temp);
                itemAdapter.notifyDataSetChanged();    //通知界面更新

            }
        }.regionList(null,"");
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.imageClose:
                dismiss();
                break;
        }
    }

    //保管場所-倉庫 列表点击事件
    private class itemClick implements RegionListAdapter.RegionListItemClickListener {

        @Override
        public void onRegionListItemClick(MgRegion entity) {
            for(MgRegion mgRegion: itemList) {
                mgRegion.setSelection(false);
            }
            entity.setSelection(true);
            itemAdapter.notifyDataSetChanged();    //更新UI界面
        }
    }

    private RegionDialogResultListener regionDialogResultListener;
    public void setRegionDialogResultListener(RegionDialogResultListener regionDialogResultListener){
        this.regionDialogResultListener = regionDialogResultListener;
    }
    public interface RegionDialogResultListener{
        void onRegionDialogResultListener(String code,String name);
    }
}
