package com.zebra.demo.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;

import androidx.core.content.FileProvider;

import com.zebra.demo.R;
import com.zebra.demo.adapter.InventoryDataAdapter;
import com.zebra.demo.base.Constants;
import com.zebra.demo.bean.HistoryData;
import com.zebra.demo.tools.TxtFileOperator;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class InventoryHistoryActivity extends BaseActivity   {

    ListView LvTags;

    ImageButton imageButton;

    Button button;

    private ProgressBar progressBar;

    private List<HistoryData> tagList = new ArrayList<>();

    InventoryDataAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_history);

        LvTags = findViewById(R.id.LvTags);
        imageButton = findViewById(R.id.button);
//        button = findViewById(R.id.button2);
        progressBar = findViewById(R.id.progressBar);
        showLoading();

        this.tagList = TxtFileOperator.readJsonFromFile(getApplicationContext(),TxtFileOperator.HISTORY_RFID_FILE_NAME, HistoryData.class);

        //sxt 20241226 add start
        if (this.tagList == null || this.tagList.size() == 0) {
            imageButton.setEnabled(false);
        }
        /*this.tagList = new ArrayList<HistoryData>();
        for (int i = 1;i < 50;i++) {
            HistoryData data = new HistoryData();
            data.setAntennaID(String.valueOf(i));
            data.setPeakRSSI("-60");
            data.setTagID("12345678901234-" + i);
            this.tagList.add(data);
        }
		*/
        //sxt 20241226 add end

        adapter = new InventoryDataAdapter(this, this.tagList);
        LvTags.setAdapter(adapter);

        hideLoading();

        LvTags.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                adapter.notifyDataSetInvalidated();

                Intent intent = new Intent(InventoryHistoryActivity.this, DetailActivity.class);
                intent.putExtra(Constants.HISTORY_DATA_KEY,(Serializable) tagList.get(position));
                startActivity(intent);
            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InventoryHistoryActivity.this, InventoryHistoryActivity.class);
                startActivity(intent);
                shareHistoryFile();

            }
        });

//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(InventoryHistoryActivity.this, InventoryHistoryActivity.class);
//                startActivity(intent);
//                TxtFileOperator.openFile(getApplicationContext(), TxtFileOperator.HISTORY_RFID_FILE_NAME);
//
//                HistoryData data = new HistoryData();
//                data.setAntennaID("111");
//                data.setPeakRSSI("-60");
//                data.setTagID("123456789012345");
//                TxtFileOperator.writeFile(data, true);
//
//                TxtFileOperator.closeFile();
//
//            }
//        });

    }

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideLoading() {
        progressBar.setVisibility(View.GONE);
    }


    /**
     * RFID履歴ファイルを他APPと共有させる
     */
    private void shareHistoryFile() {
        File file = new File(getFilesDir(), TxtFileOperator.HISTORY_RFID_FILE_NAME);
        Uri fileUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".fileprovider", file);

        Intent intent = new Intent();
//        intent.setAction(Intent.ACTION_VIEW);
        intent.setAction(Intent.ACTION_SEND);
        intent.setDataAndType(fileUri, "text/plain");
        intent.putExtra(Intent.EXTRA_STREAM, fileUri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(intent, "Open with"));
    }

}
