package com.example.ebskk.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.example.ebskk.R;
import com.example.ebskk.fragment.InStoreFragment;
import com.example.ebskk.fragment.OutStoreFragment;
import com.example.ebskk.widget.VNRegionDialog;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends BaseTabFragmentActivity {
    List<Fragment> list;

    BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        bottomNavigation = findViewById(R.id.bottomNavigation);

        // 隐藏标题栏
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏状态栏
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        list = new ArrayList<>();
        list.add(new InStoreFragment());
        list.add(new OutStoreFragment());

        //显示第一个Fragment
        showFragment(list.get(0));

        //底部导航栏的切换事件
        bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.in_store:
                        showFragment(list.get(0));
                        break;
                    case R.id.out_store:
                        showFragment(list.get(1));
                        break;
                }
                return true;
            }
        });
    }

    private void showFragment(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container,fragment);
        ft.commit();
    }
}