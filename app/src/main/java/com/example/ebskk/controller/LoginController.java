package com.example.ebskk.controller;

import android.app.Activity;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.example.ebskk.base.BaseController;
import com.example.ebskk.bean.AjaxResult;
import com.example.ebskk.bean.LoginBody;
import com.example.ebskk.core.OkHttpUtils;

public abstract class LoginController extends BaseController {


    /***
     * 用户登录
     * @param activity
     * @param loginBody
     */
    public void login(Activity activity, LoginBody loginBody) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                String res = OkHttpUtils.builder()
                        .url("/login")
                        .addParam("username", loginBody.getUsername()).addParam("password", loginBody.getPassword())
                        .post(true)
                        .sync();
                return res;
            }

            @Override
            protected void onPostExecute(String s) {
                String msg = "ログインに失敗しました";

                if (TextUtils.isEmpty(s)) {
                    onResult(onError(msg));
                    return;
                }
                if (activity != null) {
                    //在主线程中可以更新UI
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            onResult(computeResult(s, msg));
                        }
                    });
                } else {
                    onResult(computeResult(s, msg));
                }
            }
        }.execute();
    }

    public void getInfo(Activity activity, String token) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                String res = OkHttpUtils.builder()
                        .url("/getInfo")
                        .addHeader("Authorization","Bearer " + token)
                        .get()
                        .sync();
                Log.i("ccc","rec"+res);
                return res;
            }

            @Override
            protected void onPostExecute(String s) {
                String msg = "ログインに失敗しました";

                if (TextUtils.isEmpty(s)) {
                    onResult(onError(msg));
                    return;
                }
                if (activity!=null){
                    //在主线程中可以更新UI
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            onResult(computeResult(s,msg));
                        }
                    });
                } else {
                    onResult(computeResult(s,msg));
                }
            }
        }.execute();
    }

    public abstract void onResult(AjaxResult result);

}
