package com.zebra.demo.base;

import android.util.Log;

import com.zebra.rfid.api3.Antennas;
import com.zebra.rfid.api3.BEEPER_VOLUME;
import com.zebra.rfid.api3.RFIDReader;
import com.zebra.rfid.api3.START_TRIGGER_TYPE;
import com.zebra.rfid.api3.STOP_TRIGGER_TYPE;
import com.zebra.rfid.api3.StartTrigger;
import com.zebra.rfid.api3.StopTrigger;

//リーダ操作関数群
public class SettingsUtl {
    final static String TAG = "ZEBRA-DEMO";
    // パワーの設定
    public static void setReaderPower(RFIDReader reader,int powerLevel) {
        try {
            if (reader != null && reader.isConnected()) {
                //reader.Config.setTransmitPower(powerLevel);
                // power levels are index based so maximum power supported get the last one
                int MAX_POWER = reader.ReaderCapabilities.getTransmitPowerLevelValues().length - 1;
                // set antenna configurations
                Antennas.AntennaRfConfig config = reader.Config.Antennas.getAntennaRfConfig(1);
                if (MAX_POWER < powerLevel) {
                    powerLevel = MAX_POWER;
                }
                config.setTransmitPowerIndex(powerLevel);
                config.setrfModeTableIndex(0);
                config.setTari(0);
                reader.Config.Antennas.setAntennaRfConfig(1, config);
            }

        } catch (Exception e) {
            Log.d(TAG,"パワー設定に失敗しました: " + e.getMessage());
        }
    }

    // ブザー音量の設定
    public static void setBeeperVolume(RFIDReader reader,String selectedVolume) {
        try {
            if (reader != null && reader.isConnected()) {
                switch (selectedVolume) {
                    case "LOW_BEEP": // 低ボリュームに設定する処理
                        reader.Config.setBeeperVolume(BEEPER_VOLUME.LOW_BEEP);
                        break;
                    case "MEDIUM_BEEP": // 中ボリュームに設定する処理
                        reader.Config.setBeeperVolume(BEEPER_VOLUME.MEDIUM_BEEP);
                        break;
                    case "HIGH_BEEP": // 高ボリュームに設定する処理
                        reader.Config.setBeeperVolume(BEEPER_VOLUME.HIGH_BEEP);
                        break;
                    default: // デフォルトの処理
                        reader.Config.setBeeperVolume(BEEPER_VOLUME.QUIET_BEEP);
                        break;
                }
            }

        } catch (Exception e) {
            Log.d(TAG,"ブザー音量設定に失敗しました: " + e.getMessage());
        }
    }

    private static void setStartTrigger(RFIDReader reader,StartTrigger trigger) {
        try {
            if (reader != null && reader.isConnected()) {
                reader.Config.setStartTrigger(trigger);
            }
        } catch (Exception e) {
            Log.d(TAG,"スタートトリガ設定に失敗しました: " + e.getMessage());
        }
    }

    public static void setStopTrigger(RFIDReader reader,StopTrigger trigger) {

        try {
            if (reader != null && reader.isConnected()) {
                reader.Config.setStopTrigger(trigger);
            }
        } catch (Exception e) {
            Log.d(TAG,"ストップトリガ設定に失敗しました: " + e.getMessage());
        }
    }

    public  static void setInventoryEvent(RFIDReader reader,String eventFlag, boolean status) {

        try {
            if (reader != null && reader.isConnected()) {
                switch (eventFlag) {
                    case "HandheldEvent":
                        reader.Events.setHandheldEvent(status);
                        break;
                    case "TagReadEvent":
                        reader.Events.setTagReadEvent(status);
                        break;
                    case "AttachTagDataWithReadEvent":
                        reader.Events.setAttachTagDataWithReadEvent(status);
                        break;
                    case "ReaderDisconnectEvent":
                        reader.Events.setReaderDisconnectEvent(status);
                        break;
                    case "InfoEvent":
                        reader.Events.setInfoEvent(status);
                        break;
                    case "CradleEvent":
                        reader.Events.setCradleEvent(status);
                        break;
                    case "BatteryEvent":
                        reader.Events.setBatteryEvent(status);
                        break;
                    case "FirmwareUpdateEvent":
                        //reader.Events.setFirmwareUpdateEvent(status);
                        break;
                    case "HeartBeatEvent":
                        //reader.Events.setHeartBeatEvent(status);
                        break;
                }
            }
        } catch (Exception e) {
            Log.e(TAG,eventFlag + "設定に失敗しました: " + e.getMessage(),e);
        }
    }


