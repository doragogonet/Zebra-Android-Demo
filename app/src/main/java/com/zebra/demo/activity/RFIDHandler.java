package com.zebra.demo.activity;


import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

import com.zebra.demo.bean.SettingData;
import com.zebra.rfid.api3.ENUM_TRANSPORT;
import com.zebra.rfid.api3.InvalidUsageException;
import com.zebra.rfid.api3.OperationFailureException;
import com.zebra.rfid.api3.RFIDReader;
import com.zebra.rfid.api3.ReaderDevice;
import com.zebra.rfid.api3.Readers;

import java.util.ArrayList;
//extends BaseActivity
public class RFIDHandler  implements Readers.RFIDReaderEventHandler{

    final static String TAG = "ZEBRA-DEMO";
    // RFID Reader
    private  static Readers readers;
    private  static ArrayList<ReaderDevice> availableRFIDReaderList;
    private  static ReaderDevice readerDevice;
    public   static RFIDReader reader;
    public   static SettingData settingData ;
   // private  EventHandler eventHandler;
    // UI and context
    private  SettingsActivity context;

    // In case of RFD8500 change reader name with intended device below from list of paired RFD8500
    String readerName = "RFD40+_24230525100752";


    public RFIDHandler (SettingsActivity settingsActivity)
    {
        context = settingsActivity;
        if( settingData == null)
            settingData = new SettingData();
    }


    protected void onCleared() {
        disconnect();
    }


    private boolean isReaderConnected() {
        if (reader != null && reader.isConnected())
            return true;
        else {
            Log.d(TAG, "reader is not connected");
            return false;
        }
    }

    //
    //  Activity life cycle behavior
    //


     String onResume() {
        return connect();
    }

     void onPause() {
        disconnect();

    }

     void onDestroy() {
        dispose();
    }

    //
    // RFID SDK
    //
    public void InitSDK() {
        Log.d(TAG, "InitSDK");
        if (readers == null) {

            new CreateInstanceTask().execute();

        } else
            connectReader();
    }

    // Enumerates SDK based on host device
    private class CreateInstanceTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            Log.d(TAG, "CreateInstanceTask");
            // Based on support available on host device choose the reader type
            InvalidUsageException invalidUsageException = null;
            readers = new Readers(context, ENUM_TRANSPORT.ALL);
            try {
                availableRFIDReaderList = readers.GetAvailableRFIDReaderList();
            } catch (InvalidUsageException e) {
                e.printStackTrace();
            }
            if (invalidUsageException != null) {
                readers.Dispose();
                readers = null;
                if (readers == null) {
                    readers = new Readers(context, ENUM_TRANSPORT.BLUETOOTH);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            connectReader();
        }
    }

    private synchronized void connectReader(){
        if(!isReaderConnected()){
            new ConnectionTask().execute();
        }
    }

