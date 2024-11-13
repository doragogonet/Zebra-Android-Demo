package com.zebra.demo.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import com.alibaba.fastjson.JSON;
import com.zebra.demo.R;
import com.zebra.demo.base.Constants;
import com.zebra.demo.base.RFIDReaderManager;
import com.zebra.demo.bean.SettingData;
import com.zebra.rfid.api3.RFIDReader;

public class BaseActivity extends AppCompatActivity {

    protected RFIDReader reader;
    private ActionBar actionBar;
    private String[] memoryBankArr;
    private String[] actionArr;
    private String[] targetArr;
    private SharedPreferences sharedPreferences;

    private MenuItem imgMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences(Constants.ZEBRA_EBS_STORAGE, Context.MODE_PRIVATE);

        memoryBankArr = getResources().getStringArray(R.array.memory_bank_options);
        actionArr = getResources().getStringArray(R.array.action_options);
        targetArr = getResources().getStringArray(R.array.target_options);

        this.reader = RFIDReaderManager.getInstance().getReader();

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setIcon(R.drawable.ic_launcher);
        actionBar.setDisplayShowTitleEnabled(false);

    }

    protected void saveValue(String key, SettingData value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.ZEBRA_EBS_STORAGE_SETTING, JSON.toJSONString(value));
        editor.apply();
    }

    protected SettingData getValue(String key) {
        String value = sharedPreferences.getString(key , null);
        return JSON.parseObject(value, SettingData.class);
    }

    protected void setHomeAsUpEnabled(boolean isShow) {
        if (this.actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(isShow);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        //return super.onCreateOptionsMenu(menu);
        imgMenuItem = menu.findItem(R.id.homeConnectLogo);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public String getMemoryBankLabel(int index) {
        String label = "";

        if (index < memoryBankArr.length) {
            label = memoryBankArr[index];
        }

        return label;
    }

    public String getActionLabel(int index) {
        String label = "";

        if (index < actionArr.length) {
            label = actionArr[index];
        }

        return label;
    }

    public String getTargetLabel(int index) {
        String label = "";

        if (index < targetArr.length) {
            label = targetArr[index];
        }

        return label;
    }

    protected void changeRadioColor(int color) {
        //元々のカラー：-11447983
        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.radio_img_foreground, null);
        if (drawable instanceof VectorDrawable) {
            VectorDrawable vdc = (VectorDrawable) drawable;
            vdc.setTint(color);
        }
        imgMenuItem.setIcon(drawable);
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.homeConnectLogo:
//                // 当点击回首页的图标时，重定向到首页
//                Intent intent = new Intent(this, MainActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }
}
