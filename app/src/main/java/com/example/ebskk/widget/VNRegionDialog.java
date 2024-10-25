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

public class VNRegionDialog extends Dialog implements View.OnClickListener {
    private ImageView imageClose;
    private VNLineTextView textStore,textRack,textCell;
    private ListView storeListView,rackListView,cellListView;
    private QMUIEmptyView emptyView;
    private Context context;

    private List<MgRegion> storeList;
    private RegionListAdapter storeAdapter;
    private List<MgRegion> rackList;
    private RegionListAdapter rackAdapter;
    private List<MgRegion> cellList;
    private RegionListAdapter cellAdapter;


    public VNRegionDialog(@NonNull Context context) {
        super(context);
        this.context = context;

        storeList = new ArrayList<>();
        rackList = new ArrayList<>();
        cellList = new ArrayList<>();

        storeAdapter = new RegionListAdapter(context,storeList);
        storeAdapter.setItemClickListener(new StoreItemClick());
        rackAdapter = new RegionListAdapter(context,rackList);
        rackAdapter.setItemClickListener(new RackItemClick());
        cellAdapter = new RegionListAdapter(context,cellList);
        cellAdapter.setItemClickListener(new CellItemClick());

        init();
    }

    private void init(){
        setContentView(R.layout.dialog_region_selector);
        Window dialogWindow = this.getWindow();
        dialogWindow.setBackgroundDrawableResource(R.color.transparent);    //背景透明
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.BOTTOM);                        //布局位置是底部
        lp.width = QMUIDisplayHelper.getScreenWidth(context);           //宽度
        lp.height = QMUIDisplayHelper.getScreenHeight(context)/10*9;    //高度
        dialogWindow.setAttributes(lp);

        imageClose = findViewById(R.id.imageClose);
        imageClose.setOnClickListener(this::onClick);
        textStore = findViewById(R.id.textStore);
        textStore.setOnClickListener(this::onClick);
        textRack = findViewById(R.id.textRack);
        textRack.setOnClickListener(this::onClick);
        textCell = findViewById(R.id.textCell);
        textCell.setOnClickListener(this::onClick);

        storeListView = findViewById(R.id.storeListView);
        storeListView.setAdapter(storeAdapter);
        rackListView = findViewById(R.id.rackListView);
        rackListView.setAdapter(rackAdapter);
        cellListView = findViewById(R.id.cellListView);
        cellListView.setAdapter(cellAdapter);
        emptyView = findViewById(R.id.emptyView);

        loadStoreList();
    }

    private void loadStoreList() {
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
                            loadStoreList();
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
                textStore.setText(entity.getName());
                textStore.setTag(entity.getCode());
                storeList.clear();
                storeList.addAll(temp);
                storeAdapter.notifyDataSetChanged();    //通知界面更新

                //展示数据
                switchTabHeader(0);
                switchTabContent(0);


            }
        }.regionList(null,"");
    }

    private void loadRackList() {
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
                            loadRackList();
                        }
                    });
                    return;
                }
                List<MgRegion> temp = JSON.parseArray(JSON.toJSONString(result.getData()),MgRegion.class);
                Log.i("abc","temp" + JSON.toJSONString(temp));
                if (temp==null || temp.size()<1) return;
//                Log.i("abc","onPostExecute" + s);

                //把第一条数据设置上去
                MgRegion entity = temp.get(0);
                entity.setSelection(true);
                textRack.setText(entity.getName());
                textRack.setTag(entity.getCode());
                rackList.clear();
                rackList.addAll(temp);
                rackAdapter.notifyDataSetChanged();    //通知界面更新

                //展示数据
                switchTabHeader(1);
                switchTabContent(1);

            }
        }.regionList(null,String.valueOf(textStore.getTag()));
    }

    private void loadCellList() {
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
                            loadCellList();
                        }
                    });
                    return;
                }
                List<MgRegion> temp = JSON.parseArray(JSON.toJSONString(result.getData()),MgRegion.class);
                Log.i("abc","loadCellList" + JSON.toJSONString(temp));
                if (temp==null || temp.size()<1) return;
