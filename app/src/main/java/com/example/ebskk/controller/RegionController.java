package com.example.ebskk.controller;

import android.app.Activity;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.example.ebskk.base.BaseController;
import com.example.ebskk.bean.AjaxResult;
import com.example.ebskk.bean.LoginBody;
import com.example.ebskk.bean.MgRegion;
import com.example.ebskk.core.OkHttpUtils;

import java.util.ArrayList;
import java.util.List;

public abstract class RegionController extends BaseController {


    /***
     * 用户登录
     * @param activity
     * @param code
     */
    public void regionList(Activity activity, String code)
    {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
//                String res = OkHttpUtils.builder()
//                            .url("/login")
//                            .addParam("username", loginBody.getUsername()).addParam("password", loginBody.getPassword())
//                            .post(true)
//                            .sync();
//                return res;
                Log.i("abc","doInBackground");
                return "[{'code':'1','name':'a'},{'code':'2','name':'b'}]";
            }

            @Override
            protected void onPostExecute(String s) {
                String msg = "登录失败";
                Log.i("abc","onPostExecute" + s);

                if (TextUtils.isEmpty(s)) {
                    onResult(onError(msg));
                    return;
                }
                if (activity!=null){
                    //在主线程中可以更新UI
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.i("abc","code:" + code);
                            AjaxResult result = computeResult(s,msg);
                            result.setCode(200);
                            if (code.equals("")) {
                                result.setData(getData(null,null));
                            } else {
                                String[] temp = code.split("-");
                                if (temp.length==1) {
                                    result.setData(getData(temp[0],null));
                                } else {
                                    result.setData(getData(temp[0],temp[1]));
                                }
                            }
                            onResult(result);

                        }
                    });
                } else {
                    Log.i("abc","code:1" + code);
                    AjaxResult result = computeResult(s,msg);
                    result.setCode(200);
                    if (code.equals("")) {
                        result.setData(getData(null,null));
                    } else {
                        String[] temp = code.split("-");
                        if (temp.length==1) {
                            result.setData(getData(temp[0],null));
                        } else {
                            result.setData(getData(temp[0],temp[1]));
                        }
                    }

                    onResult(result);
                }
            }
        }.execute();
    }

    private List<MgRegion> getData(String store, String rack) {
        if (store == null) {
            List<MgRegion> temp = new ArrayList<>();
            MgRegion mgRegion = new MgRegion();
            mgRegion.setCode("A0001");
            mgRegion.setName("A0001");
            temp.add(mgRegion);

            MgRegion mgRegion2 = new MgRegion();
            mgRegion2.setCode("A0002");
            mgRegion2.setName("A0002");
            temp.add(mgRegion2);

            MgRegion mgRegion3 = new MgRegion();
            mgRegion3.setCode("A0003");
            mgRegion3.setName("A0003");
            temp.add(mgRegion3);

            MgRegion mgRegion4 = new MgRegion();
            mgRegion4.setCode("A0004");
            mgRegion4.setName("A0004");
            temp.add(mgRegion4);

            return temp;
        }

        if (rack == null) {
            List<MgRegion> temp = new ArrayList<>();
            MgRegion mgRegion = new MgRegion();
            mgRegion.setCode(store + "-1010");
            mgRegion.setName("1010");
            temp.add(mgRegion);

            MgRegion mgRegion2 = new MgRegion();
            mgRegion2.setCode(store + "-1011");
            mgRegion2.setName("1011");
            temp.add(mgRegion2);

            MgRegion mgRegion3 = new MgRegion();
            mgRegion3.setCode(store + "-1012");
            mgRegion3.setName("1012");
            temp.add(mgRegion3);

            MgRegion mgRegion4 = new MgRegion();
            mgRegion4.setCode(store + "-1013");
            mgRegion4.setName("1013");
            temp.add(mgRegion4);

            return temp;
        }

        List<MgRegion> temp = new ArrayList<>();
        MgRegion mgRegion = new MgRegion();
        mgRegion.setCode(store + "-" + rack + "-0210");
        mgRegion.setName("0210");
        temp.add(mgRegion);

        MgRegion mgRegion2 = new MgRegion();
        mgRegion2.setCode(store + "-" + rack + "-0211");
        mgRegion2.setName("0211");
        temp.add(mgRegion2);

        MgRegion mgRegion3 = new MgRegion();
        mgRegion3.setCode(store + "-" + rack +  "-0212");
        mgRegion3.setName("0212");
        temp.add(mgRegion3);

        MgRegion mgRegion4 = new MgRegion();
        mgRegion4.setCode(store + "-" + rack +  "-0213");
        mgRegion4.setName("0213");
        temp.add(mgRegion4);

        Log.i("abc","temp"+JSON.toJSONString(temp));
        return temp;

    }

    public abstract void onResult(AjaxResult result);

}