    private class ConnectionTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            Log.d(TAG, "ConnectionTask");
            GetAvailableReader();
            if (reader != null) {
                return connect();
            }
            return "Failed to find or connect reader";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            context.statusTextViewRFID.setText(result);
        }
    }

    private synchronized void GetAvailableReader() {
        Log.d(TAG, "GetAvailableReader");
        if (readers != null) {
            readers.attach(this);
            try {
                if (readers.GetAvailableRFIDReaderList() != null) {
                    availableRFIDReaderList = readers.GetAvailableRFIDReaderList();
                    if (!availableRFIDReaderList.isEmpty()) {
                        // if single reader is available then connect it
                        if (availableRFIDReaderList.size() == 1) {
                            readerDevice = availableRFIDReaderList.get(0);
                            reader = readerDevice.getRFIDReader();
                        } else {
                            // search reader specified by name
                            for (ReaderDevice device : availableRFIDReaderList) {
                                if (device.getName().equals(readerName)) {
                                    readerDevice = device;
                                    reader = readerDevice.getRFIDReader();
                                }
                            }
                        }
                    }
                }
            }catch (InvalidUsageException ignored){
                Log.e(TAG, "GetAvailableReader InvalidUsageException: "+ ignored.getMessage());
            }
        }
    }
    // handler for receiving reader appearance events
    @Override
    public void RFIDReaderAppeared(ReaderDevice readerDevice) {
        Log.d(TAG, "RFIDReaderAppeared " + readerDevice.getName());
        connectReader();
    }

    @Override
    public void RFIDReaderDisappeared(ReaderDevice readerDevice) {
        Log.d(TAG, "RFIDReaderDisappeared " + readerDevice.getName());
        if (readerDevice.getName().equals(reader.getHostName()))
            disconnect();
    }


    private synchronized String connect() {
        if (reader != null) {

            Log.d(TAG, "connect " + reader.getHostName());
            try {
                if (!reader.isConnected()) {
                    // Establish connection to the RFID Reader
                    reader.connect();
                }
                AsyncConnected();
                if (reader.isConnected()) {
                    return "Connected: " + reader.getHostName();
                }else{
                    return "NoConnected: " + reader.getHostName();
                }

        } catch (InvalidUsageException e) {
                Log.e(TAG,"InvalidUsageException"+ e.getMessage());
                return  e.getMessage();
            } catch (OperationFailureException e) {
                Log.d(TAG, "OperationFailureException " + e.getMessage());
                return e.getMessage();
            } catch (Exception e)  {
            // 例外発生有無に関わらず必ず最後におこなう処理
                return   e.getMessage();
            }

        }

        //画面復帰
        setBtnEnable();

        return "";
    }


    private synchronized void disconnect() {
        Log.d(TAG, "disconnect " + reader);
        try {
            if (reader != null && reader.isConnected()) {
                //機能保存
               // reader.Events.removeEventsListener(eventHandler);
                reader.disconnect();
                //デバイス終了
                dispose();
                settingData = null;
                reader = null;
                //画面初期する
                AsyncConnected();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public   void AsyncConnected() {
        if(reader != null && reader.isConnected()) {
            saveSetting();
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //画面表示
                    setViewValue();
                }
            });

            context.runOnUiThread(() ->context.changeRadioColor(Color.GREEN));
        }else {
            context.runOnUiThread(() ->context.statusTextViewRFID.setText("Disconnected"));
            context.runOnUiThread(() ->context.changeRadioColor(Color.DKGRAY));
        }
        setBtnEnable();

    }


    private synchronized void dispose() {
        try {
            if (readers != null) {
                readers.Dispose();
                readers = null;
            }
            if(settingData != null)
                settingData = null;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    synchronized void performInventory() {
        // check reader connection
        if (!isReaderConnected())
            return;
        try {
            reader.Actions.Inventory.perform();
        } catch (InvalidUsageException | OperationFailureException e) {
            e.printStackTrace();
        }
    }

    synchronized void stopInventory() {
        // check reader connection
        if (!isReaderConnected())
            return;
        try {
            reader.Actions.Inventory.stop();
        } catch (InvalidUsageException | OperationFailureException e) {
            e.printStackTrace();
        }
    }

    public  void saveSetting() {
        if (reader == null || !reader.isConnected() || settingData == null) {
            return;
        }
        try {
            String tmp;
            tmp = reader.Config.getBeeperVolume().toString();
            settingData.setBeeperVolume(tmp);
            tmp = String.valueOf(reader.Config.Antennas.getAntennaRfConfig(1).getTransmitPowerIndex());
            settingData.setPowerIndex(tmp);
            settingData.setStartTrigger(reader.Config.getStartTrigger().getTriggerType().toString());
            settingData.setStopTrigger(reader.Config.getStopTrigger().getTriggerType().toString());
            settingData.setHandheldEvent(String.valueOf(reader.Events.isHandheldEventSet()));
            settingData.setTagReadEvent(String.valueOf(reader.Events.isTagReadEventSet()));
            settingData.setAttachTagDataWithReadEvent(String.valueOf(reader.Events.isAttachTagDataWithReadEventSet()));
            settingData.setReaderDisconnectEvent(String.valueOf(reader.Events.isReaderDisconnectEventSet()));
            settingData.setInfoEvent(String.valueOf(reader.Events.isInfoEventSet()));
            settingData.setCradleEvent(String.valueOf(reader.Events.isCradleEventset()));
            settingData.setBatteryEvent(String.valueOf(reader.Events.isBatterySet()));
            settingData.setFirmwareUpdateEvent("false");
            settingData.setHeartBeatEvent("fales");

        } catch (Exception ignored) {
            ignored.printStackTrace();
        }

    }

    public void setBtnEnable() {
        boolean isConn;

        //メニュー更新の通知
        if(context.imgMenuItem == null ){
            context.invalidateOptionsMenu();
        }
        if (reader != null && reader.isConnected()) {
            context.changeRadioColor(Color.GREEN);
            isConn = true;
        }else{
            context.changeRadioColor(Color.DKGRAY);
            isConn = false;
        }

        context.runOnUiThread(() -> context.btnConnect.setEnabled(!isConn));
        context.runOnUiThread(() -> context.btnDisconnect.setEnabled(isConn));
        context.runOnUiThread(() -> context.spConnectionType.setEnabled(!isConn));
        context.runOnUiThread(() -> context.spBeeperVolume.setEnabled(isConn));
        context.runOnUiThread(() -> context.sbPower.setEnabled(isConn));
        context.runOnUiThread(() -> context.tvPower.setEnabled(isConn));
        context.runOnUiThread(() -> context.spStartTrigger.setEnabled(isConn));
        context.runOnUiThread(() -> context.spStopTrigger.setEnabled(isConn));
        context.runOnUiThread(() -> context.swTagReadEvent.setEnabled(isConn));     //sxt 20241231 add


        context.runOnUiThread(() -> context.swAttachTagDataWithReadEvent.setEnabled(isConn));
        context.runOnUiThread(() -> context.swReaderDisconnectEvent.setEnabled(isConn));
        context.runOnUiThread(() -> context.swInfoEvent.setEnabled(isConn));
        context.runOnUiThread(() -> context.swCradleEvent.setEnabled(isConn));
        context.runOnUiThread(() -> context.swBatteryEvent.setEnabled(isConn));
        context.runOnUiThread(() -> context.swFirmwareUpdateEvent.setEnabled(isConn));
        context.runOnUiThread(() -> context.swHeartBeatEvent.setEnabled(isConn));
        context.runOnUiThread(() -> context.swHandheldEvent.setEnabled(isConn));

    }

    //画面が戻る初期設定　
    public void setViewValue() {

                if (reader == null || !reader.isConnected()) {
                    return;
                }
                try {
                    if(settingData.getBeeperVolume() == null) {
                        settingData.setBeeperVolume(reader.Config.getBeeperVolume().toString());
                    }
                    int sel;
                    switch(settingData.getBeeperVolume()) {
                        case "HIGH_BEEP":
                            sel = 0;
                            break;
                        case "LOW_BEEP":
                            sel = 1;
                            break;
                        case "MEDIUM_BEEP":
                            sel = 2;
                            break;
                        case "QUIET_BEEP":
                            sel = 3;
                            break;
                        default:
                            sel = 1;
                            break;

                    }
                    context.spBeeperVolume.setEnabled(true);
                    context.spBeeperVolume.setSelection(sel);


                    if(settingData.getPowerIndex() == null) {
                        settingData.setPowerIndex(String.valueOf(reader.Config.Antennas.getAntennaRfConfig(1).getTransmitPowerIndex()));
                    }
                    context.sbPower.setProgress(Integer.parseInt(settingData.getPowerIndex()));
                    context.tvPower.setText(String.valueOf(Integer.parseInt(settingData.getPowerIndex()) / 10));

                    if(settingData.getStartTrigger() == null){
                        settingData.setStartTrigger(reader.Config.getStartTrigger().getTriggerType().toString());
                    }
                    switch(settingData.getStartTrigger()) {
                        case "START_TRIGGER_TYPE_IMMEDIATE":
                            sel = 0;
                            break;
                        case "START_TRIGGER_TYPE_PERIODIC":
                            sel =1;
                            break;
                        case "START_TRIGGER_TYPE_GPI":
                            sel =2;
                            break;
                        case "START_TRIGGER_TYPE_HANDHELD":
                            sel =3;
                            break;
                        default: // デフォルトの処理
                            sel = 0;
                            break;
                    }
                    context.spStartTrigger.setEnabled(true);
                    context.spStartTrigger.setSelection(sel);

                    if(settingData.getStopTrigger() == null){
                        settingData.setStopTrigger(reader.Config.getStopTrigger().getTriggerType().toString());
                    }
                    switch (settingData.getStopTrigger()) {
                        case "STOP_TRIGGER_TYPE_IMMEDIATE":
                            sel = 0;
                            break;
                        case "STOP_TRIGGER_TYPE_DURATION":
                            sel = 1;
                            break;
                        case "STOP_TRIGGER_TYPE_GPI_WITH_TIMEOUT":
                            sel = 2;
                            break;
                        case "STOP_TRIGGER_TYPE_TAG_OBSERVATION_WITH_TIMEOUT":
                            sel = 3;
                            break;
                        case "STOP_TRIGGER_TYPE_N_ATTEMPTS_WITH_TIMEOUT":
                            sel = 4;
                            break;
                        case "STOP_TRIGGER_TYPE_HANDHELD_WITH_TIMEOUT":
                            sel = 5;
                            break;
                        case "STOP_TRIGGER_TYPE_ACCESS_N_ATTEMPTS_WITH_TIMEOUT":
                            sel = 6;
                            break;
                        default: // デフォルトの処理
                            sel = 0;
                            break;
                    }
                    context.spStopTrigger.setEnabled(true);
                    context.spStopTrigger.setSelection(sel);

                    if(settingData.getHandheldEvent() == null){
                        settingData.setHandheldEvent(String.valueOf(reader.Events.isHandheldEventSet()));
                    }
                    context.swHandheldEvent.setChecked(Boolean.parseBoolean(settingData.getHandheldEvent()));

                    if(settingData.getTagReadEvent() == null){
                        settingData.setTagReadEvent(String.valueOf(reader.Events.isTagReadEventSet()));
                    }
                    context.swTagReadEvent.setChecked(Boolean.parseBoolean(settingData.getTagReadEvent()));

                    if(settingData.getAttachTagDataWithReadEvent() == null){
                        settingData.setAttachTagDataWithReadEvent(String.valueOf(reader.Events.isAttachTagDataWithReadEventSet()));
                    }
                    context.swAttachTagDataWithReadEvent.setChecked(Boolean.parseBoolean(settingData.getAttachTagDataWithReadEvent()));

                    if(settingData.getReaderDisconnectEvent() == null){
                        settingData.setReaderDisconnectEvent(String.valueOf(reader.Events.isReaderDisconnectEventSet()));
                    }
                    context.swReaderDisconnectEvent.setChecked(Boolean.parseBoolean(settingData.getReaderDisconnectEvent()));

                    if(settingData.getInfoEvent() == null) {
                        settingData.setInfoEvent(String.valueOf(reader.Events.isInfoEventSet()));
                    }
                    context.swInfoEvent.setChecked(Boolean.parseBoolean(settingData.getInfoEvent()));

                    if(settingData.getCradleEvent() == null){
                        settingData.setCradleEvent(String.valueOf(reader.Events.isCradleEventset()));
                    }
                    context.swCradleEvent.setChecked(Boolean.parseBoolean(settingData.getCradleEvent()));

                    if(settingData.getBatteryEvent() == null){
                        settingData.setBatteryEvent(String.valueOf(reader.Events.isBatterySet()));
                    }
                    context.swBatteryEvent.setChecked(Boolean.parseBoolean(settingData.getBatteryEvent()));

                    if(settingData.getFirmwareUpdateEvent() == null){
                        settingData.setFirmwareUpdateEvent("false");
                    }
                    context.swFirmwareUpdateEvent.setChecked(Boolean.parseBoolean(settingData.getFirmwareUpdateEvent()));

                    if(settingData.getHeartBeatEvent() == null){
                        settingData.setHeartBeatEvent("false");
                    }
                    context.swHeartBeatEvent.setChecked(Boolean.parseBoolean(settingData.getHeartBeatEvent()));
                } catch (Exception ignored) {
                    ignored.printStackTrace();
                    Log.d(TAG,ignored.getMessage());
                }
    }



}