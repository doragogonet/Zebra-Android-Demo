package com.zebra.demo.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.zebra.demo.R;
import com.zebra.demo.base.RFIDReaderManager;
import com.zebra.rfid.api3.RFIDReader;

public class BaseActivity extends AppCompatActivity {

    protected RFIDReader reader;
    private ActionBar actionBar;

    private String[] memoryBankArr;
    private String[] actionArr;
    private String[] targetArr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.homeLogo:
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
