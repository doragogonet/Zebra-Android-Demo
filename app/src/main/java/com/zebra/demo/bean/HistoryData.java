package com.zebra.demo.bean;

public class HistoryData {
    private String antennaID;
    private String memoryBankData;
    private String peakRSSI;
    private String tagSeenCount;
    private String tagID;
    private String TID;
    private String user;
    private String CRC;
    private String tagCurrentTime;
    private int memoryBankValue;
    private String PC;

    public String getTagCurrentTime() {
        return tagCurrentTime;
    }

    public void setTagCurrentTime(String tagCurrentTime) {
        this.tagCurrentTime = tagCurrentTime;
    }

    public int getMemoryBankValue() {
        return memoryBankValue;
    }

    public void setMemoryBankValue(int memoryBankValue) {
        this.memoryBankValue = memoryBankValue;
    }

    public String getAntennaID() {
        return antennaID;
    }

    public void setAntennaID(String antennaID) {
        this.antennaID = antennaID;
    }

    public String getMemoryBankData() {
        return memoryBankData;
    }

    public void setMemoryBankData(String memoryBankData) {
        this.memoryBankData = memoryBankData;
    }

    public String getPeakRSSI() {
        return peakRSSI;
    }

    public void setPeakRSSI(String peakRSSI) {
        this.peakRSSI = peakRSSI;
    }

    public String getTagSeenCount() {
        return tagSeenCount;
    }

    public void setTagSeenCount(String tagSeenCount) {
        this.tagSeenCount = tagSeenCount;
    }

    public String getTagID() {
        return tagID;
    }

    public void setTagID(String tagID) {
        this.tagID = tagID;
    }

    public String getTID() {
        return TID;
    }

    public void setTID(String TID) {
        this.TID = TID;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getCRC() {
        return CRC;
    }

    public void setCRC(String CRC) {
        this.CRC = CRC;
    }

    public String getPC() {
        return PC;
    }

    public void setPC(String PC) {
        this.PC = PC;
    }

}
