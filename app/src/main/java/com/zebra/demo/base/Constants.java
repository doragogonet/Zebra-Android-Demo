package com.zebra.demo.base;

import android.util.Log;

import com.zebra.demo.activity.MainActivity;
import com.zebra.demo.activity.RFIDHandler;
import com.zebra.demo.bean.SettingData;
import com.zebra.rfid.api3.Antennas;
import com.zebra.rfid.api3.ENUM_TRIGGER_MODE;
import com.zebra.rfid.api3.INVENTORY_STATE;
import com.zebra.rfid.api3.InvalidUsageException;
import com.zebra.rfid.api3.OperationFailureException;
import com.zebra.rfid.api3.RFIDReader;
import com.zebra.rfid.api3.SESSION;
import com.zebra.rfid.api3.SL_FLAG;
import com.zebra.rfid.api3.START_TRIGGER_TYPE;
import com.zebra.rfid.api3.STOP_TRIGGER_TYPE;
import com.zebra.rfid.api3.TAG_FIELD;
import com.zebra.rfid.api3.TagStorageSettings;
import com.zebra.rfid.api3.TriggerInfo;

public class Constants {
    public static final String CLIENT_TYPE = "android";
    public static final String SERVER_HOST = "http://192.168.1.10:8003";
//    public static final String API_SERVER_HOST = SERVER_HOST + "/api";
    public static final String API_SERVER_HOST = SERVER_HOST + "";

    public static final String HISTORY_DATA_KEY = "historyDataKey";

    public static final String ZEBRA_EBS_STORAGE = "zebra_ebs_storage";

    public static final String ZEBRA_EBS_STORAGE_SETTING = "zebra_ebs_storage_setting";


        final static String TAG = "ZEBRA-DEMO";

        //画面設定なしの時に使う。
        public static  void initRFIDConfig(RFIDReader reader) {
            Log.d("ConfigureReader", "ConfigureReader " + reader.getHostName());
            if ( reader != null && reader.isConnected()) {
                TriggerInfo triggerInfo = new TriggerInfo();
                triggerInfo.StartTrigger.setTriggerType(START_TRIGGER_TYPE.START_TRIGGER_TYPE_IMMEDIATE);
                triggerInfo.StopTrigger.setTriggerType(STOP_TRIGGER_TYPE.STOP_TRIGGER_TYPE_IMMEDIATE);
                try {
                    int MAX_POWER = 270;
                    // receive events from reader
                    //if (eventHandler == null)
                    //    eventHandler = new RFIDHandler.EventHandler();
                    //reader.Events.addEventsListener(eventHandler);
                    // HH event
                    reader.Events.setHandheldEvent(true);
                    // tag event with tag data
                    reader.Events.setTagReadEvent(true);
                    reader.Events.setAttachTagDataWithReadEvent(false);
                    // set trigger mode as rfid so scanner beam will not come
                    reader.Config.setTriggerMode(ENUM_TRIGGER_MODE.RFID_MODE, true);
                    // set start and stop triggers
                    reader.Config.setStartTrigger(triggerInfo.StartTrigger);
                    reader.Config.setStopTrigger(triggerInfo.StopTrigger);
                    // power levels are index based so maximum power supported get the last one
                    MAX_POWER = reader.ReaderCapabilities.getTransmitPowerLevelValues().length - 1;
                    // set antenna configurations
                    Antennas.AntennaRfConfig config = reader.Config.Antennas.getAntennaRfConfig(1);
                    config.setTransmitPowerIndex(MAX_POWER);
                    config.setrfModeTableIndex(0);
                    config.setTari(0);
                    reader.Config.Antennas.setAntennaRfConfig(1, config);
                    // Set the singulation control
                    Antennas.SingulationControl s1_singulationControl = reader.Config.Antennas.getSingulationControl(1);
                    s1_singulationControl.setSession(SESSION.SESSION_S0);
                    s1_singulationControl.Action.setInventoryState(INVENTORY_STATE.INVENTORY_STATE_A);
                    s1_singulationControl.Action.setSLFlag(SL_FLAG.SL_ALL);
                    reader.Config.Antennas.setSingulationControl(1, s1_singulationControl);
                    // delete any prefilters
                    reader.Actions.PreFilters.deleteAll();

                    TagStorageSettings tagStorageSettings = reader.Config.getTagStorageSettings();
                    TAG_FIELD[] tagField = new TAG_FIELD[6];
                    tagField[0] = TAG_FIELD.PC;
                    tagField[1] = TAG_FIELD.PEAK_RSSI;
                    tagField[2] = TAG_FIELD.TAG_SEEN_COUNT;
                    tagField[3] = TAG_FIELD.CRC;
                    tagField[4] = TAG_FIELD.LAST_SEEN_TIME_STAMP;
                    tagField[5] = TAG_FIELD.PHASE_INFO;
                    tagStorageSettings.setTagFields(tagField);
                    reader.Config.setTagStorageSettings(tagStorageSettings);
                    //
                } catch (InvalidUsageException | OperationFailureException e) {
                    Log.e("ConfigureReader","InvalidUsageException"+e.getMessage());
                }
            }
        }

