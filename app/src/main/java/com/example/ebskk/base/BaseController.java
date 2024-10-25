package com.example.ebskk.base;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.example.ebskk.bean.AjaxResult;

public class BaseController {
    /**
     * 返回错误信息
     * @param msg
     * @return
     */
    protected AjaxResult onError(String msg) {return new AjaxResult(400,null,msg);}

    protected AjaxResult computeResult(String context, String msg) {
        AjaxResult ajaxResult = AjaxResult.newInstanceFromJSONString(context);
        if (ajaxResult == null) {
            ajaxResult = onError(msg);
        }
        return ajaxResult;
    }
}
