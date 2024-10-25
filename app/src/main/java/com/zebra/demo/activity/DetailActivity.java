package com.zebra.demo.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.content.Intent;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import com.zebra.demo.R;

import java.util.HashMap;

public class DetailActivity extends FragmentActivity {
    public static final String TAG_EPC = "tagEPC";
    public static final String TAG_EPC_TID = "tagEpcTID";
    public static final String TAG_COUNT = "tagCount";
    public static final String TAG_RSSI = "tagRssi";

    public static final String TAG_PRO_CODE = "tagProCode";
    public static final String TAG_PRO_NAME = "tagProName";
    public static final String TAG_PRO_MODEL = "tagProModel";
    public static final String TAG_PRO_COUNT = "tagProCount";
    public static final String TAG_PRO_BATCH = "tagProBatch";
    public static final String TAG_PRO_TYPE = "tagProType";
    public static final String TAG_PRO_CREATE_TIME = "tagProCreateTime";
    public static final String TAG_PRO_CREATE_COMPANY = "tagProCreateCompany";
    public static final String TAG_PRO_BOX = "tagProBox";

    public static final int VISIBLE = 0;        
    public static final int INVISIBILITY = 4;   
    public static final int GONE = 8;           
    public static final String LABEL_NAME1 = "";
    public static final String LABEL_NAME2 = "";

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        HashMap map = (HashMap) intent.getSerializableExtra("map");

        String proName =  map.get(TAG_PRO_NAME).toString();
        boolean isVisble = !(proName.indexOf(LABEL_NAME1) > -1 || proName.indexOf(LABEL_NAME2) > -1);

        //标签编号
        TextView tvId = (TextView) findViewById(R.id.TvEpc);
        tvId.setText("标签编号：" + map.get(TAG_EPC).toString());

        //号型代码
        TextView tvCode = (TextView) findViewById(R.id.TvProCode);
        TextView tvCodeLine = (TextView) findViewById(R.id.TvProCodeLine);
        tvCode.setText("号型代码：" + map.get(TAG_PRO_CODE).toString());
        if (!isVisble) {
            tvCode.setVisibility(GONE);
            tvCodeLine.setVisibility(GONE);
        }

        //物资名称
        TextView tvName = (TextView) findViewById(R.id.TvProName);
        tvName.setText("物资名称：" + map.get(TAG_PRO_NAME).toString());

        //物资号型
        TextView tvModel = (TextView) findViewById(R.id.TvProModel);
        TextView tvModelLine = (TextView) findViewById(R.id.TvProModelLine);
        tvModel.setText("物资号型：" + map.get(TAG_PRO_MODEL).toString());
        if (!isVisble) {
            tvModel.setVisibility(GONE);
            tvModelLine.setVisibility(GONE);
        }

        //装箱数量
        TextView tvCount = (TextView) findViewById(R.id.TvProCount);
        TextView tvCountLine = (TextView) findViewById(R.id.TvProCountLine);
        tvCount.setText("装箱数量：" + map.get(TAG_PRO_COUNT).toString());
        if (!isVisble) {
            tvCount.setVisibility(GONE);
            tvCountLine.setVisibility(GONE);
        }

        //批次编号
        TextView tvBatch = (TextView) findViewById(R.id.TvProBatch);
        TextView tvBatchLine = (TextView) findViewById(R.id.TvProBatchLine);
        tvBatch.setText("批次编号：" + map.get(TAG_PRO_BATCH).toString());
        if (!isVisble) {
            tvBatch.setVisibility(GONE);
            tvBatchLine.setVisibility(GONE);
        }

        //种类
        TextView tvType= (TextView) findViewById(R.id.TvProType);
        TextView tvTypeLine= (TextView) findViewById(R.id.TvProTypeLine);
        tvType.setText("标签形式：" + map.get(TAG_PRO_TYPE).toString());
        if (!isVisble) {
            tvType.setVisibility(GONE);
            tvTypeLine.setVisibility(GONE);
        }

        //生产日期
        TextView tvCreateTime= (TextView) findViewById(R.id.TvProCreateTime);
        TextView tvCreateTimeLine= (TextView) findViewById(R.id.TvProCreateTimeLine);
        tvCreateTime.setText("生产日期：" + map.get(TAG_PRO_CREATE_TIME).toString());
        if (!isVisble) {
            tvCreateTime.setVisibility(GONE);
            tvCreateTimeLine.setVisibility(GONE);
        }

        //生产企业
        TextView tvCreateCompany= (TextView) findViewById(R.id.TvProCreateCompany);
        TextView tvCreateCompanyLine= (TextView) findViewById(R.id.TvProCreateCompanyLine);
        tvCreateCompany.setText("生产企业：" + map.get(TAG_PRO_CREATE_COMPANY).toString());
        if (!isVisble) {
            tvCreateCompany.setVisibility(GONE);
            tvCreateCompanyLine.setVisibility(GONE);
        }

        //箱
        TextView tvBox= (TextView) findViewById(R.id.TvProBox);
        TextView tvBoxLine= (TextView) findViewById(R.id.TvProBoxLine);
        tvBox.setText("箱　　号：" + map.get(TAG_PRO_BOX).toString());
        if (!isVisble) {
            tvBox.setVisibility(GONE);
            tvBoxLine.setVisibility(GONE);
        }
    }
}