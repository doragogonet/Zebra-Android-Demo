package com.example.ebskk.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.ebskk.R;
import com.example.ebskk.adapter.InStoreAdapter;
import com.example.ebskk.bean.InStore;
import com.example.ebskk.widget.VNRegionDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class InStoreFragment extends Fragment {

    private View fragmentInStoreView;
    private ListView listView;
    private List<InStore> inStores;
    private InStoreAdapter inStoreAdapter;
    private Context context;

    private Runnable runnable;
    private TextView tvTimeDisplay;               //显示日期时间
    private Handler handler = new Handler();
    private SharedPreferences sharedPreferences;
    private TextView tvNickName;                  //显示登录用户

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        fragmentInStoreView = inflater.inflate(R.layout.fragment_in_store, container, false);

        //初始化控件
        listView = fragmentInStoreView.findViewById(R.id.list);

        inStores = new ArrayList<>();
        InStore inStore = new InStore();
        inStore.setItemName("物品名０００００００１");
        inStore.setIndividualID("0000000001");
        inStore.setLocationID("A0110-1010-0210");
        inStore.setQuantity(1);
        inStores.add(inStore);

        InStore inStore2 = new InStore();
        inStore2.setItemName("物品名０００００００2");
        inStore2.setIndividualID("0000000002");
        inStore2.setLocationID("A0110-1010-0211");
        inStore2.setQuantity(99);
        inStores.add(inStore2);
        Log.i("abc","0000");
        // 创建Adapter
        inStoreAdapter = new InStoreAdapter(getActivity(), inStores);
        inStoreAdapter.setLocationIDClickListener(new LocationIDClick());

        Log.i("abc","onCreateView");

        // 设置Adapter
        listView.setAdapter(inStoreAdapter);


        //显示登录用户
        //获取sharedPreferences实例
        sharedPreferences = getActivity().getSharedPreferences("user",Context.MODE_PRIVATE);
        String nickName = sharedPreferences.getString("nickName",null);
        tvNickName = fragmentInStoreView.findViewById(R.id.nickName);
        tvNickName.setText(nickName);

        //显示日期时间
        tvTimeDisplay = fragmentInStoreView.findViewById(R.id.dateNow);
        runnable = new Runnable() {
            @Override
            public void run() {
                updateTime();
                handler.postDelayed(this, 1000); // 每秒更新一次
            }
        };
        // 开始更新时间
        handler.post(runnable);

        return fragmentInStoreView;
    }

    private void updateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String currentTime = sdf.format(new Date());
        tvTimeDisplay.setText(currentTime);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i("ccc", "onStop方法运行了...");

        // 停止更新时间
        handler.removeCallbacks(runnable);
    }

    private class LocationIDClick implements InStoreAdapter.LocationIDClickListener {

        @Override
        public void onLocationIDClick(InStore entity, int position) {

            VNRegionDialog dialog = new VNRegionDialog(getActivity());
            dialog.setRegionDialogResultListener(new VNRegionDialog.RegionDialogResultListener() {
                @Override
                public void onRegionDialogResultListener(String code, String name) {
                    System.out.println("打印选择地址："+code+"===="+name);

                    entity.setLocationID(code);
                    inStores.set(position,entity);
                    inStoreAdapter.notifyDataSetChanged();    //更新UI界面

                    Log.i("abc","onLocationIDClick"+entity.getLocationID());
                    Log.i("abc","position"+position);
                }
            });
            dialog.show();

//            textStore.setTag(entity.getCode());
//            textStore.setText(entity.getName());



        }
    }


}