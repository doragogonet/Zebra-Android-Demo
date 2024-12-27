package com.zebra.demo.base;

import com.zebra.rfid.api3.Readers;
import com.zebra.rfid.api3.RfidEventsListener;
import com.zebra.rfid.api3.TagData;

public interface ResponseHandlerInterface  {
    public void handleTagdata(TagData[] tagData);

}
