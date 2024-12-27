package com.zebra.demo.activity;

import com.zebra.rfid.api3.RFIDReader;
import com.zebra.rfid.api3.TagData;

public   interface ResponseHandlerInterface {
    public void handleTagdata(TagData[] tagData);
    public void handleTagLocationInfo(TagData[] tagData);

}
