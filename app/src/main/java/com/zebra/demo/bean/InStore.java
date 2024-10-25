package com.zebra.demo.bean;

public class InStore {

    private String itemID;          //物品ID
    private String itemName;        //物品名
    private String individualID;    //個別識別ID（RFIDタグID）
    private String locationID;      //保管場所ID（Locationsテーブルの外部キー）
    private int quantity;           //数量

    public String getItemID() {
        return itemID;
    }

    public void setItemID(String itemID) {
        this.itemID = itemID;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getIndividualID() {
        return individualID;
    }

    public void setIndividualID(String individualID) {
        this.individualID = individualID;
    }

    public String getLocationID() {
        return locationID;
    }

    public void setLocationID(String locationID) {
        this.locationID = locationID;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
