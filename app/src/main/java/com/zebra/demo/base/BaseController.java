package com.zebra.demo.base;

import com.zebra.demo.bean.AjaxResult;

public class BaseController {

    protected AjaxResult onError(String msg) {return new AjaxResult(400,null,msg);}

    protected AjaxResult computeResult(String context, String msg) {
        AjaxResult ajaxResult = AjaxResult.newInstanceFromJSONString(context);
        if (ajaxResult == null) {
            ajaxResult = onError(msg);
        }
        return ajaxResult;
    }
}
