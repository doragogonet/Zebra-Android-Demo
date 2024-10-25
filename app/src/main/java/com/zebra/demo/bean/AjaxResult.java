package com.zebra.demo.bean;

import com.alibaba.fastjson.JSON;

public class AjaxResult {

    private String msg;
    private int code;
    private Object data;
    private String token;
    private User user;

    public AjaxResult() {
    }

    public AjaxResult( int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public AjaxResult(int code,String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static AjaxResult newInstanceFromJSONString(String jsonString) {
        if (jsonString == null || jsonString.equals("")) return null;

        try {
            return JSON.parseObject(jsonString,AjaxResult.class);

        } catch (Exception e) {
            return null;
        }
    }

    public boolean isSuccess() {return code == 200;}

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
