package com.example.ebskk.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.example.ebskk.R;
import com.example.ebskk.UhfInfo;
import com.example.ebskk.activity.DetailActivity;
import com.example.ebskk.activity.UHFMainActivity;
import com.example.ebskk.tools.EpcData;
import com.example.ebskk.tools.EpcFormat;
import com.example.ebskk.tools.NumberTool;
import com.example.ebskk.tools.StringUtils;
import com.example.ebskk.tools.UIHelper;

import com.rscja.deviceapi.RFIDWithUHFUART;
import com.rscja.deviceapi.entity.UHFTAGInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class UHFReadTagFragment extends KeyDwonFragment {
    private static final String TAG = "UHFReadTagFragment";
    private boolean loopFlag = false;
    private int inventoryFlag = 1;
    private List<String> tempDatas = new ArrayList<>();
    MyAdapter adapter;
    Button BtClear;
    TextView tvTime;
    TextView tv_count;
    TextView tv_total;
    RadioGroup RgInventory;
    RadioButton RbInventorySingle;
    RadioButton RbInventoryLoop;

    Button BtInventory;
    ListView LvTags;
    private UHFMainActivity mContext;
    private HashMap<String, String> map;

    private int total;
    private long time;

    private CheckBox cbFilter;
    private ViewGroup layout_filter;

    public static final String TAG_EPC = "tagEPC";
    public static final String TAG_EPC_TID = "tagEpcTID";
    public static final String TAG_COUNT = "tagCount";
    public static final String TAG_RSSI = "tagRssi";

    //sxt add start
    public static final String TAG_PRO_CODE = "tagProCode";
    public static final String TAG_PRO_NAME = "tagProName";
    public static final String TAG_PRO_MODEL = "tagProModel";
    public static final String TAG_PRO_COUNT = "tagProCount";
    public static final String TAG_PRO_BATCH = "tagProBatch";
    public static final String TAG_PRO_TYPE = "tagProType";
    public static final String TAG_PRO_CREATE_TIME = "tagProCreateTime";
    public static final String TAG_PRO_CREATE_COMPANY = "tagProCreateCompany";
    public static final String TAG_PRO_BOX = "tagProBox";
    //sxt add end

    private CheckBox cbEPC_Tam;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            UHFTAGInfo info = (UHFTAGInfo) msg.obj;
            addDataToList(info.getEPC(),mergeTidEpc(info.getTid(), info.getEPC(),info.getUser()), info.getRssi());
            setTotalTime();
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.i(TAG, "UHFReadTagFragment.onCreateView");
        return inflater.inflate(R.layout.uhf_readtag_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.i(TAG, "UHFReadTagFragment.onActivityCreated");
        super.onActivityCreated(savedInstanceState);
        mContext = (UHFMainActivity) getActivity();
        mContext.currentFragment=this;
        BtClear = (Button) getView().findViewById(R.id.BtClear);
        tvTime = (TextView) getView().findViewById(R.id.tvTime);
        tvTime.setText("0s");
        tv_count = (TextView) getView().findViewById(R.id.tv_count);
        tv_total = (TextView) getView().findViewById(R.id.tv_total);
        RgInventory = (RadioGroup) getView().findViewById(R.id.RgInventory);
        RbInventorySingle = (RadioButton) getView().findViewById(R.id.RbInventorySingle);
        RbInventoryLoop = (RadioButton) getView().findViewById(R.id.RbInventoryLoop);

        //sxt add start 隐藏
        RbInventorySingle.setVisibility(View.INVISIBLE);
        RbInventoryLoop.setVisibility(View.INVISIBLE);
        //sxt add end

        BtInventory = (Button) getView().findViewById(R.id.BtInventory);

        LvTags = (ListView) getView().findViewById(R.id.LvTags);
        adapter=new MyAdapter(mContext);
        LvTags.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.setSelectItem(position);
                adapter.notifyDataSetInvalidated();

                //sxt add start
                // 通过Intent跳转至新的页面
                Intent intent = new Intent(getActivity(), DetailActivity.class);
//                int index = mContext.uhfInfo.getSelectIndex();
                intent.putExtra("map",mContext.tagList.get(position));
                getActivity().startActivity(intent);

                //sxt add end

            }
        });
        LvTags.setAdapter(adapter);

        BtClear.setOnClickListener(new BtClearClickListener());
        RgInventory.setOnCheckedChangeListener(new RgInventoryCheckedListener());
        BtInventory.setOnClickListener(new BtInventoryClickListener());

        initFilter(getView());

        initEPCTamperAlarm(getView());
        //clearData();
        tv_count.setText(mContext.tagList.size()+"");
        tv_total.setText(total+"");
        Log.i(TAG, "UHFReadTagFragment.EtCountOfTags=" + tv_count.getText());
    }

    private Button btnSetFilter;

    private void initFilter(View view) {

        layout_filter = (ViewGroup) view.findViewById(R.id.layout_filter);
        layout_filter.setVisibility(View.GONE);
        cbFilter = (CheckBox) view.findViewById(R.id.cbFilter);
        cbFilter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                layout_filter.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            }
        });

        //sxt add start 隐藏
        cbFilter.setVisibility(View.INVISIBLE);
        //sxt add end

        final EditText etLen = (EditText) view.findViewById(R.id.etLen);
        final EditText etPtr = (EditText) view.findViewById(R.id.etPtr);
        final EditText etData = (EditText) view.findViewById(R.id.etData);
        final RadioButton rbEPC = (RadioButton) view.findViewById(R.id.rbEPC);
        final RadioButton rbTID = (RadioButton) view.findViewById(R.id.rbTID);
        final RadioButton rbUser = (RadioButton) view.findViewById(R.id.rbUser);
        btnSetFilter = (Button) view.findViewById(R.id.btSet);

        btnSetFilter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                int filterBank = RFIDWithUHFUART.Bank_EPC;
                if (rbEPC.isChecked()) {
                    filterBank = RFIDWithUHFUART.Bank_EPC;
                } else if (rbTID.isChecked()) {
                    filterBank = RFIDWithUHFUART.Bank_TID;
                } else if (rbUser.isChecked()) {
                    filterBank = RFIDWithUHFUART.Bank_USER;
                }
                if (etLen.getText().toString() == null || etLen.getText().toString().isEmpty()) {
                    UIHelper.ToastMessage(mContext, "数据长度不能为空");
                    return;
                }
                if (etPtr.getText().toString() == null || etPtr.getText().toString().isEmpty()) {
                    UIHelper.ToastMessage(mContext, "起始地址不能为空");
                    return;
                }
                int ptr = StringUtils.toInt(etPtr.getText().toString(), 0);
                int len = StringUtils.toInt(etLen.getText().toString(), 0);
                String data = etData.getText().toString().trim();
                if (len > 0) {
                    String rex = "[\\da-fA-F]*"; //匹配正则表达式，数据为十六进制格式
                    if (data == null || data.isEmpty() || !data.matches(rex)) {
                        UIHelper.ToastMessage(mContext, "过滤的数据必须是十六进制数据");
                        return;
                    }

                    if (mContext.mReader.setFilter(filterBank, ptr, len, data)) {
                        UIHelper.ToastMessage(mContext, R.string.uhf_msg_set_filter_succ);
                    } else {
                        UIHelper.ToastMessage(mContext, R.string.uhf_msg_set_filter_fail);
                    }
                } else {
                    //禁用过滤
                    String dataStr = "";
                    if (mContext.mReader.setFilter(RFIDWithUHFUART.Bank_EPC, 0, 0, dataStr)
                            && mContext.mReader.setFilter(RFIDWithUHFUART.Bank_TID, 0, 0, dataStr)
                            && mContext.mReader.setFilter(RFIDWithUHFUART.Bank_USER, 0, 0, dataStr)) {
                        UIHelper.ToastMessage(mContext, R.string.msg_disable_succ);
                    } else {
                        UIHelper.ToastMessage(mContext, R.string.msg_disable_fail);
                    }
                }
                cbFilter.setChecked(false);

            }
        });
        CheckBox cb_filter = (CheckBox) view.findViewById(R.id.cb_filter);
        rbEPC.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rbEPC.isChecked()) {
                    etPtr.setText("32");
                }
            }
        });
        rbTID.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rbTID.isChecked()) {
                    etPtr.setText("0");
                }
            }
        });
        rbUser.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rbUser.isChecked()) {
                    etPtr.setText("0");
                }
            }
        });
    }

    private void initEPCTamperAlarm(View view) {
        cbEPC_Tam = (CheckBox) view.findViewById(R.id.cbEPC_Tam);
        cbEPC_Tam.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //mContext.mReader.setEPCAndTamperAlarmMode();
                } else {
                    mContext.mReader.setEPCMode();
                }
            }
        });
    }

    @Override
    public void onPause() {
        Log.i(TAG, "UHFReadTagFragment.onPause");
        super.onPause();

        // 停止识别
        stopInventory();
    }

    /**
     * 添加数据到列表中
     *
     * @param
     */
    private void addDataToList(String epc,String epcAndTidUser, String rssi) {
        if (StringUtils.isNotEmpty(epc)) {
            int index = checkIsExist(epc);
            map = new HashMap<String, String>();
            map.put(TAG_EPC, epc);
            map.put(TAG_EPC_TID, epcAndTidUser);
            map.put(TAG_COUNT, String.valueOf(1));
            map.put(TAG_RSSI, rssi);

            String tempCode = "";
            String tempName = "";
            if (epc.length()>6) {
                tempCode = epc.substring(0,6);
            }


            if (index == -1) {
                //sxt add start
                if (tempCode.equals("200000")) {
                    tempName = epc.substring(19,24);

                    map.put(TAG_PRO_CODE, "");      //号型代码
                    map.put(TAG_PRO_NAME, "托盘 " + tempName);      //物资名称
                    map.put(TAG_PRO_MODEL, "");    //物资号型
                    map.put(TAG_PRO_COUNT, "");    //装箱数量
                    map.put(TAG_PRO_BATCH, "");    //批次编号
                    map.put(TAG_PRO_TYPE, "");      //种类
                    map.put(TAG_PRO_CREATE_TIME, "");         //生产日期
                    map.put(TAG_PRO_CREATE_COMPANY, "");   //生产企业
                    map.put(TAG_PRO_BOX, "");        //箱
                } else {
                    List<String> list = new ArrayList<String>();
                    list.add(epc);
                    List<EpcData> ret1 = EpcFormat.formatEpc(list);
                    if (ret1.size()>0){
                        map.put(TAG_PRO_CODE, ret1.get(0).get_code());      //号型代码
                        map.put(TAG_PRO_NAME, ret1.get(0).get_name());      //物资名称
                        map.put(TAG_PRO_MODEL, ret1.get(0).get_model());    //物资号型
                        map.put(TAG_PRO_COUNT, ret1.get(0).get_count());    //装箱数量
                        map.put(TAG_PRO_BATCH, ret1.get(0).get_batch());    //批次编号
                        map.put(TAG_PRO_TYPE, ret1.get(0).get_type());      //种类
                        map.put(TAG_PRO_CREATE_TIME, ret1.get(0).get_createTime());         //生产日期
                        map.put(TAG_PRO_CREATE_COMPANY, ret1.get(0).get_createCompany());   //生产企业
                        map.put(TAG_PRO_BOX, ret1.get(0).get_box());        //箱
                    }
                }
                //sxt add end

                mContext.tagList.add(map);
                tempDatas.add(epc);
                tv_count.setText(String.valueOf(adapter.getCount()));
            } else {
                int tagCount = Integer.parseInt(mContext.tagList.get(index).get(TAG_COUNT), 10) + 1;
                map.put(TAG_COUNT, String.valueOf(tagCount));
                map.put(TAG_EPC_TID, epcAndTidUser);

                //sxt add start
                map.put(TAG_PRO_CODE, mContext.tagList.get(index).get(TAG_PRO_CODE));      //号型代码
                map.put(TAG_PRO_NAME, mContext.tagList.get(index).get(TAG_PRO_NAME));      //物资名称
                map.put(TAG_PRO_MODEL, mContext.tagList.get(index).get(TAG_PRO_MODEL));    //物资号型
                map.put(TAG_PRO_COUNT, mContext.tagList.get(index).get(TAG_PRO_COUNT));    //装箱数量
                map.put(TAG_PRO_BATCH, mContext.tagList.get(index).get(TAG_PRO_BATCH));    //批次编号
                map.put(TAG_PRO_TYPE, mContext.tagList.get(index).get(TAG_PRO_TYPE));      //种类
                map.put(TAG_PRO_CREATE_TIME, mContext.tagList.get(index).get(TAG_PRO_CREATE_TIME));         //生产日期
                map.put(TAG_PRO_CREATE_COMPANY, mContext.tagList.get(index).get(TAG_PRO_CREATE_COMPANY));   //生产企业
                map.put(TAG_PRO_BOX, mContext.tagList.get(index).get(TAG_PRO_BOX));       //箱
                //sxt add end

                mContext.tagList.set(index, map);
            }
            tv_total.setText(String.valueOf(++total));
            adapter.notifyDataSetChanged();

            //----------
            mContext.uhfInfo.setTempDatas(tempDatas);
            mContext.uhfInfo.setTagList(mContext.tagList);
            mContext.uhfInfo.setCount(total);
            mContext.uhfInfo.setTagNumber(adapter.getCount());
        }
    }

    public class BtClearClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            clearData();
            selectItem=-1;
            mContext.uhfInfo=new UhfInfo();
        }
    }

    private void clearData() {
        tv_count.setText("0");
        tv_total.setText("0");
        tvTime.setText("0s");
        total = 0;
        mContext.tagList.clear();
        tempDatas.clear();

        adapter.notifyDataSetChanged();
    }

    public class RgInventoryCheckedListener implements OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (checkedId == RbInventorySingle.getId()) {
                // 单步识别
                inventoryFlag = 0;
                cbFilter.setChecked(false);
                cbFilter.setVisibility(View.INVISIBLE);
            } else if (checkedId == RbInventoryLoop.getId()) {
                // 单标签循环识别
                inventoryFlag = 1;
                cbFilter.setVisibility(View.VISIBLE);
            }
        }
    }


    public class BtInventoryClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            readTag();
        }
    }

    private void readTag() {
        cbFilter.setChecked(false);
        if (BtInventory.getText().equals(mContext.getString(R.string.btInventory))) {// 识别标签
            switch (inventoryFlag) {
                case 0:// 单步
                    time = System.currentTimeMillis();
                    UHFTAGInfo uhftagInfo = mContext.mReader.inventorySingleTag();
                    if (uhftagInfo != null) {
                        String tid = uhftagInfo.getTid();
                        String epc = uhftagInfo.getEPC();
                        String user=uhftagInfo.getUser();
                        addDataToList(epc,mergeTidEpc(tid, epc, user), uhftagInfo.getRssi());
                        setTotalTime();
                        mContext.playSound(1);
                    } else {
                        UIHelper.ToastMessage(mContext, R.string.uhf_msg_inventory_fail);
//					mContext.playSound(2);
                    }
                    break;
                case 1:// 单标签循环
                    if (mContext.mReader.startInventoryTag()) {
                        BtInventory.setText(mContext.getString(R.string.title_stop_Inventory));
                        loopFlag = true;
                        setViewEnabled(false);
                        time = System.currentTimeMillis();
                        new TagThread().start();
                    } else {
                        stopInventory();
                        UIHelper.ToastMessage(mContext, R.string.uhf_msg_inventory_open_fail);
//					mContext.playSound(2);
                    }
                    break;
                default:
                    break;
            }
        } else {// 停止识别
            stopInventory();
            setTotalTime();
        }
    }

    private void setTotalTime() {
        float useTime = (System.currentTimeMillis() - time) / 1000.0F;
        tvTime.setText(NumberTool.getPointDouble(1, useTime) + "s");
    }

    private void setViewEnabled(boolean enabled) {
        RbInventorySingle.setEnabled(enabled);
        RbInventoryLoop.setEnabled(enabled);
        cbFilter.setEnabled(enabled);
        btnSetFilter.setEnabled(enabled);
        BtClear.setEnabled(enabled);
        cbEPC_Tam.setEnabled(enabled);
    }

    /**
     * 停止识别
     */
    private void stopInventory() {
        if (loopFlag) {
            loopFlag = false;
            setViewEnabled(true);
            if (mContext.mReader.stopInventory()) {
                BtInventory.setText(mContext.getString(R.string.btInventory));
            } else {
                UIHelper.ToastMessage(mContext, R.string.uhf_msg_inventory_stop_fail);
            }
        }
    }

    /**
     * 判断EPC是否在列表中
     *
     * @param epc 索引
     * @return
     */
    public int checkIsExist(String epc) {
        if (StringUtils.isEmpty(epc)) {
            return -1;
        }
        return binarySearch(tempDatas, epc);
    }

    /**
     * 二分查找，找到该值在数组中的下标，否则为-1
     */
    static int binarySearch(List<String> array, String src) {
        int left = 0;
        int right = array.size() - 1;
        // 这里必须是 <=
        while (left <= right) {
            if (compareString(array.get(left), src)) {
                return left;
            } else if (left != right) {
                if (compareString(array.get(right), src))
                    return right;
            }
            left++;
            right--;
        }
        return -1;
    }

    static boolean compareString(String str1, String str2) {
        if (str1.length() != str2.length()) {
            return false;
        } else if (str1.hashCode() != str2.hashCode()) {
            return false;
        } else {
            char[] value1 = str1.toCharArray();
            char[] value2 = str2.toCharArray();
            int size = value1.length;
            for (int k = 0; k < size; k++) {
                if (value1[k] != value2[k]) {
                    return false;
                }
            }
            return true;
        }
    }

    class TagThread extends Thread {
        public void run() {
            UHFTAGInfo uhftagInfo;
            Message msg;
            while (loopFlag) {
                uhftagInfo = mContext.mReader.readTagFromBuffer();
                if (uhftagInfo != null) {
                    msg = handler.obtainMessage();
                    msg.obj = uhftagInfo;
                    handler.sendMessage(msg);
                    mContext.playSound(1);
                }
            }
        }
    }

    private String mergeTidEpc(String tid, String epc,String user) {
        String data="EPC:"+ epc;
        if (!TextUtils.isEmpty(tid) && !tid.equals("0000000000000000") && !tid.equals("000000000000000000000000")) {
            data+= "\nTID:" + tid ;
        }
        if(user!=null && user.length()>0) {
            data+="\nUSER:"+user;
        }
        return  data;
    }

    @Override
    public void myOnKeyDwon() {
        readTag();
    }


    //-----------------------------
    private int  selectItem=-1;
    public final class ViewHolder {
        public TextView tvEPCTID;
        public TextView tvTagCount;
        public TextView tvTagRssi;
        //sxt add start
        public TextView tvItemId;           //索引
        public TextView tvProCode;          //号型代码
        public TextView tvProName;          //物资名称
        public TextView tvProModel;         //物资号型
        public TextView tvProCount;         //装箱数量
        public TextView tvProBatch;         //批次编号
        public TextView tvProType;          //种类
        public TextView tvProCreateTime;    //生产日期
        public TextView tvProCreateCompany;     //生产企业
        public TextView tvProBox;               //箱
        //sxt add end
    }

    public class MyAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        public MyAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }
        public int getCount() {
            // TODO Auto-generated method stub
            return mContext.tagList.size();
        }
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return mContext.tagList.get(arg0);
        }
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return arg0;
        }
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.listtag_items, null);
                holder.tvEPCTID = (TextView) convertView.findViewById(R.id.TvTagUii);
                holder.tvTagCount = (TextView) convertView.findViewById(R.id.TvTagCount);
                holder.tvTagRssi = (TextView) convertView.findViewById(R.id.TvTagRssi);

                //sxt add start
                holder.tvItemId = (TextView) convertView.findViewById(R.id.TvItemId);   //找到布局
                holder.tvProCode = (TextView) convertView.findViewById(R.id.TvProCode);
                holder.tvProName = (TextView) convertView.findViewById(R.id.TvProName);
                holder.tvProModel = (TextView) convertView.findViewById(R.id.TvProModel);
                holder.tvProCount = (TextView) convertView.findViewById(R.id.TvProCount);
                holder.tvProBatch = (TextView) convertView.findViewById(R.id.TvProBatch);
                holder.tvProType = (TextView) convertView.findViewById(R.id.TvProType);
                holder.tvProCreateTime = (TextView) convertView.findViewById(R.id.TvProCreateTime);
                holder.tvProCreateCompany = (TextView) convertView.findViewById(R.id.TvProCreateCompany);
                holder.tvProBox = (TextView) convertView.findViewById(R.id.TvProBox);
                //sxt add end

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.tvEPCTID.setText((String) mContext.tagList.get(position).get(TAG_EPC_TID));
            holder.tvTagCount.setText((String) mContext.tagList.get(position).get(TAG_COUNT));
            holder.tvTagRssi.setText((String) mContext.tagList.get(position).get(TAG_RSSI));

            //sxt add start
            holder.tvItemId.setText((position + 1) + "");

            holder.tvProCode.setText((String) mContext.tagList.get(position).get(TAG_PRO_CODE));
            holder.tvProName.setText((String) mContext.tagList.get(position).get(TAG_PRO_NAME));
            holder.tvProModel.setText((String) mContext.tagList.get(position).get(TAG_PRO_MODEL));
            holder.tvProCount.setText((String) mContext.tagList.get(position).get(TAG_PRO_COUNT));
            holder.tvProBatch.setText((String) mContext.tagList.get(position).get(TAG_PRO_BATCH));
            holder.tvProType.setText((String) mContext.tagList.get(position).get(TAG_PRO_TYPE));
            holder.tvProCreateTime.setText((String) mContext.tagList.get(position).get(TAG_PRO_CREATE_TIME));
            holder.tvProCreateCompany.setText((String) mContext.tagList.get(position).get(TAG_PRO_CREATE_COMPANY));
            holder.tvProBox.setText((String) mContext.tagList.get(position).get(TAG_PRO_BOX));
            //sxt add end

            if (position == selectItem) {
                convertView.setBackgroundColor(mContext.getResources().getColor(R.color.lfile_colorPrimary));
            }
            else {
                convertView.setBackgroundColor(Color.TRANSPARENT);
            }
            return convertView;
        }
        public  void setSelectItem(int select) {
            if(selectItem==select){
                selectItem=-1;
                mContext.uhfInfo.setSelectItem("");
                mContext.uhfInfo.setSelectIndex(selectItem);
            }else {
                selectItem = select;
                mContext.uhfInfo.setSelectItem(mContext.tagList.get(select).get(TAG_EPC));
                mContext.uhfInfo.setSelectIndex(selectItem);
            }

        }
    }




}
