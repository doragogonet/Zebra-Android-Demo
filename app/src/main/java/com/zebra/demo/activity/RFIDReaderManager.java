package com.zebra.demo.activity;

import com.zebra.rfid.api3.RFIDReader;

public class RFIDReaderManager {
    private static RFIDReaderManager instance;
    private RFIDReader reader;

    // プライベートコンストラクタ
    private RFIDReaderManager() {}

    // シングルトンインスタンスの取得
    public static synchronized RFIDReaderManager getInstance() {
        if (instance == null) {
            instance = new RFIDReaderManager();
        }
        return instance;
    }

    // リーダーの設定
    public void setReader(RFIDReader reader) {
        this.reader = reader;
    }

    // リーダーの取得
    public RFIDReader getReader() {
        return reader;
    }
}