    public static  StartTrigger getDefaultStartTrigger(RFIDReader reader) {
        StartTrigger tempStartTrigger = null;
        try {
            if (reader == null || !reader.isConnected()) {
                return null;
            }
            tempStartTrigger = reader.Config.getStartTrigger();
        } catch (Exception e){
            Log.e(TAG,"getDefaultStartTrigger err =",e);
        }
        return tempStartTrigger;
    }

    public  static  StopTrigger getDefaultStopTrigger(RFIDReader reader) {
        StopTrigger tempStopTrigger = null;
        try {
            if (reader == null || !reader.isConnected()) {
                return null;
            }
            tempStopTrigger = reader.Config.getStopTrigger();
        } catch (Exception e){
            Log.e(TAG,"getDefaultStart;Trigger err =",e);
        }
        return tempStopTrigger;
    }

    public static  void setTriggerStopType(RFIDReader reader, String selectedValue){
        StopTrigger tempStopTrigger = getDefaultStopTrigger(reader);
        if (tempStopTrigger == null) {
            return;
        }
        switch (selectedValue) {
            case "IMMEDIATE":
                tempStopTrigger.setTriggerType(STOP_TRIGGER_TYPE.STOP_TRIGGER_TYPE_IMMEDIATE);
                break;
            case "DURATION":
                tempStopTrigger.setTriggerType(STOP_TRIGGER_TYPE.STOP_TRIGGER_TYPE_DURATION);
                break;
            case "GPI":
                tempStopTrigger.setTriggerType(STOP_TRIGGER_TYPE.STOP_TRIGGER_TYPE_GPI_WITH_TIMEOUT);
                break;
            case "TAG_OBSERVATION":
                tempStopTrigger.setTriggerType(STOP_TRIGGER_TYPE.STOP_TRIGGER_TYPE_TAG_OBSERVATION_WITH_TIMEOUT);
                break;
            case "N_ATTEMPTS":
                tempStopTrigger.setTriggerType(STOP_TRIGGER_TYPE.STOP_TRIGGER_TYPE_N_ATTEMPTS_WITH_TIMEOUT);
                break;
            case "HANDHELD":
                tempStopTrigger.setTriggerType(STOP_TRIGGER_TYPE.STOP_TRIGGER_TYPE_HANDHELD_WITH_TIMEOUT);
                break;
            default: // デフォルトの処理
                tempStopTrigger.setTriggerType(STOP_TRIGGER_TYPE.STOP_TRIGGER_TYPE_IMMEDIATE);
                break;
        }
        setStopTrigger(reader,tempStopTrigger);
    }


    public static  void setTriggerStartType(RFIDReader reader, String selectedValue){

        StartTrigger tempStartTrigger = getDefaultStartTrigger(reader);
        if (tempStartTrigger == null) {
            return;
        }
        switch (selectedValue) {
            case "IMMEDIATE":
                tempStartTrigger.setTriggerType(START_TRIGGER_TYPE.START_TRIGGER_TYPE_IMMEDIATE);
                break;
            case "PERIODIC":
                tempStartTrigger.setTriggerType(START_TRIGGER_TYPE.START_TRIGGER_TYPE_PERIODIC);
                break;
            case "GPI":
                tempStartTrigger.setTriggerType(START_TRIGGER_TYPE.START_TRIGGER_TYPE_GPI);
                break;
            case "HANDHELD":
                tempStartTrigger.setTriggerType(START_TRIGGER_TYPE.START_TRIGGER_TYPE_HANDHELD);
                break;
            default: // デフォルトの処理
                tempStartTrigger.setTriggerType(START_TRIGGER_TYPE.START_TRIGGER_TYPE_IMMEDIATE);
                break;
        }
        setStartTrigger(reader,tempStartTrigger);
    }


}