//                Log.i("abc","onPostExecute" + s);

                //把第一条数据设置上去
                MgRegion entity = temp.get(0);
                entity.setSelection(true);
                textCell.setText(entity.getName());
                textCell.setTag(entity.getCode());
                cellList.clear();
                cellList.addAll(temp);
                cellAdapter.notifyDataSetChanged();    //通知界面更新

                //展示数据
                switchTabHeader(2);
                switchTabContent(2);

            }
        }.regionList(null,String.valueOf(textRack.getTag()));
    }

    //切换Tab标题
    private void switchTabHeader(int position) {
        //设置默认颜色
        textStore.setTextColor(context.getResources().getColor(R.color.colorDefault));
        textRack.setTextColor(context.getResources().getColor(R.color.colorDefault));
        textCell.setTextColor(context.getResources().getColor(R.color.colorDefault));

        switch (position) {
            case 0:
                textStore.setTextColor(context.getResources().getColor(R.color.primary));
                break;
            case 1:
                textRack.setTextColor(context.getResources().getColor(R.color.primary));
                break;
            case 2:
                textCell.setTextColor(context.getResources().getColor(R.color.primary));
                break;
        }
    }

    //切换Tab内容
    private void switchTabContent(int position) {

        emptyView.setVisibility(View.GONE);     //隐藏
        emptyView.setLoadingShowing(true);
        emptyView.setDetailText("ロード中...");

        storeListView.setVisibility(View.GONE); //隐藏
        rackListView.setVisibility(View.GONE);  //隐藏
        cellListView.setVisibility(View.GONE);  //隐藏

        switch (position) {
            case 0:
                storeListView.setVisibility(View.VISIBLE);  //显示
                break;
            case 1:
                rackListView.setVisibility(View.VISIBLE);   //显示
                break;
            case 2:
                cellListView.setVisibility(View.VISIBLE);   //显示
                break;
        }
    }



    @Override
    public void onClick(View v) {

        int id = v.getId();
        switch (id) {
            case R.id.imageClose:
                dismiss();
                break;
            case R.id.textStore:
                switchTabHeader(0);
                switchTabContent(0);
                break;
            case R.id.textRack:
                switchTabHeader(1);
                switchTabContent(1);
                break;
            case R.id.textCell:
                switchTabHeader(2);
                switchTabContent(2);
                break;
        }

    }

    //保管場所-倉庫 列表点击事件
    private class StoreItemClick implements RegionListAdapter.RegionListItemClickListener {

        @Override
        public void onRegionListItemClick(MgRegion entity) {
            for(MgRegion mgRegion: storeList) {
                mgRegion.setSelection(false);
            }
            entity.setSelection(true);

            textStore.setTag(entity.getCode());
            textStore.setText(entity.getName());
            storeAdapter.notifyDataSetChanged();    //更新UI界面
            loadRackList();
        }
    }

    //保管場所-ラック 列表点击事件
    private class RackItemClick implements RegionListAdapter.RegionListItemClickListener {

        @Override
        public void onRegionListItemClick(MgRegion entity) {
            for(MgRegion mgRegion: rackList) {
                mgRegion.setSelection(false);
            }
            entity.setSelection(true);

            textRack.setTag(entity.getCode());
            Log.i("abc","textRack.setTag" +entity.getCode());
            textRack.setText(entity.getName());
            rackAdapter.notifyDataSetChanged();    //更新UI界面
            loadCellList();
        }
    }

    //保管場所-段数-間口 列表点击事件
    private class CellItemClick implements RegionListAdapter.RegionListItemClickListener {

        @Override
        public void onRegionListItemClick(MgRegion entity) {
            for(MgRegion mgRegion: cellList) {
                mgRegion.setSelection(false);
            }
            entity.setSelection(true);

            textCell.setTag(entity.getCode());
            textCell.setText(entity.getName());
            cellAdapter.notifyDataSetChanged();    //更新UI界面

            if (regionDialogResultListener!=null) {
                String code = entity.getCode();
                String name = textStore.getText().toString() + textRack.getText().toString() + entity.getName();
                regionDialogResultListener.onRegionDialogResultListener(code,name);
                dismiss();
            }
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
