package com.zebra.demo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.zebra.demo.R;

public class MainActivity extends BaseActivity {

    private Button btnInventory, btnInventoryHistory, btnTagTracking, btnSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        super.setHomeAsUpEnabled(false);

        // ボタンの初期化
        btnInventory = findViewById(R.id.btnInventory);
        btnInventoryHistory = findViewById(R.id.btnInventoryHistory);
        btnTagTracking = findViewById(R.id.btnTagTracking);
        btnSettings = findViewById(R.id.btnSettings);

        // Inventoryボタンのクリックイベント
        btnInventory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, InventoryActivity.class);
                startActivity(intent);
            }
        });

        // Inventory履歴ボタンのクリックイベント
        btnInventoryHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, InventoryHistoryActivity.class);
                startActivity(intent);
            }
        });

        // タグ追跡ボタンのクリックイベント
        btnTagTracking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TagTrackingActivity.class);
                startActivity(intent);
            }
        });

        // 設定ボタンのクリックイベント
        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });
    }

}
