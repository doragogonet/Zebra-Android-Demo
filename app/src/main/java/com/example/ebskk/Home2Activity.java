package com.example.ebskk;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.example.ebskk.activity.BaseTabFragmentActivity;

public class Home2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setDarkStatusWhite(true);
        /* 隐藏标题栏 */
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_home2);
    }

}