package com.zebra.demo.activity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.zebra.demo.R;
import com.zebra.demo.base.RFIDReaderManager;
import com.zebra.demo.view.VerticalProgressBar;
import com.zebra.rfid.api3.*;

/*********************************************************************************
Zebra RFID SDKのTagLocationing機能を利用して、タグの位置情報を基にタグの追跡を行う。
TagLocationingは、タグのRSSI値だけでなく、RFIDリーダーのビーム方向（方向性）も考慮
して、特定のタグの位置をより正確に特定することができます。
*********************************************************************************/

public class TagTrackingActivity extends BaseActivity {

    private EditText etEPCCode;
    private ProgressBar progressBar;
    private Button btnStartTracking, btnStopTracking;
    //private RFIDReader reader;
    private boolean isTracking = false;
    private String targetEPC;  // 指定されたEPCコード

    private VerticalProgressBar vpProgressBar;
    private ProgressBar verticalProgressBar;
    private int progressStatus = 0;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_tracking);

        // UI要素の初期化
        etEPCCode = findViewById(R.id.etEPCCode);
        //progressBar = findViewById(R.id.progressBar);
        btnStartTracking = findViewById(R.id.btnStartTracking);
        btnStopTracking = findViewById(R.id.btnStopTracking);

        vpProgressBar = (VerticalProgressBar) findViewById(R.id.vp_progress);
        //verticalProgressBar = findViewById(R.id.verticalProgressBar);

        // SettingsActivityで接続されたリーダーを取得
        reader = RFIDReaderManager.getInstance().getReader();

        // リーダーが接続されているかの確認
        if (reader != null && reader.isConnected()) {
            // イベントリスナーの追加
            addReaderEventListener();
        } else {
            Toast.makeText(this, "リーダーが接続されていません", Toast.LENGTH_SHORT).show();
        }

        // 開始ボタンのクリックイベント
        btnStartTracking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTagLocationing();
            }
        });

        // 終了ボタンのクリックイベント
        btnStopTracking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopTagLocationing();
            }
        });

//        // プログレスバーの進行状況を更新するスレッド
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while (progressStatus < 100) {
//                    progressStatus++;
//                    handler.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            verticalProgressBar.setProgress(progressStatus);
//                        }
//                    });
//                    try {
//                        Thread.sleep(100); // 進行スピードを調整
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }).start();
        run();
    }

    private void run() {
        new Thread(){
            public void run() {
                try {
                    for (int i= 0;i<=100;i++) {
                        Thread.sleep(50);//休息50毫秒
                        vpProgressBar.setProgress(i);//更新进度条进度
                        //updateProgressBar(i);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            };
        }.start();
    }

    // リーダーイベントリスナーの追加
    private void addReaderEventListener() {
        try {
            reader.Events.addEventsListener(new RfidEventsListener() {
                @Override
                public void eventReadNotify(RfidReadEvents rfidReadEvents) {
                    handleTagLocationing(rfidReadEvents);  // タグロケーション処理
                }

                @Override
                public void eventStatusNotify(RfidStatusEvents rfidStatusEvents) {}
            });
        } catch (Exception e) {
            Toast.makeText(this, "イベントリスナーの追加に失敗しました: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // タグロケーションの開始
    private void startTagLocationing() {
        targetEPC = etEPCCode.getText().toString().trim();
        if (targetEPC.isEmpty()) {
            Toast.makeText(this, "EPCコードを入力してください", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            if (reader != null && reader.isConnected()) {
                // タグロケーション設定
                reader.Actions.TagLocationing.Perform(targetEPC, null, null);  // EPCコードを指定してタグロケーションを開始
                isTracking = true;
                Toast.makeText(this, "タグロケーションを開始しました", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "リーダーが接続されていません", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "タグロケーションの開始に失敗しました: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // タグロケーションの終了
    private void stopTagLocationing() {
        try {
            if (reader != null && reader.isConnected()) {
                reader.Actions.TagLocationing.Stop();  // タグロケーションを停止
                isTracking = false;
                Toast.makeText(this, "タグロケーションを終了しました", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "リーダーが接続されていません", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "タグロケーションの終了に失敗しました: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // タグロケーション処理
    private void handleTagLocationing(RfidReadEvents rfidReadEvents) {
        TagData[] tags = reader.Actions.getReadTags(100);
        if (tags != null) {
            for (TagData tag : tags) {
                // 指定されたEPCタグのみを処理
                if (isTracking && tag.getTagID().equals(targetEPC)) {
                    int locationConfidence = tag.LocationInfo.getRelativeDistance();  // 相対距離を取得
                    updateProgressBar(locationConfidence);  // プログレスバーを更新
                }
            }
        }
    }

    // プログレスバーの更新
    private void updateProgressBar(int locationConfidence) {
        // ロケーションの相対距離（0-100）を使ってプログレスバーを更新
        int progress = Math.min(100, Math.max(0, locationConfidence));
        runOnUiThread(() -> progressBar.setProgress(progress));
    }
}