        public  static void SettingData2Config(RFIDReader reader){

            SettingData settingData = RFIDHandler.settingData;
            if (reader == null || !reader.isConnected()) {
                return;
            }
            try {

                if(settingData.getBeeperVolume() != null) {
                    SettingsUtl.setBeeperVolume(reader,settingData.getBeeperVolume());
                }

                if(settingData.getPowerIndex() != null) {
                    SettingsUtl.setReaderPower(reader, Integer.parseInt(settingData.getPowerIndex()));
                }

                if(settingData.getStartTrigger() != null){
                    String sel;
                    switch (settingData.getStartTrigger()) {
                        case "START_TRIGGER_TYPE_IMMEDIATE":
                            sel = "IMMEDIATE";
                            break;
                        case "START_TRIGGER_TYPE_PERIODIC":
                            sel = "PERIODIC";
                            break;
                        case  "START_TRIGGER_TYPE_GPI":
                            sel ="GPT";
                            break;
                        case "START_TRIGGER_TYPE_HANDHELD":
                            sel = "HANDHELD";
                            break;
                        default:
                            sel = "IMMEDIATE";
			    break;
                    }
                    SettingsUtl.setTriggerStartType(reader,sel);
                }

                if(settingData.getStopTrigger() != null){

                    String sel;
                    switch (settingData.getStopTrigger()) {
                        case "START_TRIGGER_TYPE_IMMEDIATE":
                            sel = "IMMEDIATE";
                            break;
                        case "START_TRIGGER_TYPE_PERIODIC":
                            sel = "PERIODIC";
                            break;
                        case  "START_TRIGGER_TYPE_GPI":
                            sel ="GPT";
                            break;
                        case "START_TRIGGER_TYPE_HANDHELD":
                            sel = "HANDHELD";
                            break;
                        default:
                            sel = "IMMEDIATE";
			    break;
                    }

                    SettingsUtl.setTriggerStopType(reader,sel);
                }

                if(settingData.getHandheldEvent() == null){
                    reader.Events.setHandheldEvent(Boolean.valueOf(settingData.getHandheldEvent()));
                }
                if(settingData.getTagReadEvent() == null){
                    reader.Events.setTagReadEvent(Boolean.valueOf(settingData.getTagReadEvent()));
                }
                if(settingData.getAttachTagDataWithReadEvent() == null){
                    reader.Events.setAttachTagDataWithReadEvent(Boolean.valueOf(settingData.getAttachTagDataWithReadEvent()));
                }

                if(settingData.getReaderDisconnectEvent() == null){
                    reader.Events.setReaderDisconnectEvent(Boolean.valueOf(settingData.getReaderDisconnectEvent()));
                }

                if(settingData.getInfoEvent() == null) {
                    reader.Events.setInfoEvent(Boolean.valueOf(settingData.getInfoEvent() ));
                }
                if(settingData.getCradleEvent() == null){
                    reader.Events.setCradleEvent(Boolean.valueOf(settingData.getCradleEvent()));
                }

                if(settingData.getBatteryEvent() == null){
                    reader.Events.setBatteryEvent(Boolean.valueOf(settingData.getBatteryEvent()));
                }

            } catch (Exception ignored) {
                ignored.printStackTrace();
                Log.d(TAG,ignored.getMessage());
            }




        }


}
